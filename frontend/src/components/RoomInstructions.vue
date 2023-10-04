<script setup lang="ts">
import type { Room } from '@/types/types'

defineProps<{
  room: Room | null
  isHost: boolean
}>()
</script>

<template>
  <div class="relative flex items-center justify-center mx-auto h-16 w-full landscape:w-[1060px]">
    <div
      class="bg-white px-2 py-1 rounded-lg text-base landscape:text-2xl font-bold mx-12 text-center"
    >
      <span v-if="isHost">Configurer la partie :</span>
      <span v-else>Choose your role and wait until the host sets up a game.</span>
    </div>
  </div>
</template>
