<template>
  <div class="goods-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-bg-animation">
        <div class="blob blob-1"></div>
        <div class="blob blob-2"></div>
        <div class="blob blob-3"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">发现校园好物</h1>
        <p class="hero-subtitle">让闲置物品流转起来，传递价值与快乐</p>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索你感兴趣的宝贝..."
            class="hero-search-input"
            @keyup.enter="handleSearch"
            clearable
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon class="search-icon"><Search /></el-icon>
            </template>
            <template #append>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
      <div class="hero-shape"></div>
    </div>

    <!-- Main Content -->
    <div class="main-container">
      <!-- Toolbar -->
      <div class="toolbar">
        <div class="category-list">
          <div 
            class="category-item" 
            :class="{ active: activeCategory === 'all' }"
            @click="handleCategoryChange('all')"
          >
            全部
          </div>
          <div 
            v-for="cat in categoryOptions" 
            :key="cat.value"
            class="category-item"
            :class="{ active: activeCategory === cat.value }"
            @click="handleCategoryChange(cat.value)"
          >
            {{ cat.label }}
          </div>
        </div>
        
        <div class="sort-actions">
          <el-dropdown @command="handleSortChange" trigger="click">
            <span class="sort-trigger">
              {{ currentSortLabel }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item 
                  v-for="opt in SORT_OPTIONS" 
                  :key="opt.value" 
                  :command="opt.value"
                  :class="{ 'is-active': sortOrder === opt.value }"
                >
                  {{ opt.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button type="primary" class="publish-btn" @click="goToPublish">
            <el-icon><Plus /></el-icon> 发布闲置
          </el-button>
        </div>
      </div>

      <!-- Goods Grid -->
      <div class="goods-grid" v-loading="loading">
        <template v-if="displayedGoods.length > 0">
          <div 
            v-for="goods in displayedGoods" 
            :key="goods.id" 
            class="goods-card"
            @click="goToDetail(goods.id)"
          >
            <div class="card-image-wrapper">
              <img :src="resolveImage(goods.imgUrl)" loading="lazy" alt="商品图片">
              <div class="card-overlay">
                <span class="view-btn">查看详情</span>
              </div>
            </div>
            <div class="card-content">
              <h3 class="goods-title" :title="goods.title">{{ goods.title || '未命名商品' }}</h3>
              <div class="goods-meta">
                <span class="price">¥{{ goods.price }}</span>
                <div class="meta-right">
                  <span class="view-count" v-if="goods.viewCount">
                    <el-icon><View /></el-icon> {{ goods.viewCount }}
                  </span>
                  <span class="time">{{ formatDate(goods.createTime) }}</span>
                </div>
              </div>
              <div class="user-info" v-if="goods.sellerName">
                <el-avatar :size="20" :src="resolveAvatar(goods.sellerAvatar)">{{ goods.sellerName.charAt(0) }}</el-avatar>
                <span class="username">{{ goods.sellerName }}</span>
              </div>
            </div>
          </div>
        </template>
        
        <div v-else-if="!loading" class="empty-state">
          <el-empty description="暂无相关商品，去发布一个吧！" />
        </div>
      </div>

      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="totalGoods > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 36]"
          layout="prev, pager, next"
          :total="totalGoods"
          background
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { getGoodsList, searchGoods } from '../api/goods';
import { Search, ArrowDown, Plus, View } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { GOODS_CATEGORIES } from '../constants/categories';

const router = useRouter();
const assetBaseURL = import.meta.env.VITE_FILE_BASE_URL || 'http://localhost:8080';

// State
const loading = ref(false);
const goodsList = ref([]);
const searchKeyword = ref('');
const activeCategory = ref('all');
const sortOrder = ref('default');
const currentPage = ref(1);
const pageSize = ref(12);

// Options
const categoryOptions = GOODS_CATEGORIES.map(c => ({ value: String(c.value), label: c.label }));
const SORT_OPTIONS = [
  { value: 'default', label: '综合排序' },
  { value: 'priceAsc', label: '价格由低到高' },
  { value: 'priceDesc', label: '价格由高到低' },
  { value: 'newest', label: '最新发布' }
];

const currentSortLabel = computed(() => {
  const opt = SORT_OPTIONS.find(o => o.value === sortOrder.value);
  return opt ? opt.label : '排序';
});

const totalGoods = computed(() => goodsList.value.length);

const displayedGoods = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return goodsList.value.slice(start, end);
});

// Methods
const loadData = async () => {
  loading.value = true;
  try {
    let res;
    if (searchKeyword.value.trim()) {
      res = await searchGoods({ 
        keyword: searchKeyword.value,
        sort: sortOrder.value === 'default' ? undefined : sortOrder.value
      });
    } else {
      const categoryValue = activeCategory.value === 'all' ? undefined : Number(activeCategory.value);
      const sortValue = sortOrder.value === 'default' ? undefined : sortOrder.value;
      res = await getGoodsList({ category: categoryValue, sort: sortValue });
    }

    if (res.code === 200) {
      goodsList.value = res.data || [];
    } else {
      ElMessage.error(res.msg || '获取数据失败');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('网络连接失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  activeCategory.value = 'all'; // Reset category on search
  currentPage.value = 1;
  loadData();
};

const handleCategoryChange = (val) => {
  activeCategory.value = val;
  searchKeyword.value = ''; // Clear search on category change
  currentPage.value = 1;
  loadData();
};

const handleSortChange = (val) => {
  sortOrder.value = val;
  loadData();
};

const handlePageChange = (page) => {
  currentPage.value = page;
  window.scrollTo({ top: 400, behavior: 'smooth' });
};

const goToPublish = () => {
  const userId = localStorage.getItem('userId');
  if (!userId) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  router.push('/publish');
};

const goToDetail = (id) => {
  router.push(`/detail/${id}`);
};

const resolveImage = (url) => {
  if (!url) return 'https://picsum.photos/seed/default/400/300';
  if (url.startsWith('http')) return url;
  return `${assetBaseURL}${url}`;
};

const resolveAvatar = (url) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  return `${assetBaseURL}${url}`;
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.goods-page {
  min-height: 100vh;
  background-color: #f8fafc;
}

/* Hero Section */
.hero-section {
  position: relative;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 50%, #db2777 100%);
  background-size: 200% 200%;
  animation: gradientBG 15s ease infinite;
  padding: 100px 20px 140px;
  text-align: center;
  color: white;
  overflow: hidden;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.hero-bg-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
  pointer-events: none;
}

.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.6;
  animation: float 10s infinite ease-in-out;
}

.blob-1 {
  width: 300px;
  height: 300px;
  background: #60a5fa;
  top: -50px;
  left: -50px;
  animation-delay: 0s;
}

.blob-2 {
  width: 400px;
  height: 400px;
  background: #f472b6;
  bottom: -100px;
  right: -50px;
  animation-delay: -2s;
}

.blob-3 {
  width: 200px;
  height: 200px;
  background: #a78bfa;
  top: 40%;
  left: 20%;
  animation-delay: -4s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: 800px;
  margin: 0 auto;
  animation: fadeInUp 1s ease-out;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 900;
  margin-bottom: 20px;
  letter-spacing: -0.03em;
  text-shadow: 0 4px 12px rgba(0,0,0,0.1);
  background: linear-gradient(to right, #fff, #e0e7ff);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 48px;
  font-weight: 400;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.search-box {
  max-width: 640px;
  margin: 0 auto;
  transform: translateY(0);
  transition: transform 0.3s;
}

.search-box:hover {
  transform: translateY(-2px);
}

.hero-search-input :deep(.el-input__wrapper) {
  border-radius: 50px 0 0 50px;
  box-shadow: 0 8px 20px -4px rgba(0, 0, 0, 0.2);
  padding-left: 24px;
  height: 56px;
  font-size: 1.1rem;
  border: none;
}

.hero-search-input :deep(.el-input-group__append) {
  border-radius: 0 50px 50px 0;
  background-color: #ffffff;
  box-shadow: 0 8px 20px -4px rgba(0, 0, 0, 0.2);
  border-left: none;
  border: none;
}

.hero-search-input :deep(.el-button) {
  color: #4f46e5;
  font-weight: 700;
  font-size: 1.1rem;
  border-radius: 0 50px 50px 0;
  padding: 0 36px;
  height: 56px;
  transition: all 0.3s;
}

.hero-search-input :deep(.el-button:hover) {
  background-color: #f8fafc;
  color: #4338ca;
}

/* Main Content */
.main-container {
  max-width: 1280px;
  margin: -60px auto 0;
  padding: 0 24px 60px;
  position: relative;
  z-index: 3;
}

/* Toolbar */
.toolbar {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 24px;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.01);
  margin-bottom: 40px;
  flex-wrap: wrap;
  gap: 16px;
  transition: all 0.3s ease;
}

.toolbar:hover {
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  transform: translateY(-2px);
}

.category-list {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 4px;
  scrollbar-width: none;
}

.category-item {
  padding: 8px 20px;
  border-radius: 100px;
  font-size: 14px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
  background: rgba(241, 245, 249, 0.8);
  border: 1px solid transparent;
  font-weight: 500;
}

.category-item:hover {
  background: #e2e8f0;
  color: #334155;
  transform: translateY(-1px);
}

.category-item.active {
  background: #4f46e5;
  color: white;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
  border-color: rgba(255,255,255,0.1);
}

.sort-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.sort-trigger {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #475569;
  font-weight: 600;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

.sort-trigger:hover {
  background: rgba(0,0,0,0.03);
  color: #1e293b;
}

.publish-btn {
  border-radius: 100px;
  padding: 10px 28px;
  font-weight: 600;
  box-shadow: 0 4px 6px -1px rgba(79, 70, 229, 0.2);
  transition: all 0.3s;
}

.publish-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(79, 70, 229, 0.3);
}

/* Grid */
.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 32px;
  padding-bottom: 40px;
}

.goods-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(241, 245, 249, 0.8);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  position: relative;
  animation: fadeInUp 0.6s ease-out backwards;
}

/* Staggered animation for first few items */
.goods-card:nth-child(1) { animation-delay: 0.1s; }
.goods-card:nth-child(2) { animation-delay: 0.15s; }
.goods-card:nth-child(3) { animation-delay: 0.2s; }
.goods-card:nth-child(4) { animation-delay: 0.25s; }
.goods-card:nth-child(5) { animation-delay: 0.3s; }
.goods-card:nth-child(6) { animation-delay: 0.35s; }
.goods-card:nth-child(n+7) { animation-delay: 0.4s; }

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.goods-card:hover {
  transform: translateY(-12px) scale(1.02);
  box-shadow: 0 20px 40px -5px rgba(0, 0, 0, 0.1), 0 10px 20px -5px rgba(79, 70, 229, 0.1);
  border-color: rgba(79, 70, 229, 0.1);
  z-index: 1;
}

.card-image-wrapper {
  position: relative;
  padding-top: 80%; /* Slightly taller */
  overflow: hidden;
  background: #f1f5f9;
}

.card-image-wrapper img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.goods-card:hover .card-image-wrapper img {
  transform: scale(1.1);
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(2px);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
}

.goods-card:hover .card-overlay {
  opacity: 1;
}

.view-btn {
  background: white;
  color: #0f172a;
  padding: 10px 24px;
  border-radius: 100px;
  font-size: 14px;
  font-weight: 600;
  transform: translateY(20px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.view-btn:hover {
  transform: scale(1.05) translateY(0) !important;
  background: #f8fafc;
}

.goods-card:hover .view-btn {
  transform: translateY(0);
}

.card-content {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.goods-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}

.goods-meta {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 16px;
}

.price {
  color: #4f46e5;
  font-size: 1.25rem;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.time {
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 500;
}

.user-info {
  margin-top: auto;
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.username {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
}

.empty-state {
  grid-column: 1 / -1;
  padding: 60px 0;
}

.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

.meta-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #94a3b8;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .main-container {
    padding: 0 16px 40px;
  }
  
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .sort-actions {
    justify-content: space-between;
  }
}
</style>