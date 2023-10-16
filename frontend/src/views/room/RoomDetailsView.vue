<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
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
import RulesModal from '@/components/RulesModal.vue'
import Replay from '@/components/RoomReplay.vue'
import { apiFetchData } from '@/utils/api'
import { useUserStore } from '@/stores/user'
// import { useUrlStore } from '@/stores/url'
// import { storeToRefs } from 'pinia'
import { CompatClient, Stomp } from '@stomp/stompjs'
import SockJS from 'sockjs-client/dist/sockjs.min.js'
import { useWindowSize } from '@vueuse/core'

const props = defineProps<{
  id: number
}>()

const userStore = useUserStore()
// const urlStore = useUrlStore()
// const { urlId, isUrlHidden } = storeToRefs(urlStore)
const room = ref<Room | null>(null)
const isTimerMenuOpen = ref<boolean>(false)
const isRulesMenuOpen = ref<boolean>(false)
const isLoading = ref<boolean>(true)
const stompClient = ref<CompatClient | null>(null)
const isDisconnected = ref<boolean>(false)
const afkTimer = ref<number | undefined>(undefined)
const scale = ref<number>(1)
const { width: windowWidth, height: windowHeight } = useWindowSize()

onMounted(async () => {
  const roomId = props.id
  if (!roomId) return

  // urlStore.setUrl(roomId.toString())
  // history.pushState({}, '', `/room/${urlStore.getUrl()}`)

  try {
    const data: Room = await apiFetchData(`room/${roomId}`, 'GET')
    room.value = data
    console.log('data', data)
    isLoading.value = false

    const socket = new SockJS('http://localhost:8080/ws')
    stompClient.value = Stomp.over(socket)

    stompClient.value.connect({}, () => {
      if (!stompClient.value) return
      stompClient.value.subscribe(`/topic/room/${roomId}`, (response: any) => {
        const data = JSON.parse(response.body)
        room.value = data
      })
    })
    handleUserActivity()
  } catch (error) {
    console.error(error)
  }
})

// onBeforeUnmount(() => {
//   window.addEventListener('beforeunload', () => {
//     history.pushState({}, '', `/room/${props.id}`)
//   })
// })

