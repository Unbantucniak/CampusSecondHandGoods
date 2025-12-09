<template>
  <div class="detail-wrapper">
    <div class="detail-content">
      <div class="glass-container" v-if="goodsDetail.id">
        <!-- Left: Image Gallery -->
        <div class="image-section">
          <div class="image-container">
            <el-image 
              :src="goodsImage" 
              fit="cover"
              class="main-image"
              :preview-src-list="[goodsImage]"
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
        </div>

        <!-- Right: Info -->
        <div class="info-section">
          <div class="header-info">
            <h1 class="goods-title">{{ goodsDetail.title }}</h1>
            <div class="price-row">
              <div class="price-tag">
                <span class="currency">¥</span>
                <span class="amount">{{ goodsDetail.price }}</span>
              </div>
              <div class="view-stat">
                <el-icon><View /></el-icon>
                <span>{{ goodsDetail.viewCount || 0 }}次浏览</span>
              </div>
            </div>
          </div>

          <div class="seller-card">
            <el-avatar :size="50" :src="sellerAvatar" class="seller-avatar" />
            <div class="seller-info">
              <p class="seller-name">{{ goodsDetail.sellerName }}</p>
              <p class="seller-contact">
                <el-icon><Phone /></el-icon>
                {{ goodsDetail.contact }}
              </p>
            </div>
          </div>

          <div class="description-box">
            <h3>商品详情</h3>
            <p>{{ goodsDetail.description }}</p>
          </div>

          <div class="action-bar">
            <el-button 
              type="primary" 
              size="large" 
              class="buy-btn" 
              round 
              @click="handleBuy"
              :disabled="goodsDetail.status === 1"
            >
              {{ goodsDetail.status === 1 ? '已售出' : '立即购买' }}
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- Loading State -->
      <div v-else class="loading-state">
        <el-skeleton animated>
          <template #template>
            <div style="display: flex; gap: 40px;">
              <el-skeleton-item variant="image" style="width: 50%; height: 400px;" />
              <div style="width: 50%;">
                <el-skeleton-item variant="h1" style="width: 50%" />
                <el-skeleton-item variant="text" style="margin-top: 20px;" />
                <el-skeleton-item variant="text" />
                <el-skeleton-item variant="text" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getGoodsDetail } from '../api/goods';
import { getUserById } from '../api/user';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Picture, Phone, Star, Share, View } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const goodsDetail = ref({
  id: '',
  title: '',
  price: '',
  description: '',
  sellerName: '加载中...',
  contact: '',
  sellerAvatar: '',
  imgUrl: '',
  userId: null,
  status: 0,
  viewCount: 0
});

const handleBuy = () => {
  ElMessageBox.alert(
    `请联系卖家购买：<br/><strong>${goodsDetail.value.sellerName}</strong><br/>联系方式：${goodsDetail.value.contact}`,
    '购买提示',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '知道了',
      callback: () => {}
    }
  );
};

const assetBaseURL = import.meta.env.VITE_FILE_BASE_URL || 'http://localhost:8080';
const defaultAvatar = 'https://placehold.co/96x96?text=Seller';

const goodsImage = computed(() => {
  const url = goodsDetail.value.imgUrl;
  if (!url) return 'https://picsum.photos/800/600';
  if (/^https?:\/\//i.test(url)) return url;
  if (url.startsWith('/')) return `${assetBaseURL}${url}`;
  return url;
});

const sellerAvatar = computed(() => {
  const url = goodsDetail.value.sellerAvatar;
  if (!url) return defaultAvatar;
  if (/^https?:\/\//i.test(url)) return url;
  return url;
});

async function loadSellerContact(sellerId) {
  if (!sellerId) return;
  try {
    const res = await getUserById(sellerId);
    if (res.code === 200 && res.data) {
      goodsDetail.value.contact = res.data.contact || '暂无联系方式';
      goodsDetail.value.sellerName = res.data.username || '未知卖家';
      goodsDetail.value.sellerAvatar = res.data.avatarUrl || '';
    }
  } catch (error) {
    // 错误已在该页面的其他处理
  }
}

onMounted(async () => {
  const id = route.query.id || route.params.id;
  if (!id) {
    ElMessage.error('参数错误');
    router.push('/list');
    return;
  }
  
  try {
    const res = await getGoodsDetail(id);
    if (res.code === 200 && res.data) {
      goodsDetail.value = res.data;
      if (res.data.userId) {
        await loadSellerContact(res.data.userId);
      }
    } else {
      ElMessage.error('商品不存在');
      router.push('/list');
    }
  } catch (error) {
    ElMessage.error('网络错误');
  }
});
</script>

<style scoped>
.detail-wrapper {
  min-height: 100vh;
  background: #f8fafc;
  padding-top: 80px; /* Navbar height */
}

.detail-content {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

.glass-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 20px 40px -10px rgba(0, 0, 0, 0.05);
  display: flex;
  overflow: hidden;
  min-height: 600px;
  animation: fadeInUp 0.6s ease-out;
}

.image-section {
  flex: 1.2;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.image-container {
  width: 100%;
  height: 100%;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 30px -5px rgba(0, 0, 0, 0.1);
}

/* .image-container:hover {
  transform: scale(1.02);
} */

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.info-section {
  flex: 1;
  padding: 60px;
  display: flex;
  flex-direction: column;
}

.header-info {
  margin-bottom: 30px;
}

.goods-title {
  font-size: 2.5rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 16px;
  line-height: 1.2;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.price-tag {
  color: #ef4444;
  font-weight: 700;
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 1.5rem;
  margin-right: 4px;
}

.amount {
  font-size: 3rem;
  letter-spacing: -1px;
}

.view-stat {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #94a3b8;
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.seller-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 16px;
  margin-bottom: 30px;
  border: 1px solid #e2e8f0;
}

.seller-avatar {
  border: 2px solid white;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.seller-info {
  margin-left: 16px;
  flex: 1;
}

.seller-name {
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 4px;
}

.seller-contact {
  font-size: 0.875rem;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 4px;
}

.description-box {
  flex: 1;
  margin-bottom: 40px;
}

.description-box h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #334155;
  margin-bottom: 12px;
}

.description-box p {
  color: #64748b;
  line-height: 1.8;
}

.action-bar {
  display: flex;
  gap: 16px;
  margin-top: auto;
}

.buy-btn {
  flex: 1;
  height: 56px;
  font-size: 1.1rem;
  font-weight: 600;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  border: none;
  transition: all 0.3s;
}

.buy-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px -5px rgba(79, 70, 229, 0.3);
}

.icon-btn {
  width: 56px;
  height: 56px;
  border-color: #e2e8f0;
  color: #64748b;
}

.icon-btn:hover {
  color: #4f46e5;
  border-color: #4f46e5;
  background: #eef2ff;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 900px) {
  .glass-container {
    flex-direction: column;
  }
  
  .image-section {
    height: 400px;
    padding: 0;
  }
  
  .image-container {
    border-radius: 0;
  }
  
  .info-section {
    padding: 30px;
  }
}
</style>