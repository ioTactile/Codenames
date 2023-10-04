<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { Room } from '@/types/types'
import Nav from '@/components/RoomNav.vue'
import Join from '@/components/RoomJoin.vue'
import Instructions from '@/components/RoomInstructions.vue'
import Board from '@/components/RoomBoard.vue'
import RedSide from '@/components/RoomRedSide.vue'
import BlueSide from '@/components/RoomBlueSide.vue'
import TimerModal from '@/components/RoomTimerModal.vue'
import { apiFetchData } from '@/utils/api'
import { useRoomUserStore } from '@/stores/roomUser'

const room = ref<Room | null>(null)
const props = defineProps<{
  id: number
}>()

const roomUserStore = useRoomUserStore()
const isTimerMenuOpen = ref<boolean>(false)
const isRoomUser = ref<boolean>(false)
const isLoading = ref<boolean>(true)

const isHost = computed(() => {
  if (!room.value) return false
  const host = room.value.players[0].name
  return host === roomUserStore.getRoomUser(room.value.id)?.username
})

onMounted(async () => {
  const roomId = props.id
  if (!roomId) {
    alert('No room id provided')
    return
  }

  try {
    const data: Room = await apiFetchData(`room/${roomId}`, 'GET')
    room.value = data
    console.log('data', data)
    isRoomUser.value = !!room.value?.players.some(
      (player) => player.name === roomUserStore.getRoomUser(room.value!.id)?.username
    )
    isLoading.value = false
  } catch (error) {
    console.error('Error:', error)
  }
})
</script>

<template>
  <div class="first-layer absolute inset-0 transition-all"></div>
  <div class="second-layer bg-cover absolute inset-0"></div>
  <div id="scaler">
    <div id="gamescene">
      <template v-if="isLoading">
        <main class="flex justify-center items-center text-white font-bold h-screen px-2">
          Connexion au salon...
        </main>
      </template>
      <template v-else>
        <template v-if="isRoomUser">
          <main>
            <Nav :room="room" :is-host="isHost" @open-timer-menu="isTimerMenuOpen = $event" />
            <Instructions :room="room" :is-host="isHost" />
            <Board :room="room" />
            <RedSide :room="room" />
            <BlueSide :room="room" />
          </main>
        </template>
        <template v-else>
          <main class="flex justify-center items-center h-screen px-2">
            <Join location="join" @join="isRoomUser = $event" />
          </main>
        </template>
      </template>
    </div>
  </div>
  <TimerModal :isTimerMenuOpen="isTimerMenuOpen" @close-timer-menu="isTimerMenuOpen = $event" />
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

#scaler {
  position: relative;
  transform: scale(calc(var(--vh, 1vh) / 0.01));
}

/* #gamescene {
  position: absolute;
  left: 50%;
  top: 0px;
  transform: translateX(-50%);
  width: 1920px;
  height: 1080px;
} */
</style>
