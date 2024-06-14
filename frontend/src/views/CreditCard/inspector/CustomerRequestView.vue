<template>
  <el-main style="background-color: #f1f1f1;">
    <br>
    <br>
    <div style="display: flex; justify-content: center;">
      <el-table :data="request" stripe style="width: 1100px;">
        <el-table-column prop="id" label="请求编号" width="200px"/>
        <el-table-column prop="credit_card_id" label="信用卡id" width="200px">
          <template v-slot="{ row = {} }">
            <span v-if="row.creditCardId === null || row.creditCardId === ''">暂未创建</span>
            <span v-else>{{ row.creditCardId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="请求类型" width="200px">
          <template v-slot="{ row = {} }">
            <span v-if="row.type === 1">创建信用卡</span>
            <span v-else-if="row.type === 2">更新信用卡额度</span>
          </template>
        </el-table-column>
        <el-table-column label="具体请求内容" width="300px">
          <template v-slot="{ row = {} }">
            <span v-if="row.type === 1">创建一张新的信用卡,额度为{{ row.amount }}元</span>
            <span v-else-if="row.type === 2">更新信用卡的额度为{{ row.amount }}元</span>
          </template>
        </el-table-column>
        <el-table-column label="处理结果" width="200px">
          <template v-slot="{ row = {} }">
            <div style="margin-top: 10px;">
              <el-button type="success" size="mini" @click="accept(row.id)">通过</el-button>
              <el-button type="danger" size="mini" @click="reject(row.id)">驳回</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-main>
</template>

<script>

import axios from "axios";
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
  data() {
    return {
      request: [{
        id: '1',
        id_number: '123456',
        credit_card_id: '',
        amount: '10000',
        type: '1',
        status: '1',
        password: ''
      }, {
        id: '2',
        id_number: '234567',
        credit_card_id: '1',
        amount: '20000',
        type: '2',
        status: '2',
        password: ''
      }, {
        id: '3',
        id_number: '345678',
        credit_card_id: '1',
        amount: '30000',
        type: '2',
        status: '3',
        password: ''
      }]
    }
  },
  methods: {
    exit() {
      Cookies.remove('token');
      this.$router.push('/personalBank/admin/login');
    },
    accept(id) {
      axiosInstance.post("/credit-card/inspector/request/accept", null, {params: {id: id}})
          .then(response => {
            if (response.data.code === 1) {
              this.$message.error('允许请求失败');
            } else {
              this.$message.success('允许请求成功');
            }
            this.queryRequest();
          }).catch(error => {
        console.error('inspector accept error:', error);
        this.$message.error('允许请求失败');
      });
      //this.$message.success('通过id为' + id + '的请求');
    },
    reject(id) {
      //this.$message.error('拒绝了id为' + id + '的请求');
      axiosInstance.post("/credit-card/inspector/request/reject", null, {params: {id: id}})
          .then(response => {
            if (response.data.code === 1) {
              this.$message.error('驳回请求失败');
            } else {
              this.$message.success('驳回请求成功');
            }
            this.queryRequest();
          }).catch(error => {
        console.error('inspector reject error:', error);
        this.$message.error('驳回请求失败');
      });
    },
    queryRequest() {
      axiosInstance.post("/credit-card/inspector/request", null, {params: {permission: Cookies.get('credit_card_inspector_permission')}})
          .then(response => {
            this.request = [];
            let requests = response.data.payload;
            console.log(requests);
            requests.forEach(request => {
              this.request.push(request);
            })
          }).catch(error => {
        console.error('query request error:', error);
      })
    }
  },
  mounted() {
    this.queryRequest();
  }
}
</script>

<style scoped>
.el-menu-item > a {
  color: inherit;
  text-decoration: none !important;
}

.el-menu-item > a.is-active {
  color: inherit;
  text-decoration: none !important;
}

.el-aside, .el-menu, .el-sub-menu {
  background-color: rgb(47, 109, 185) !important;
}

.el-aside .el-menu-item a,
.el-aside .el-sub-menu__title,
.el-aside .el-sub-menu a {
  color: black !important;
}
</style>