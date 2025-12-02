<template>
  <div class="settings-wrapper">
    <div class="settings-content">
      <div class="glass-card">
        <div class="settings-header">
          <div class="header-bg"></div>
          <div class="profile-summary">
            <div class="avatar-wrapper">
              <el-avatar :size="100" :src="form.avatarUrl || defaultAvatar" class="main-avatar" />
              <div class="avatar-overlay">
                <el-upload
                  :show-file-list="false"
                  :http-request="handleAvatarUpload"
                  accept="image/*"
                  class="avatar-uploader"
                >
                  <el-icon><Camera /></el-icon>
                </el-upload>
              </div>
            </div>
            <h2 class="username">{{ form.username }}</h2>
            <p class="user-id">ID: {{ form.userId }}</p>
          </div>
        </div>

        <div class="settings-body">
          <h3 class="section-title">个人资料设置</h3>
          
          <el-form 
            label-position="top" 
            :model="form" 
            :rules="rules" 
            ref="formRef"
            class="settings-form"
            size="large"
          >
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled :prefix-icon="User" />
            </el-form-item>

            <el-form-item label="头像链接" prop="avatarUrl">
              <div class="url-input-group">
                <el-input 
                  v-model="form.avatarUrl" 
                  placeholder="粘贴网络图片链接" 
                  :prefix-icon="Link"
                />
                <el-button @click="handleAvatarUrlTransfer" :loading="avatarUrlUploading" circle>
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </div>
            </el-form-item>

            <el-form-item label="联系方式" prop="contact">
              <el-input 
                v-model="form.contact" 
                placeholder="QQ / 微信 / 手机号" 
                :prefix-icon="Phone"
              />
            </el-form-item>

            <div class="form-actions">
              <el-button type="primary" :loading="saving" @click="handleSave" class="save-btn">
                保存修改
              </el-button>
              <el-button @click="resetForm" class="reset-btn">重置</el-button>
            </div>
          </el-form>

          <el-divider />

          <div class="danger-zone">
            <h3 class="section-title danger">账号操作</h3>
            <el-button type="danger" plain @click="handleLogout" class="logout-btn">
              <el-icon><SwitchButton /></el-icon> 退出登录
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getUserById, updateProfile } from '../api/user';
import { uploadImage, uploadImageByUrl } from '../api/goods';
import { User, Link, Phone, Camera, Refresh, SwitchButton } from '@element-plus/icons-vue';

const router = useRouter();
const formRef = ref();
const form = ref({
  userId: '',
  username: '',
  contact: '',
  avatarUrl: ''
});
const baseline = ref({ contact: '', avatarUrl: '' });
const saving = ref(false);
const defaultAvatar = 'https://placehold.co/96x96?text=User';
const avatarUrlUploading = ref(false);

const rules = {
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { max: 50, message: '联系方式过长', trigger: 'blur' }
  ]
};

const loadUserInfo = async () => {
  const userId = localStorage.getItem('userId');
  if (!userId) {
    router.push('/login');
    return;
  }
  form.value.userId = userId;
  form.value.username = localStorage.getItem('username') || '';
  form.value.contact = localStorage.getItem('contact') || '';
  form.value.avatarUrl = localStorage.getItem('avatarUrl') || defaultAvatar;
  baseline.value = { contact: form.value.contact, avatarUrl: form.value.avatarUrl };
  
  try {
    const res = await getUserById(userId);
    if (res.code === 200 && res.data) {
      form.value.username = res.data.username || form.value.username;
      form.value.contact = res.data.contact || '';
      form.value.avatarUrl = res.data.avatarUrl || defaultAvatar;
      baseline.value = { contact: form.value.contact, avatarUrl: form.value.avatarUrl };
      localStorage.setItem('username', form.value.username);
      localStorage.setItem('contact', form.value.contact);
      localStorage.setItem('avatarUrl', form.value.avatarUrl);
    }
  } catch (error) {
    // Silent fail for offline
  }
};

