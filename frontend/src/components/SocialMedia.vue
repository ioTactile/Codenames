<script setup lang="ts">
defineProps<{
  size?: string
}>()
</script>

<template>
  <aside
    :class="
      size === 'landscape'
        ? 'flex items-center justify-center landscape:mx-6 landscape:mt-2 landscape:gap-3'
        : 'absolute bottom-1.5 right-1 flex items-center justify-center gap-2 landscape:hidden'
    "
  >
    <a
      href="http://www.facebook.com/codenamesgame/"
      target="_blank"
      class="h-8 w-8 rounded-full bg-white opacity-75 hover:opacity-90 landscape:h-10 landscape:w-10"
    >
      <div class="flex h-full w-full items-center justify-center">
        <img src="/images/facebook.svg" alt="Facebook icon" class="w-7/12" />
      </div>
    </a>
    <a
      href="http://twitter.com/CodenamesGame"
      target="_blank"
      class="h-8 w-8 rounded-full bg-white opacity-75 hover:opacity-90 landscape:h-10 landscape:w-10"
    >
      <div class="flex h-full w-full items-center justify-center">
        <img src="/images/twitter.svg" alt="Twitter icon" class="w-7/12" />
      </div>
    </a>
    <a
      href="http://www.instagram.com/codenamesgame/"
      target="_blank"
      class="h-8 w-8 rounded-full bg-white opacity-75 hover:opacity-90 landscape:h-10 landscape:w-10"
    >
      <div class="flex h-full w-full items-center justify-center">
        <img src="/images/instagram.svg" alt="Instagram icon" class="w-7/12" />
      </div>
    </a>
  </aside>
</template>
