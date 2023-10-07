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
import { useUserStore } from '@/stores/user'
import { Stomp } from '@stomp/stompjs'
import SockJS from 'sockjs-client/dist/sockjs.min.js'

const props = defineProps<{
  id: number
}>()

const userStore = useUserStore()
const room = ref<Room | null>(null)
const isTimerMenuOpen = ref<boolean>(false)
const isLoading = ref<boolean>(true)

onMounted(async () => {
  const roomId = props.id
  if (!roomId) return

  try {
    const data: Room = await apiFetchData(`room/${roomId}`, 'GET')
    room.value = data
    console.log('data', data)
    isLoading.value = false

    const socket = new SockJS('http://localhost:8080/ws')
    const stompClient = Stomp.over(socket)

    stompClient.connect({}, () => {
      stompClient.subscribe(`/topic/room/${roomId}`, (response: any) => {
        const data = JSON.parse(response.body)
        room.value = data

        console.log('room updated', room.value)
      })
    })
  } catch (error) {
    console.error('Error:', error)
  }
})

const user = computed(() => {
  return (
    room.value?.players.find(
      (player) => player.name === userStore.getUser(room.value!.id)?.username
    ) ?? null
  )
})

const isHost = computed(() => {
  if (!room.value || !user.value) return false
  const host = room.value.players[0].name
  return host === user.value?.name
})
</script>

<template>
  <template v-if="room">
    <div class="absolute h-full">
      <div class="relative h-screen w-screen">
        <div
          class="absolute inset-0"
          :class="user?.playerTeam === 'BLUE' ? 'first-layer-blue' : 'first-layer-red'"
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
              <template v-if="user">
                <main>
                  <Nav
                    :room="room"
                    :is-host="isHost"
                    :user="user"
                    @open-timer-menu="isTimerMenuOpen = $event"
                  />
                  <Instructions :room="room" :is-host="isHost" :user="user" />
                  <Clue v-if="room.status === 'IN_PROGRESS'" :room="room" :user="user" />
                  <Board v-if="room.status === 'IN_PROGRESS'" :room="room" :user="user" />
                  <Config v-if="room.status === 'PENDING' && isHost" :room="room" />
                  <RedSide :room="room" :user="user" />
                  <div class="right flex flex-col">
                    <BlueSide :room="room" :user="user" />
                    <History :room="room" />
                    <SocialMedia />
                  </div>
                </main>
              </template>
              <template v-else>
                <main class="flex justify-center items-center h-screen px-2">
                  <Join location="join" />
                </main>
              </template>
            </template>
          </div>
        </div>
        <TimerModal
          :isTimerMenuOpen="isTimerMenuOpen"
          @close-timer-menu="isTimerMenuOpen = $event"
        />
      </div>
    </div>
  </template>
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
  transform: scale(0.8);
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
@/stores/userStore @/stores/roomUser