onUnmounted(() => {
  clearTimeout(afkTimer.value)
  if (stompClient.value) {
    stompClient.value.disconnect()
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

const disconnnectOnAfk = () => {
  if (stompClient.value) {
    stompClient.value.disconnect(() => {
      isDisconnected.value = true
    })
  }
}

const handleUserActivity = () => {
  clearTimeout(afkTimer.value)
  afkTimer.value = setTimeout(disconnnectOnAfk, 600000)
}

const reconnectToWebsocket = () => {
  clearTimeout(afkTimer.value)
  const socket = new SockJS('http://localhost:8080/ws')
  stompClient.value = Stomp.over(socket)

  stompClient.value.connect({}, () => {
    if (!stompClient.value) return
    stompClient.value.subscribe(`/topic/room/${room.value?.id}`, (response: any) => {
      const data = JSON.parse(response.body)
      room.value = data
    })
  })
  isDisconnected.value = false
}

const handleResize = () => {
  const targetHeight = 1080
  const targetWidth = (16 / 9) * targetHeight

  if (window.innerWidth <= 500) {
    scale.value = 1
    return
  }
  scale.value = Math.min(windowHeight.value / targetHeight, windowWidth.value / targetWidth)
}
watch(
  [windowHeight, windowWidth],
  () => {
    handleResize()
  },
  { immediate: true }
)

// watch(isUrlHidden, (value) => {
//   if (value === false) {
//     urlId.value = props.id.toString()
//   }
//   history.pushState({}, '', `/room/${urlStore.getUrl()}`)
// })
</script>

<template>
  <template v-if="room">
    <div id="app">
      <div id="view">
        <div
          class="absolute inset-0"
          :class="user?.playerTeam === 'BLUE' ? 'first-layer-blue' : 'first-layer-red'"
        ></div>
        <div class="second-layer absolute inset-0 bg-cover"></div>
        <template v-if="isLoading">
          <main class="flex h-screen items-center justify-center px-2 font-bold text-white">
            Connexion au salon...
          </main>
        </template>
        <template v-else-if="!isLoading && !isDisconnected">
          <div id="scaler" :style="{ transform: `scale(${scale})` }">
            <div id="gamescene">
              <template v-if="user">
                <main class="h-full w-full">
                  <Nav
                    :room="room"
                    :is-host="isHost"
                    :user="user"
                    @open-timer-menu="isTimerMenuOpen = $event"
                    @open-rules-menu="isRulesMenuOpen = $event"
                  />
                  <Instructions :room="room" :is-host="isHost" :user="user" />
                  <Clue
                    v-if="room.status === 'IN_PROGRESS'"
                    :room="room"
                    :user="user"
                    :window-width="windowWidth"
                  />
                  <Board v-if="room.status !== 'PENDING'" :room="room" :user="user" />
                  <Config v-if="room.status === 'PENDING' && isHost" :room="room" />
                  <Replay
                    v-if="room.status === ('RED_TEAM_WINS' || 'BLUE_TEAM_WINS')"
                    :room="room"
                    :user="user"
                    :is-host="isHost"
                  />
                  <template v-if="windowWidth > 500">
                    <div class="left">
                      <RedSide :room="room" :user="user" />
                    </div>
                    <div class="right flex flex-col">
                      <BlueSide :room="room" :user="user" />
                      <History :room="room" />
                      <SocialMedia size="landscape" />
                    </div>
                  </template>
                  <template v-else>
                    <div class="bottom">
                      <RedSide :room="room" :user="user" />
                      <History :room="room" />
                      <BlueSide :room="room" :user="user" />
                    </div>
                  </template>
                </main>
              </template>
              <template v-else>
                <main class="flex h-screen items-center justify-center px-2">
                  <Join location="join" />
                </main>
              </template>
            </div>
          </div>
          <button class="absolute bottom-0 left-0 z-10">
            <img
              src="/images/button.png"
              alt="News button"
              class="pointer-events-none"
              :style="`height: ${scale * 150}px`"
            />
          </button>
        </template>
        <template v-else-if="!isLoading && isDisconnected">
          <div class="flex min-h-full items-center justify-center text-center text-white">
            <div class="max-w-screen-sm">
              <h1 class="mb-2 px-2 text-2xl font-bold">
                Vous avez été déconnecté pour cause d'inactivité.
              </h1>
              <p class="text-md mb-4 px-2">
                Si vous voulez continuer à jouer, vous pouvez vous reconnecter au salon.
              </p>
              <button class="button shadow-button text-base" @click="reconnectToWebsocket">
                Se reconnecter au salon
              </button>
            </div>
          </div>
        </template>
        <TimerModal
          :isTimerMenuOpen="isTimerMenuOpen"
          @close-timer-menu="isTimerMenuOpen = $event"
        />
        <RulesModal
          :isRulesMenuOpen="isRulesMenuOpen"
          @close-rules-menu="isRulesMenuOpen = $event"
        />
      </div>
    </div>
  </template>
</template>

<style scoped>
#app {
  position: absolute;
  width: 100%;
  height: 100%;
  transition: transform 200ms ease 0s;
  user-select: none;
  overflow: hidden;
}

#view {
  position: relative;
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
  transform-origin: top center;
  border: 0px solid transparent;
  transition: transform 200ms;
}

#gamescene {
  position: absolute;
  left: 50%;
  top: 0px;
  transform: translateX(-50%);
  width: 1920px;
  height: 1080px;
}

@media screen and (max-width: 500px) {
  #gamescene {
    width: 500px;
    min-height: 700px;
    max-height: 1080px;
  }
}

@media screen and (min-width: 501px) {
  .left {
    position: absolute;
    top: 150px;
    left: 20px;
    width: 340px;
    height: 900px;
  }
}

@media screen and (min-width: 501px) {
  .right {
    position: absolute;
    top: 150px;
    right: 20px;
    width: 340px;
    height: 900px;
  }
}

.bottom {
  position: absolute;
  bottom: 0px;
  width: 100%;
  height: 100%;
  transform: translateY(50%);
  display: flex;
  overflow: hidden;
}
</style>
