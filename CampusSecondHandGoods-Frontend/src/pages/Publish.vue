<template>
  <div class="publish-wrapper">
    <div class="publish-content">
      <div class="glass-form-container">
        <div class="form-header">
          <h1 class="page-title">{{ isEditMode ? '编辑商品' : '发布闲置' }}</h1>
          <p class="page-subtitle">{{ isEditMode ? '修改商品信息' : '填写商品信息，让好物找到新主人' }}</p>
        </div>

        <el-form 
          :model="goodsForm" 
          label-position="top"
          class="custom-form"
          size="large"
        >
          <el-form-item label="商品标题">
            <el-input 
              v-model="goodsForm.title" 
              placeholder="品牌型号，都是买家喜欢搜索的"
              :prefix-icon="Goods"
            />
          </el-form-item>

          <div class="form-row">
            <el-form-item label="价格 (元)" class="half-width">
              <el-input 
                v-model="goodsForm.price" 
                placeholder="0.00"
                :prefix-icon="Money"
              />
            </el-form-item>
            
            <el-form-item label="商品分类" class="half-width">
              <el-select
                v-model="goodsForm.category"
                placeholder="选择分类"
                style="width: 100%"
              >
                <el-option
                  v-for="item in GOODS_CATEGORIES"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </div>

          <el-form-item label="商品描述">
            <el-input
              v-model="goodsForm.description"
              type="textarea"
              :rows="6"
              placeholder="描述一下宝贝的转手原因、入手渠道和使用感受吧..."
              resize="none"
            />
          </el-form-item>

          <el-form-item label="商品图片">
            <div class="upload-area">
              <div class="upload-controls">
                <el-upload
                  class="upload-btn-wrapper"
                  :show-file-list="false"
                  :http-request="handleFileUpload"
                  accept="image/*"
                >
                  <el-button type="primary" plain class="upload-btn">
                    <el-icon><Upload /></el-icon>
                    上传图片
                  </el-button>
                </el-upload>
                
                <div class="url-upload">
                  <el-input
                    v-model="goodsForm.imgUrl"
                    placeholder="或粘贴图片链接"
                    class="url-input"
                  />
                  <el-button @click="handleUrlTransfer" :loading="urlUploading" circle>
                    <el-icon><Refresh /></el-icon>
                  </el-button>
                </div>
              </div>

              <transition name="fade">
                <div v-if="goodsForm.imgUrl" class="preview-container">
                  <el-image
                    :src="goodsForm.imgUrl"
                    fit="cover"
                    class="preview-image"
                    :preview-src-list="[goodsForm.imgUrl]"
                  />
                  <div class="preview-overlay" @click="goodsForm.imgUrl = ''">
                    <el-icon><Delete /></el-icon>
                  </div>
                </div>
              </transition>
            </div>
          </el-form-item>

          <div class="form-actions">
            <el-button @click="goBack" class="cancel-btn">取消</el-button>
            <el-button 
              type="primary" 
              :loading="submitting" 
              @click="handlePublish"
              class="submit-btn"
            >
              {{ isEditMode ? '保存修改' : '立即发布' }}
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { publishGoods, updateGoods, getGoodsDetail, uploadImage, uploadImageByUrl } from '../api/goods';
import { GOODS_CATEGORIES } from '../constants/categories';

const router = useRouter();
const route = useRoute();
const isEditMode = ref(false);
const goodsId = ref(null);

// 表单数据
const goodsForm = ref({
  title: '',
  price: '',
  category: null,
  description: '',
  imgUrl: ''
});

const submitting = ref(false);
const urlUploading = ref(false);

onMounted(async () => {
  const id = route.query.id;
  if (id) {
    isEditMode.value = true;
    goodsId.value = id;
    await loadGoodsDetail(id);
  }
});

const loadGoodsDetail = async (id) => {
  try {
    const res = await getGoodsDetail(id);
    if (res.code === 200 && res.data) {
      const data = res.data;
      goodsForm.value = {
        title: data.title,
        price: data.price,
        category: data.category,
        description: data.description,
        imgUrl: data.imgUrl
      };
    } else {
      ElMessage.error('加载商品信息失败');
      router.push('/my');
    }
  } catch (error) {
    ElMessage.error('网络错误');
    router.push('/my');
  }
};