const handleSave = () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      const payload = {
        userId: Number(form.value.userId),
        contact: form.value.contact.trim(),
        avatarUrl: form.value.avatarUrl.trim() || defaultAvatar
      };
      const res = await updateProfile(payload);
      if (res.code === 200) {
        ElMessage.success('资料已更新');
        const serverContact = res.data?.contact ?? payload.contact;
        const serverAvatar = res.data?.avatarUrl ?? payload.avatarUrl;
        form.value.contact = serverContact;
        form.value.avatarUrl = serverAvatar;
        baseline.value = { contact: serverContact, avatarUrl: serverAvatar };
        localStorage.setItem('contact', serverContact);
        localStorage.setItem('avatarUrl', serverAvatar);
        window.dispatchEvent(new CustomEvent('profile-updated'));
      } else {
        ElMessage.error(res.msg || '更新失败');
      }
    } catch (error) {
      ElMessage.error('请求失败');
    } finally {
      saving.value = false;
    }
  });
};

const resetForm = () => {
  form.value.contact = baseline.value.contact;
  form.value.avatarUrl = baseline.value.avatarUrl;
};

const handleAvatarUpload = async ({ file, onError, onSuccess }) => {
  try {
    const res = await uploadImage(file);
    if (res.code === 200 && res.data?.url) {
      form.value.avatarUrl = res.data.url;
      ElMessage.success('头像上传成功');
      onSuccess?.(res, file);
    } else {
      throw new Error(res.msg || '上传失败');
    }
  } catch (error) {
    ElMessage.error(error.message || '上传失败');
    onError?.(error);
  }
};

const handleAvatarUrlTransfer = async () => {
  if (!form.value.avatarUrl) {
    ElMessage.warning('请输入链接');
    return;
  }
  avatarUrlUploading.value = true;
  try {
    const res = await uploadImageByUrl(form.value.avatarUrl.trim());
    if (res.code === 200 && res.data?.url) {
      form.value.avatarUrl = res.data.url;
      ElMessage.success('已转存');
    } else {
      ElMessage.error(res.msg || '转存失败');
    }
  } catch (error) {
    ElMessage.error('转存失败');
  } finally {
    avatarUrlUploading.value = false;
  }
};

const handleLogout = () => {
  localStorage.removeItem('userId');
  localStorage.removeItem('username');
  localStorage.removeItem('contact');
  localStorage.removeItem('avatarUrl');
  ElMessage.success('已退出登录');
  window.dispatchEvent(new CustomEvent('profile-updated'));
  router.push('/login');
};

onMounted(loadUserInfo);
</script>

<style scoped>
.settings-wrapper {
  min-height: 100vh;
  background: #f8fafc;
  padding-top: 80px;
}

.settings-content {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
}

.glass-card {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 40px -10px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9;
}

.settings-header {
  position: relative;
  text-align: center;
  padding-bottom: 30px;
  border-bottom: 1px solid #f1f5f9;
}

.header-bg {
  height: 120px;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
}

.profile-summary {
  margin-top: -50px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  border-radius: 50%;
  padding: 4px;
  background: white;
}

.main-avatar {
  border: 4px solid white;
  cursor: pointer;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: white;
  font-size: 24px;
  cursor: pointer;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.username {
  margin-top: 12px;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
}

.user-id {
  color: #64748b;
  font-size: 0.9rem;
}

.settings-body {
  padding: 40px;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #334155;
  margin-bottom: 24px;
}

.section-title.danger {
  color: #ef4444;
}

.settings-form {
  max-width: 500px;
  margin: 0 auto;
}

.url-input-group {
  display: flex;
  gap: 12px;
}

.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 32px;
}

.save-btn {
  flex: 1;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  border: none;
  font-weight: 600;
}

.reset-btn {
  width: 100px;
}

.logout-btn {
  width: 100%;
  margin-top: 16px;
}

@media (max-width: 600px) {
  .settings-body {
    padding: 24px;
  }
}
</style>
