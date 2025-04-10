<template>
  <div class="main-layout">
    <TopBar />
    <div class="layout-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import TopBar from '@/components/TopBar.vue'
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.layout-content {
  flex: 1;
  overflow-y: auto;
}
</style> 