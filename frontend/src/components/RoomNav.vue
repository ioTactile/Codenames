<script setup lang="ts">
import { ref } from 'vue'
import type { Player as P, Room } from '@/types/types'
import Players from '@/components/RoomPlayers.vue'
import Player from '@/components/RoomPlayer.vue'

defineProps<{
  room: Room
  isHost: boolean
  user: P | null
}>()

const emit = defineEmits<{
  (e: 'openTimerMenu', value: boolean): void
}>()

const isPlayersMenuOpen = ref<boolean>(false)
const isPlayerMenuOpen = ref<boolean>(false)

const togglePlayersMenu = () => {
  isPlayersMenuOpen.value = !isPlayersMenuOpen.value
}

const togglePlayerMenu = () => {
  isPlayerMenuOpen.value = !isPlayerMenuOpen.value
}
</script>

<template>
  <nav class="m-1 flex justify-between px-0.5 sm:px-2">
    <div class="flex">
      <div class="relative">
        <button @click="togglePlayersMenu" class="button shadow-bottom">
          <div class="flex items-center justify-center">
            <span class="hidden sm:block">Joueurs :</span>
            <img src="/images/icon_player.png" alt="Icon player" class="mr-1 h-4 landscape:ml-2" />
            <span>{{ room?.players.length }}</span>
          </div>
        </button>
        <Players
          v-if="isPlayersMenuOpen"
          :users="room.players"
          :id="room.id"
          :is-host="isHost"
          :room-status="room.status"
        />
      </div>
      <button
        v-if="isHost"
        class="button-circle ml-2 flex items-center justify-center shadow-bottom"
        @click="emit('openTimerMenu', true)"
      >
        <img src="/images/timer.png" alt="Timer icon" class="pointer-events-none w-3/4" />
      </button>
    </div>
    <div class="flex">
      <button class="button mx-2 shadow-bottom">Règles</button>
      <div class="relative">
        <button
          class="button shadow-bottom"
          :class="{
            'color-beige': user?.playerTeam === 'NONE',
            'color-red': user?.playerTeam === 'RED',
            'color-blue': user?.playerTeam === 'BLUE'
          }"
          @click="togglePlayerMenu"
        >
          <div class="relative">
            <span class="mr-7 truncate portrait:max-w-[100px]">
              {{ user?.name || 'Non défini' }}
            </span>
            <svg
              class="dark:text-dark-text absolute -right-1 top-3 h-5 w-5 -translate-y-1/2 transform fill-current text-gray-700"
              xmlns="http://www.w3.org/2000/svg"
              viewbox="0 0 22 22"
            >
              <path
                d="m432.71 528.79c-4.418 0-8 3.582-8 8 0 4.418 3.582 8 8 8 4.418 0 8-3.582 8-8 0-4.418-3.582-8-8-8m-2.667 4c.736 0 1.333.597 1.333 1.333 0 .736-.597 1.333-1.333 1.333-.736 0-1.333-.597-1.333-1.333 0-.736.597-1.333 1.333-1.333m5.333 0c.736 0 1.333.597 1.333 1.333 0 .736-.597 1.333-1.333 1.333-.736 0-1.333-.597-1.333-1.333 0-.736.597-1.333 1.333-1.333m-6.667 5.333h8c0 2.209-1.791 4-4 4-2.209 0-4-1.791-4-4"
                transform="translate(-421.71-525.79)"
              />
            </svg>
          </div>
        </button>
        <Player v-if="isPlayerMenuOpen" :id="room.id" :status="room.status" :user="user" />
      </div>
    </div>
  </nav>
</template>
