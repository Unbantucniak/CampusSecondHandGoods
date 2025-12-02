<template>
  <div class="app-shell">
    <Navbar v-if="showNavbar" class="app-shell__nav" />
    <main class="app-shell__main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import Navbar from './components/Navbar.vue';

const route = useRoute();
const hideNavbarOn = ['/login'];
const showNavbar = computed(() => !hideNavbarOn.includes(route.path));
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
  /* padding removed to allow full-width hero sections */
  width: 100%;
  max-width: 100%;
}
</style>