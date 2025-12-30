<template>
  <div class="app-shell">
    <Navbar v-if="showNavbar" class="app-shell__nav" />
    <main class="app-shell__main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'; //计算属性
import { useRoute } from 'vue-router'; 
import Navbar from './components/Navbar.vue'; //导入导航组件

const route = useRoute(); //获取路由对象
const hideNavbarOn = ['/login']; //隐藏导航
const showNavbar = computed(() => !hideNavbarOn.includes(route.path)); //如果路径在hideNavbarOn中，则不显示导航
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background-color: #f5f5f5;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-shell__nav {
  position: sticky; 
  top: 0;
  z-index: 10;
}

.app-shell__main {
  flex: 1;
  width: 100%;
  max-width: 100%;
}
</style>