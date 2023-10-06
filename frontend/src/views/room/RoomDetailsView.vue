<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { Room } from '@/types/types'
import Nav from '@/components/RoomNav.vue'
import Join from '@/components/RoomJoin.vue'
import Instructions from '@/components/RoomInstructions.vue'
import Clue from '@/components/RoomClue.vue'
import Config from '@/components/RoomConfig.vue'
import Board from '@/components/RoomBoard.vue'
import RedSide from '@/components/RoomRedSide.vue'
import BlueSide from '@/components/RoomBlueSide.vue'
import History from '@/components/RoomHistory.vue'
import SocialMedia from '@/components/SocialMedia.vue'
import TimerModal from '@/components/RoomTimerModal.vue'
import { apiFetchData } from '@/utils/api'
import { useRoomUserStore } from '@/stores/roomUser'
import { Client, Stomp } from '@stomp/stompjs'
import SockJS from 'sockjs-client/dist/sockjs.min.js'

const room = ref<Room | null>(null)
const stompClient = ref<Client | null>(null)
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

const getUserTeam = computed(() => {
  if (!room.value) return null
  const user = room.value.players.find(
    (player) => player.name === roomUserStore.getRoomUser(room.value!.id)?.username
  )
  return user?.playerTeam
})

const getUser = () => {
  return !!room.value?.players.some(
    (player) => player.name === roomUserStore.getRoomUser(room.value!.id)?.username
  )
}

onMounted(async () => {
  const roomId = props.id
  if (!roomId) {
    alert('No room id provided')
    return
  }

  // try {
  //   const data: Room = await apiFetchData(`room/${roomId}`, 'GET')
  //   room.value = data
  //   console.log('data', data)
  //   isRoomUser.value = getUser()
  //   isLoading.value = false
  // } catch (error) {
  //   console.error('Error:', error)
  // }

  initializeStompClient()
  isRoomUser.value = getUser()
  isLoading.value = false
})

const handleRoomUpdate = (data: Room) => {
  room.value = data
}

const initializeStompClient = () => {
  const socket = new SockJS('http://localhost:8080/ws')
  const client = Stomp.over(socket)

  client.connect({}, () => {
    stompClient.value = client
    subscribeToRoom()
  })
}

const subscribeToRoom = () => {
  const roomId = props.id
  if (!roomId) return
  if (!stompClient.value) return
  stompClient.value.subscribe(`/topic/room/${roomId}`, (data: any) => {
    const roomData: Room = JSON.parse(data.body)
    handleRoomUpdate(roomData)
  })
}
</script>

<template>
  <div class="absolute h-full">
    <div class="relative h-screen w-screen">
      <div
        class="absolute inset-0"
        :class="getUserTeam === 'BLUE' ? 'first-layer-blue' : 'first-layer-red'"
      ></div>
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
                <Clue v-if="room?.status === 'IN_PROGRESS'" :room="room" />
                <Board v-if="room?.status === 'IN_PROGRESS'" :room="room" />
                <Config
                  v-if="room?.status === 'PENDING' && isHost"
                  :room="room"
                  :is-host="isHost"
                />
                <RedSide :room="room" />
                <div class="right flex flex-col">
                  <BlueSide :room="room" />
                  <History :room="room" />
                  <SocialMedia />
                </div>
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
    </div>
  </div>
</template>

<style scoped>
.view {
  width: 100vw;
  height: 100vh;
}
.first-layer-red {
  z-index: -1;
  background: radial-gradient(circle at 50% 50%, rgb(231, 102, 60) 0%, rgb(72, 12, 2) 100%);
}

.first-layer-blue {
  z-index: -1;
  background: radial-gradient(circle at 50% 50%, rgb(91, 174, 215) 0%, rgb(5, 60, 84) 100%);
}

.second-layer {
  z-index: -1;
  background-image: url('https://cdn.codenames.game/v20210210/img/bg-raster.svg');
  mix-blend-mode: overlay;
}

#scaler {
  position: relative;
  border: 0px solid transparent;
  transform: scale(1);
}

/* #gamescene {
  position: absolute;
  left: 50%;
  top: 0px;
  transform: translateX(-50%);
  width: 1920px;
  height: 1080px;
} */

.right {
  position: absolute;
  top: 150px;
  right: 20px;
  width: 340px;
  height: 900px;
}
</style>
