// 商品相关API接口
import request from './index';

// 获取商品列表：若传入category，则查询分类；否则获取全部列表
export const getGoodsList = ({ category, sort } = {}) => {
  const params = {};
  if (sort) {
    params.sort = sort;
  }
  if (category !== undefined && category !== null && category !== '') {
    params.category = category;
    return request.get('/goods/list', { params });
  }
  return request.get('/goods/listAll', { params });
};

// 获取商品详情
export const getGoodsDetail = (id) => {
  if (id === undefined || id === null || id === '') {
    throw new Error('missing goods id');
  }

  return request.get('/goods/detail', {
    params: { id }
  });
};

// 发布商品
export const publishGoods = (goodsData) => {
  return request.post('/goods/add', goodsData);
};

// 更新商品
export const updateGoods = (goodsData) => {
  return request.post('/goods/update', goodsData);
};

// 更新商品状态
export const updateGoodsStatus = (id, userId, status) => {
  return request.post('/goods/status', { id, userId, status });
};

export const uploadImage = (file) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

export const uploadImageByUrl = (url) => {
  return request.post('/files/uploadByUrl', { url });
};

// 获取我的商品
export const getMyGoods = (userId) => {
  return request.get('/goods/my', {
    params: { userId }
  });
};

// 删除我的商品
export const deleteMyGoods = ({ id, userId }) => {
  return request.delete('/goods/delete', {
    params: { id, userId }
  });
};

// 搜索商品
export const searchGoods = ({ keyword, sort }) => {
  const params = { keyword };
  if (sort) params.sort = sort;
  return request.get('/goods/search', { params });
};