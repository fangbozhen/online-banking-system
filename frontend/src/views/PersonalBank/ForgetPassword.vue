<template>
  <div class="main" style="overflow-y: hidden;">
    <el-container>
      <el-header class="title">
        <div style="margin-top: 12px; display: inline-block;">
          <img src="../../assets/logo.png"
               style=" margin-right: 20px; height: 40px; vertical-align: middle;" />
          <span style="font-size: large; font-family: 'Microsoft YaHei';
              color: black; font-weight: bold;">在线银行系统</span>
          <span style="margin-left: 40px; color:rgba(0, 0, 0, 0.2)">浙江大学软件工程基础课程项目</span>
        </div>
      </el-header>
      <el-main class="main-content">
        <el-card class="forget-card" style="margin-top: -100px;">
          <template #header>
            <div class="container">
              <div style="margin-left:5px" >
                <span>忘记密码</span>
              </div>
            </div>
          </template>
          <!-- 忘记密码卡片的body -->
          <el-form :model="forgetForm" :rules="forgetRules" ref="forgetForm" label-width="80px">
            <el-form-item label="邮箱" prop="email" style="margin-top: 20px">
              <el-input v-model="forgetForm.email" placeholder="邮箱" style="width: 200px"></el-input>
              <el-button v-if="isCounting" type="primary" style="margin-left: 5px; width: 95px" :disabled="isCounting">{{countDown}}秒后重试</el-button>
              <el-button v-else type="primary" style="margin-left: 5px; width: 95px" @click="getVerificationCode">获取验证码</el-button>
            </el-form-item>
            <el-form-item label="验证码" prop="verificationCode" style="margin-top: 20px">
              <el-input v-model="forgetForm.verificationCode" placeholder="六位数字验证码" style="width: 200px"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password" style="margin-top: 20px">
              <el-input v-model="forgetForm.password" placeholder="6-16位，至少包括字母和数字" style="width: 300px" show-password></el-input>
            </el-form-item>
            <el-form-item label="重复密码" prop="repassword" style="margin-top: 20px">
              <el-input v-model="forgetForm.repassword" placeholder="请再次输入密码" style="width: 300px" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitForm" style="width: 300px">重置密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import CryptoJS from 'crypto-js';
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  data() {
    const checkPassword = (rule, value, callback) => {
      if (value !== this.forgetForm.password) {
        callback(new Error('两次密码不一致'));
      } else {
        callback();
      }
    };
    return {
      // 邮件发送验证码倒计时
      countDown: 30,
      // 是否正在倒计时
      isCounting: false,
      // 倒计时定时器
      countDownTimeout: null,
      uuid: 0,
      forgetForm: {
        email: '',
        verificationCode: '',
        password: '',
        repassword: ''
      },
      forgetRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'change' },
          { pattern: /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/, message: '请输入有效的邮箱地址', trigger: 'blur' }
        ],
        verificationCode: [
          { required: true, message: '请输入验证码', trigger: 'change' },
          { pattern: /^\d{6}$/, message: '请输入有效的验证码', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'change' },
          { pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]{6,16}$/, message: '密码必须为6-16位，且同时包含字母和数字', trigger: 'blur' }
        ],
        repassword: [
          { required: true, message: '请再次输入密码', trigger: 'change'},
          { validator: checkPassword, message: '两次密码不一致', trigger: 'blur'}
        ]
      }
    };
  },
  methods: {
    jumpLogin() {
      this.$router.push('/personalBank/user/login');
    },
    submitForm() {
      // 表单校验
      this.$refs["forgetForm"].validate((valid) => {
        if (!valid) {
          ElMessage.error("重置失败，请检查信息");
          return;
        } else {
          this.handleForget();
        }
      });
    },
    handleForget() {
      // 加密后传给后端
      const encrypted = CryptoJS.SHA256(this.forgetForm.password).toString();
      axios.post("/user/forget",
          {
            "email": this.forgetForm.email,
            "uuid": this.uuid,
            "password": encrypted,
            "verification_code": this.forgetForm.verificationCode
          })
          .then(response => {
            if (response.data.code === 0) {
              ElMessage.success("重置密码成功");
              this.jumpLogin();
            } else {
              ElMessage.error(response.data.err);
            }
          })
          .catch(error => {
            console.log(error);
          })
    },
    getVerificationCode() {
      if (!this.isEmailValid) {
        ElMessage.error("请输入有效的邮箱地址");
        return;
      }
      console.log("发送成功");
      this.countDown = 30;
      this.isCounting = true;
      this.doCountdown();
      axios.post("/user/register/sendMail",
          {
            "mail": this.forgetForm.email
          })
          .then(response => {
            if (response.data.code === 0) {
              this.uuid = response.data.payload.uuid;
            } else {
              ElMessage.error(response.data.err);
            }
          })
          .catch(error => {
            console.log(error);
          })
    },
    doCountdown() {
      this.countDownTimeout = setTimeout(() => {
        if (this.countDown <= 0) {
          this.isCounting = false;
          clearTimeout(this.countDownTimeout);
        } else {
          this.countDown--;
          this.doCountdown();
        }
      },1000)
    }
  },
  computed: {
    isEmailValid() {
      const pattern = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
      return pattern.test(this.forgetForm.email);
    }
  }
};
</script>

<style scoped>

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

.forget-card {
  width: 450px;
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.main-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-color: #ececec;
}

.title {
  background-color: #ffffff;
  height: 60px;
}

</style>