// 发布/更新商品
const handlePublish = async () => {
  if (!goodsForm.value.title || !goodsForm.value.price) {
    ElMessage.warning('请填写商品标题和价格！');
    return;
  }

  const userId = localStorage.getItem('userId');
  if (!userId) {
    ElMessage.warning('请先登录账户');
    router.push('/login');
    return;
  }

  submitting.value = true;
  try {
    const priceNumber = Number(goodsForm.value.price);
    if (Number.isNaN(priceNumber) || priceNumber < 0) {
      ElMessage.warning('请输入正确的商品价格');
      submitting.value = false;
      return;
    }

    const payload = {
      title: goodsForm.value.title.trim(),
      price: priceNumber,
      category: goodsForm.value.category,
      description: goodsForm.value.description.trim(),
      imgUrl: goodsForm.value.imgUrl.trim() || null,
      userId: Number(userId)
    };

    let res;
    if (isEditMode.value) {
      payload.id = Number(goodsId.value);
      res = await updateGoods(payload);
    } else {
      res = await publishGoods(payload);
    }

    if (res.code === 200) {
      ElMessage.success(isEditMode.value ? '更新成功！' : '发布成功！');
      if (!isEditMode.value) {
        goodsForm.value = { title: '', price: '', category: null, description: '', imgUrl: '' };
      }
      router.push('/my');
    } else {
      ElMessage.error(res.msg || (isEditMode.value ? '更新失败' : '发布失败'));
    }
  } catch (error) {
    ElMessage.error('网络错误，暂时无法操作');
  } finally {
    submitting.value = false;
  }
};

// 取消发布（返回列表页）
const goBack = () => {
  router.push('/list');
};

const handleFileUpload = async ({ file, onError, onSuccess }) => {
  try {
    const res = await uploadImage(file);
    if (res.code === 200 && res.data?.url) {
      goodsForm.value.imgUrl = res.data.url;
      ElMessage.success('图片上传成功');
      onSuccess?.(res, file);
    } else {
      throw new Error(res.msg || '上传失败');
    }
  } catch (error) {
    ElMessage.error(error.message || '上传失败');
    onError?.(error);
  }
};

const handleUrlTransfer = async () => {
  if (!goodsForm.value.imgUrl) {
    ElMessage.warning('请先输入要转存的图片链接');
    return;
  }
  urlUploading.value = true;
  try {
    const res = await uploadImageByUrl(goodsForm.value.imgUrl.trim());
    if (res.code === 200 && res.data?.url) {
      goodsForm.value.imgUrl = res.data.url;
      ElMessage.success('已转存至服务器');
    } else {
      ElMessage.error(res.msg || '转存失败');
    }
  } catch (error) {
    ElMessage.error('转存失败，请稍后再试');
  } finally {
    urlUploading.value = false;
  }
};
</script>

<style scoped>
.publish-wrapper {
  min-height: 100vh;
  background: #f8fafc;
  padding-top: 80px;
  background-image: 
    radial-gradient(at 0% 0%, rgba(79, 70, 229, 0.1) 0px, transparent 50%),
    radial-gradient(at 100% 100%, rgba(236, 72, 153, 0.1) 0px, transparent 50%);
}

.publish-content {
  max-width: 900px;
  margin: 40px auto;
  padding: 0 20px;
}

.glass-form-container {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 20px 40px -10px rgba(0, 0, 0, 0.05);
  padding: 60px;
  animation: fadeInUp 0.6s ease-out;
}

.form-header {
  text-align: center;
  margin-bottom: 48px;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #1e293b 0%, #475569 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.page-subtitle {
  color: #64748b;
  font-size: 1.1rem;
}

.custom-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #334155;
  padding-bottom: 8px;
}

.custom-form :deep(.el-input__wrapper),
.custom-form :deep(.el-textarea__inner) {
  box-shadow: 0 0 0 1px #e2e8f0 inset;
  border-radius: 12px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s;
}

.custom-form :deep(.el-input__wrapper:hover),
.custom-form :deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px #cbd5e1 inset;
  background: white;
}

.custom-form :deep(.el-input__wrapper.is-focus),
.custom-form :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 2px #4f46e5 inset !important;
  background: white;
}

.form-row {
  display: flex;
  gap: 24px;
}

.half-width {
  flex: 1;
}

.upload-area {
  width: 100%;
}

.upload-controls {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.upload-btn {
  height: 40px;
  border-radius: 10px;
}

.url-upload {
  flex: 1;
  display: flex;
  gap: 8px;
}

.url-input :deep(.el-input__wrapper) {
  border-radius: 10px;
}

.preview-container {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  aspect-ratio: 16/9;
  background: #f1f5f9;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;
}

.preview-container:hover .preview-overlay {
  opacity: 1;
}

.preview-overlay .el-icon {
  font-size: 24px;
  color: white;
  background: rgba(255, 255, 255, 0.2);
  padding: 12px;
  border-radius: 50%;
  backdrop-filter: blur(4px);
}

.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 48px;
  padding-top: 32px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.submit-btn {
  flex: 2;
  height: 52px;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  border: none;
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px -4px rgba(79, 70, 229, 0.3);
}

.cancel-btn {
  flex: 1;
  height: 52px;
  font-size: 1.1rem;
  border-radius: 12px;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Fade Transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .glass-form-container {
    padding: 30px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>