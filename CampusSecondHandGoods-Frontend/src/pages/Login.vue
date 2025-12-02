<!-- src/pages/Login.vue -->
<template>
  <div class="login-wrapper">
    <!-- Left Side: Visuals -->
    <div class="login-visual">
      <div class="visual-content">
        <h1 class="brand-title">Campus Market</h1>
        <p class="brand-slogan">让闲置物品流转起来，<br>传递价值与快乐。</p>
      </div>
      <!-- New Dynamic Background -->
      <div class="aurora-bg">
        <div class="aurora-item"></div>
        <div class="aurora-item"></div>
        <div class="aurora-item"></div>
      </div>
    </div>

    <!-- Right Side: Form -->
    <div class="login-form-container">
      <div class="form-box">
        <h2 class="form-title">{{ activeTab === 'login' ? '欢迎回来' : '加入我们' }}</h2>
        <p class="form-subtitle">{{ activeTab === 'login' ? '登录您的账户以继续' : '创建一个新账户开始交易' }}</p>
        
        <div class="tab-switcher">
          <span 
            :class="{ active: activeTab === 'login' }" 
            @click="activeTab = 'login'"
          >登录</span>
          <span 
            :class="{ active: activeTab === 'register' }" 
            @click="activeTab = 'register'"
          >注册</span>
        </div>

        <!-- Login Form -->
        <transition name="fade" mode="out-in">
          <div v-if="activeTab === 'login'" key="login" class="form-content">
            <el-form :model="loginForm" size="large" class="custom-form">
              <el-form-item>
                <el-input 
                  v-model="loginForm.username" 
                  placeholder="用户名"
                  :prefix-icon="User"
                />
              </el-form-item>
              <el-form-item>
                <el-input 
                  v-model="loginForm.password" 
                  type="password" 
                  placeholder="密码"
                  :prefix-icon="Lock"
                  show-password
                  @keyup.enter="handleLogin"
                />
              </el-form-item>
              <el-button 
                type="primary" 
                class="submit-btn"
                :loading="loading"
                @click="handleLogin"
              >
                立即登录
              </el-button>
            </el-form>
          </div>

          <!-- Register Form -->
          <div v-else key="register" class="form-content">
            <el-form :model="registerForm" size="large" class="custom-form">
              <el-form-item>
                <el-input 
                  v-model="registerForm.username" 
                  placeholder="设置用户名"
                  :prefix-icon="User"
                />
              </el-form-item>
              <el-form-item>
                <el-input 
                  v-model="registerForm.password" 
                  type="password" 
                  placeholder="设置密码"
                  :prefix-icon="Lock"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-input 
                  v-model="registerForm.contact" 
                  placeholder="联系方式 (QQ/微信/手机)"
                  :prefix-icon="Phone"
                />
              </el-form-item>
              <el-form-item>
                <el-input 
                  v-model="registerForm.avatarUrl" 
                  placeholder="头像链接 (可选)"
                  :prefix-icon="Picture"
                />
              </el-form-item>
              <el-button 
                type="primary" 
                class="submit-btn"
                :loading="loading"
                @click="handleRegister"
              >
                立即注册
              </el-button>
            </el-form>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { login, register } from '../api/user';
import { User, Lock, Phone, Picture } from '@element-plus/icons-vue';

const router = useRouter();
const activeTab = ref('login');
const loading = ref(false);

const loginForm = ref({
  username: '',
  password: ''
});

