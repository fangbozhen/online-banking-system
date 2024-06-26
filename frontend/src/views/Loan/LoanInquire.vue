<template>
  <div class="user-loan-history-page">
    <el-form :model="searchForm" label-width="120px" class="search-form">
      <el-form-item label="身份证号">
        <el-input v-model="searchForm.id_number" placeholder="请输入身份证号"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="searchForms">查询</el-button>
      </el-form-item>
    </el-form>
    <div class="loan-list-container" v-if="loans.length > 0">
      <el-table :data="loans" style="width: 100%" class="loan-table" header-align="center" align="center">
        <el-table-column prop="loan_id" label="贷款编号" width="180"></el-table-column>
        <el-table-column prop="borrow_id" label="借款人编号" width="180"></el-table-column>
        <el-table-column prop="card_id" label="转入卡编号" width="180"></el-table-column>
        <el-table-column prop="officer_id" label="审查员编号" width="180"></el-table-column>
        <el-table-column prop="amount" label="贷款金额" width="180"></el-table-column>
        <el-table-column prop="rate" label="贷款利率" width="180"></el-table-column>
        <el-table-column prop="term" label="贷款期限" width="180"></el-table-column>
        <el-table-column label="贷款状态" width="180">
          <template v-slot="scope">
            <span>{{ getStatusText(scope.row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="date_applied" label="申请日期" width="180"></el-table-column>
        <el-table-column prop="date_approved" label="审批日期" width="180"></el-table-column>
      </el-table>
      <el-pagination background
                     layout="prev, pager, next, total"
                     :total="totalLoans"
                     :page-size="pageSize"
                     @current-change="handlePageChange"
                     :current-page="currentPage">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { ElTable, ElTableColumn, ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus';
import axios from 'axios';
import Cookies from "js-cookie";

const axiosInstance = axios.create();
axiosInstance.interceptors.request.use(config => {
  const token = Cookies.get('token');
  if (token) {
    config.headers.Authorization = token;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

export default {
  components: {
    ElTable, ElTableColumn, ElForm, ElFormItem, ElInput, ElButton
  },
  setup() {
    const searchForm = ref({ id_number: '' });
    const loans = ref([]);
    const currentPage = ref(1);
    const pageSize = ref(10);
    const totalLoans = ref(0);

    const statusMap = {
      application: '申请中',
      repayment: '还款中',
      declined: '已拒绝',
      settled: '已还款',
      overdue: '已逾期',
    };

    const getStatusText = (status) => {
      return statusMap[status] || '未知状态';
    };

    const searchForms = async () => {
      try {
        const formResponse = await axiosInstance.get(`/search-forms/${searchForm.value.id_number}`, {
          params: {
            page: currentPage.value,
            pageSize: pageSize.value
          }
        });

        const formsData = formResponse.data.records;
        const formIds = formsData.map(form => form.form_id);

        // 获取对应的贷款信息
        const loanPromises = formIds.map(async formId => {
          try {
            const loanResponse = await axiosInstance.get(`/search-loans/${formId}`);
            if(loanResponse.data!== null)
            return loanResponse.data;
          } catch (loanError) {
            console.error(`获取贷款数据时发生错误: ${loanError}`);
            ElMessage.error('无法加载贷款数据。');
            return {};
          }
        });

        loans.value = (await Promise.all(loanPromises)).filter(loan => loan !== null && loan !== "");
        console.log(loans);
        totalLoans.value = loans.value.total;

        if (loans.value.length === 0) {
          ElMessage.error('没有找到相关贷款记录');
        }
      } catch (error) {
        console.error('获取表单记录时发生错误:', error);
        ElMessage.error('无法加载表单记录。');
      }
    };

    const handlePageChange = (page) => {
      currentPage.value = page;
      searchForms();
    };

    return {
      searchForm,
      loans,
      searchForms,
      currentPage,
      pageSize,
      totalLoans,
      handlePageChange,
      getStatusText
    };
  }
};
</script>

<style scoped>
.user-loan-history-page {
  padding: 20px;
  background-color: #f9f9f9;
}

.search-form {
  margin-bottom: 20px;
}

.loan-list-container {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.loan-table .el-table__header-wrapper {
  background-color: #409eff;
  color: #fff;
  font-weight: bold;
  font-size: 16px;
}

.loan-table .el-table__body-wrapper .el-table__row {
  font-size: 18px;
  font-weight: bold;
  height: 60px;
  border-bottom: 1px solid #ebeef5;
}

.loan-table .el-table__body-wrapper .el-table__row:hover {
  background-color: #f2f6fc;
}

.loan-table .el-table__cell {
  padding: 10px;
  font-weight: bold;
  color: #303133;
  text-align: center; /* 确保内容居中 */
}

.loan-table .el-table__header {
  text-align: center; /* 确保表头居中 */
}
</style>
