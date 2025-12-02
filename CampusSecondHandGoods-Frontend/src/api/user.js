// src/api/user.js
import request from './index';

// 登录接口（向后端发送用户名和密码）
export const login = (userData) => {
  return request.post('/user/login', userData);
};

// 注册接口（向后端发送注册信息）
export const register = (userData) => {
  return request.post('/user/register', userData);
};

// 根据用户ID获取用户信息（用于展示卖家联系方式）
export const getUserById = (id) => {
  if (id === undefined || id === null || id === '') {
    throw new Error('missing user id');
  }

  const numericId = Number(id);
  const paramsId = Number.isNaN(numericId) ? id : numericId;

  return request.get('/user/detail', {
    params: { userId: paramsId }
  });
};

export const updateProfile = ({ userId, contact, avatarUrl }) => {
  return request.put('/user/profile', { userId, contact, avatarUrl });
};

