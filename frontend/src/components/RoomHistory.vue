<script setup lang="ts">
import type { Room } from '@/types/types'

defineProps<{
  room: Room | null
}>()
</script>

<template>
  <div
    class="bg-white flex flex-col flex-1 landscape:flex-auto landscape:w-full overflow-hidden transition-all duration-400 landscape:shadow-bottom landscape:border-ui landscape:mt-4 landscape:rounded-xl opacity-50"
  >
    <div class="flex-none">
      <div class="text-base text-center">Historique</div>
      <div class="overflow-y-auto flex-auto landscape:rounded-b-xl"></div>
    </div>
  </div>
</template>
