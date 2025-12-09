<template>
  <div class="my-goods-wrapper">
    <div class="my-goods-content">
      <div class="page-header">
        <h1 class="page-title">我的发布</h1>
        <el-button type="primary" round class="add-btn" @click="goToPublish">
          <el-icon><Plus /></el-icon> 发布新商品
        </el-button>
      </div>

      <div v-loading="loading" class="goods-grid-container">
        <el-empty
          v-if="!loading && goodsList.length === 0"
          description="您还没有发布商品"
          class="custom-empty"
        >
          <el-button type="primary" @click="goToPublish">去发布第一件商品</el-button>
        </el-empty>

        <div v-else class="goods-grid">
          <div 
            v-for="(item, index) in goodsList" 
            :key="item.id" 
            class="goods-card"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <div class="card-image-wrapper" @click="goToDetail(item.id)">
              <el-image 
                :src="getImageUrl(item.imgUrl)" 
                fit="cover" 
                class="card-image"
              >
                <template #error>
                  <div class="image-placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="card-overlay">
                <el-button circle @click.stop="goToDetail(item.id)">
                  <el-icon><View /></el-icon>
                </el-button>
              </div>
            </div>
            
            <div class="card-body">
              <h3 class="goods-title" @click="goToDetail(item.id)">{{ item.title }}</h3>
              <p class="goods-desc">{{ item.description || '暂无描述' }}</p>
              
              <div class="card-footer">
                <span class="price">¥{{ item.price }}</span>
                <div class="action-buttons">
                  <el-tooltip :content="item.status === 1 ? '重新上架' : '标记已售'" placement="top">
                    <el-button 
                      :type="item.status === 1 ? 'success' : 'warning'" 
                      circle 
                      plain 
                      size="small"
                      @click.stop="handleStatusChange(item)"
                    >
                      <el-icon><component :is="item.status === 1 ? 'Sell' : 'SoldOut'" /></el-icon>
                    </el-button>
                  </el-tooltip>
                  <el-button 
                    type="primary" 
                    circle 
                    plain 
                    size="small"
                    @click.stop="goToEdit(item.id)"
                    :disabled="item.status === 1"
                  >
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button 
                    type="danger" 
                    circle 
                    plain 
                    size="small"
                    @click.stop="confirmDelete(item.id)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
            <!-- Sold Overlay -->
            <div v-if="item.status === 1" class="sold-overlay">
              <span class="sold-text">已售出</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getMyGoods, deleteMyGoods, updateGoodsStatus } from '../api/goods';
import { Plus, Picture, View, Delete, Edit, SoldOut, Sell } from '@element-plus/icons-vue';

const router = useRouter();
const goodsList = ref([]);
const loading = ref(false);
const assetBaseURL = import.meta.env.VITE_FILE_BASE_URL || 'http://localhost:8080';

const getImageUrl = (url) => {
  if (!url) return 'https://picsum.photos/400/300';
  if (/^https?:\/\//i.test(url)) return url;
  if (url.startsWith('/')) return `${assetBaseURL}${url}`;
  return url;
};

const goToPublish = () => {
  router.push('/publish');
};

const goToEdit = (id) => {
  router.push(`/publish?id=${id}`);
};

const goToDetail = (id) => {
  router.push(`/detail/${id}`);
};

const handleStatusChange = async (item) => {
  const newStatus = item.status === 1 ? 0 : 1; // 在售(0)/已售(1)之间切换
  const actionText = newStatus === 1 ? '标记为已售出' : '重新上架';
  
  try {
    await ElMessageBox.confirm(`确认将该商品${actionText}吗？`, '提示', {
      type: 'info',
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    });

    const userId = localStorage.getItem('userId');
    const res = await updateGoodsStatus(item.id, Number(userId), newStatus);
    
    if (res.code === 200) {
      ElMessage.success('状态更新成功');
      item.status = newStatus;
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败');
    }
  }
};

const loadMyGoods = async () => {
  const userId = localStorage.getItem('userId');
  if (!userId) {
    ElMessage.warning('请先登录后查看我的发布');
    router.push('/login');
    return;
  }

  loading.value = true;
  try {
    const res = await getMyGoods(userId);
    if (res.code === 200 && Array.isArray(res.data)) {
      goodsList.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取商品列表失败');
    }
  } catch (error) {
    ElMessage.error('网络异常，暂时无法获取商品列表');
  } finally {
    loading.value = false;
  }
};

const confirmDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该商品吗？删除后不可恢复。', '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      icon: Delete
    });

    await deleteGoods(id);
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('删除操作失败');
    }
  }
};

const deleteGoods = async (id) => {
  try {
    const userId = localStorage.getItem('userId');
    const res = await deleteMyGoods({ id, userId });
    if (res.code === 200) {
      ElMessage.success('删除成功');
      goodsList.value = goodsList.value.filter((item) => item.id !== id);
    } else {
      ElMessage.error(res.msg || '删除失败');
    }
  } catch (error) {
    ElMessage.error('网络错误，删除失败');
  }
};

onMounted(loadMyGoods);
</script>

<style scoped>
.my-goods-wrapper {
  min-height: 100vh;
  background: #f8fafc;
  padding-top: 80px;
}

.my-goods-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 2rem;
  font-weight: 800;
  color: #1e293b;
  position: relative;
  padding-left: 16px;
}

.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 24px;
  background: #4f46e5;
  border-radius: 3px;
}

.add-btn {
  padding: 12px 24px;
  font-weight: 600;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  border: none;
  transition: all 0.3s;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px -5px rgba(79, 70, 229, 0.3);
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
}

.goods-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px -10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid #f1f5f9;
  animation: fadeInUp 0.6s ease-out backwards;
  position: relative;
  isolation: isolate; /* 独立层叠上下文，防止叠到其他卡片 */
}

.goods-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.1);
}

.card-image-wrapper {
  position: relative;
  height: 200px;
  overflow: hidden;
  cursor: pointer;
}

.card-image {
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease;
}

.goods-card:hover .card-image {
  transform: scale(1.1);
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.goods-card:hover .card-overlay {
  opacity: 1;
}

.card-body {
  padding: 20px;
}

.goods-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

.goods-desc {
  color: #64748b;
  font-size: 0.9rem;
  margin-bottom: 16px;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 2.8em;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.price {
  color: #ef4444;
  font-weight: 700;
  font-size: 1.2rem;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  color: #cbd5e1;
  font-size: 2rem;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.sold-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(2px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
  pointer-events: none;
}

.sold-text {
  font-size: 1.5rem;
  font-weight: 900;
  color: #94a3b8;
  border: 4px solid #94a3b8;
  padding: 8px 24px;
  border-radius: 8px;
  transform: rotate(-15deg);
  text-transform: uppercase;
  letter-spacing: 2px;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.custom-empty {
  padding: 60px 0;
}
</style>