const registerForm = ref({
  username: '',
  password: '',
  contact: '',
  avatarUrl: ''
});

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码');
    return;
  }
  
  loading.value = true;
  try {
    const payload = {
      username: loginForm.value.username.trim(),
      password: loginForm.value.password
    };
    const res = await login(payload);
    if (res.code === 200 && res.data && res.data.id) {
      ElMessage.success('登录成功');
      localStorage.setItem('userId', res.data.id);
      localStorage.setItem('username', res.data.username || payload.username);
      localStorage.setItem('contact', res.data.contact || '');
      localStorage.setItem('avatarUrl', res.data.avatarUrl || '');
      router.push('/list');
    } else {
      ElMessage.error(res?.msg || '登录失败');
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const handleRegister = async () => {
  if (!registerForm.value.username || !registerForm.value.password || !registerForm.value.contact) {
    ElMessage.warning('请完善必填信息');
    return;
  }

  loading.value = true;
  try {
    const payload = {
      username: registerForm.value.username.trim(),
      password: registerForm.value.password,
      contact: registerForm.value.contact.trim(),
      avatarUrl: registerForm.value.avatarUrl.trim()
    };
    const res = await register(payload);
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录');
      activeTab.value = 'login';
      loginForm.value.username = registerForm.value.username;
      loginForm.value.password = '';
    } else if (res.code === 409) {
      ElMessage.warning(res.msg || '用户名已被注册');
    } else {
      ElMessage.error(res.msg || '注册失败');
    }
  } catch (error) {
    ElMessage.error('网络错误');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-wrapper {
  display: flex;
  min-height: 100vh;
  background: #fff;
  overflow: hidden;
}

.login-visual {
  flex: 1;
  background: #0f172a;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  overflow: hidden;
}

.visual-content {
  position: relative;
  z-index: 10;
  color: white;
  max-width: 500px;
  text-shadow: 0 4px 12px rgba(0,0,0,0.3);
}

.brand-title {
  font-size: 4.5rem;
  font-weight: 900;
  margin-bottom: 24px;
  letter-spacing: -0.03em;
  background: linear-gradient(to right, #fff, #e2e8f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-slogan {
  font-size: 1.8rem;
  line-height: 1.6;
  opacity: 0.9;
  font-weight: 300;
}

/* Aurora Background Effect */
.aurora-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  overflow: hidden;
  background: #000;
}

.aurora-item {
  position: absolute;
  filter: blur(80px);
  opacity: 0.6;
  animation: aurora-move 10s infinite alternate;
}

.aurora-item:nth-child(1) {
  top: -20%;
  left: -20%;
  width: 70%;
  height: 70%;
  background: #4f46e5;
  animation-duration: 12s;
}

.aurora-item:nth-child(2) {
  bottom: -20%;
  right: -20%;
  width: 60%;
  height: 60%;
  background: #ec4899;
  animation-duration: 15s;
  animation-direction: alternate-reverse;
}

.aurora-item:nth-child(3) {
  top: 40%;
  left: 40%;
  width: 50%;
  height: 50%;
  background: #8b5cf6;
  animation-duration: 18s;
}

@keyframes aurora-move {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(10%, 10%) scale(1.1); }
}

.login-form-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #f8fafc;
  position: relative;
}

/* Add a subtle pattern to the form side */
.login-form-container::before {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: radial-gradient(#e2e8f0 1px, transparent 1px);
  background-size: 24px 24px;
  opacity: 0.5;
  pointer-events: none;
}

.form-box {
  width: 100%;
  max-width: 440px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  padding: 48px;
  border-radius: 32px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.5);
  position: relative;
  z-index: 2;
  transition: transform 0.3s ease;
}

.form-box:hover {
  transform: translateY(-5px);
}

.form-title {
  font-size: 2.2rem;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 8px;
}

.form-subtitle {
  color: #64748b;
  margin-bottom: 40px;
  font-size: 1.1rem;
}

.tab-switcher {
  display: flex;
  gap: 32px;
  margin-bottom: 40px;
  border-bottom: 2px solid #f1f5f9;
}

.tab-switcher span {
  padding-bottom: 16px;
  cursor: pointer;
  color: #94a3b8;
  font-weight: 600;
  font-size: 1.1rem;
  transition: all 0.3s;
  position: relative;
}

.tab-switcher span.active {
  color: #4f46e5;
}

.tab-switcher span.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 3px;
  background: #4f46e5;
  border-radius: 3px;
}

.custom-form :deep(.el-input__wrapper) {
  box-shadow: none;
  background: #f1f5f9;
  border-radius: 16px;
  padding: 12px 20px;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.custom-form :deep(.el-input__wrapper.is-focus) {
  background: white;
  border-color: #4f46e5;
  box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.1);
}

.submit-btn {
  width: 100%;
  height: 56px;
  border-radius: 16px;
  font-size: 18px;
  font-weight: 700;
  margin-top: 24px;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  border: none;
  transition: all 0.3s;
  letter-spacing: 0.5px;
}

.submit-btn:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 20px 25px -5px rgba(79, 70, 229, 0.4);
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 900px) {
  .login-wrapper {
    flex-direction: column;
  }
  
  .login-visual {
    display: none;
  }
  
  .form-box {
    padding: 32px;
    box-shadow: none;
    background: transparent;
    border: none;
  }
}
</style>