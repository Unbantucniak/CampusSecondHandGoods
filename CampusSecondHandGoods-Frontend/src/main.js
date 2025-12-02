// src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import ElementPlus from 'element-plus'; 
import 'element-plus/dist/index.css'; 
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
import router from './router'; 

import * as ElementPlusIconsVue from '@element-plus/icons-vue'; // 引入所有图标

const app = createApp(App);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.use(ElementPlus, { locale: zhCn })
  .use(router)
  .mount('#app');