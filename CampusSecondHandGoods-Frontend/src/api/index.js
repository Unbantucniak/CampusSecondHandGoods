// src/api/index.js
import axios from 'axios';

// 创建axios实例，配置基础路径（后端接口地址）
const baseURL = import.meta.env.VITE_API_BASE_URL || (import.meta.env.DEV ? '/api' : 'http://localhost:8080');

const request = axios.create({
  baseURL,
  timeout: 5000 // 请求超时时间（5秒）
});

// 响应拦截器：统一处理后端返回的数据
request.interceptors.response.use(
  (response) => {
    // 直接返回后端的data字段（简化使用）
    return response.data;
  },
  (error) => {
    // 错误处理（如网络异常）
    console.error('接口请求失败：', error);
    return Promise.reject(error);
  }
);

export default request;