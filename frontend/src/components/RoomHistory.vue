<script setup lang="ts">
import type { Room } from '@/types/types'

defineProps<{
  room: Room | null
}>()
</script>

<template>
  <div
    class="duration-400 landscape:border-ui flex flex-1 flex-col overflow-hidden bg-white opacity-50 transition-all landscape:mt-4 landscape:w-full landscape:flex-auto landscape:rounded-xl landscape:shadow-bottom"
  >
    <div class="flex-none">
      <div class="text-center text-base">Historique</div>
      <div class="flex-auto overflow-y-auto landscape:rounded-b-xl"></div>
    </div>
  </div>
</template>
