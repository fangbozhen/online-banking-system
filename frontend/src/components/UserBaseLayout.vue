<template>
  <div class="main" style="overflow-y: hidden;">
    <el-container>
      <el-header class="title">
        <div style="margin-top: 12px; display: inline-block;">
          <img src="./Icon/logo.png"
                style=" margin-right: 20px; height: 40px; vertical-align: middle;" />
          <span style="font-size: large; font-family: 'Microsoft YaHei';
              color: black; font-weight: bold;">在线银行系统</span>
          <span style="margin-left: 40px; color:rgba(0, 0, 0, 0.2)">浙江大学软件工程基础课程项目</span>
        </div>
      </el-header>
      <el-container style="width: 100%;">
        <el-aside class="aside" style="position: relative;">
          <el-menu active-text-color="#ffd04b" background-color="#0270c1" default-active="1" text-color="#fff" style="height:100%; width: 100%;" :router="true">
            <el-menu-item index="profile">
              <el-icon>
                <Reading />
              </el-icon>
              <span>用户信息</span>
            </el-menu-item>
            <el-menu-item index="account">
              <el-icon>
                <Operation />
              </el-icon>
              <span>账户管理</span>
            </el-menu-item>
            <el-menu-item index="foreign_exchange">
              <el-icon>
                <Coin />
              </el-icon>
              <span>外汇交易</span>
            </el-menu-item>
            <el-menu-item index="load">
              <el-icon>
                <Money />
              </el-icon>
              <span>贷款模块</span>
            </el-menu-item>
            <el-button class="logout-button" type="danger" @click="logoutVisible = true">
              退出登录
            </el-button>
          </el-menu>
        </el-aside>

        <el-main style="height: 100%; width: 100%; ">
          <el-scrollbar height="100%">
            <RouterView class="content" style="height: 90vh; max-height: 100%; background-color: white; color: black;" />
          </el-scrollbar>
        </el-main>

      </el-container>
    </el-container>

    <!-- 退出登录对话框 -->
    <el-dialog title="提示" v-model="logoutVisible" width="30%" align-center>
      确认退出当前账号？
      <template #footer>
        <span>
          <el-button @click="logoutVisible = false">取消</el-button>
          <el-button type="primary" @click="handleLogout">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
  </div>      
</template>

<script>
import Cookies from 'js-cookie';
import axios from 'axios';

export default {
  data() {
    return {
      logoutVisible: false,
    }
  },
  methods: {
    handleLogout() {
      axios.defaults.headers.common['Authorization'] = Cookies.get('token');
      Cookies.remove('token');
      axios.delete('/user/logout')
        .then(() => {
          this.$router.push('/personalBank/user/login');
        })
        .catch((err) => {
          console.log(err);
        });
    },
  },
};

</script>

<style scoped>
#app {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: #dcdcdc;
  width: 100vw;
  height: 100vh;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  min-height: 100%;
  height: auto;
  background-color: #dcdcdc;

}

.title {
  background-color: #ffffff;
  height: 60px;
}

.aside {
  min-height: calc(100vh - 60px);
  width: 180px;
  background-color: red;
}

.logout-button {
  position: absolute;
  bottom: 10%;
  left: 50%;
  transform: translateX(-50%);
}

</style>
