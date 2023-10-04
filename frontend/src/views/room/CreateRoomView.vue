<script setup lang="ts">
import Join from '@/components/RoomJoin.vue'
</script>

<template>
  <main class="flex justify-center items-center h-screen px-2">
    <div class="first-layer absolute inset-0 transition-all"></div>
    <div class="second-layer bg-cover absolute inset-0"></div>
    <Join location="create" />
  </main>
</template>

<style scoped>
.first-layer {
  z-index: -1;
  background: radial-gradient(circle at 50% 50%, rgb(231, 102, 60) 0%, rgb(72, 12, 2) 100%);
}

.second-layer {
  z-index: -1;
  background-image: url('https://cdn.codenames.game/v20210210/img/bg-raster.svg');
  mix-blend-mode: overlay;
}
</style>
