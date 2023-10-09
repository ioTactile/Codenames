<template>
  <div class="first-layer absolute inset-0"></div>
  <div class="second-layer absolute inset-0 bg-cover"></div>
  <div class="flex h-screen w-full items-center justify-center">
    <button class="button text-base shadow-bottom">
      <RouterLink to="/">Retour à la page d'accueil</RouterLink>
    </button>
  </div>
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
