<script setup lang="ts">
import type { Player } from '@/types/types'
import { useRoomUserStore } from '@/stores/roomUser'
import { onMounted, ref, watch } from 'vue'
import { apiFetchData } from '@/utils/api'
import { useRouter } from 'vue-router'

const props = defineProps<{
  id: number
  player: Player | null | undefined
}>()

const router = useRouter()

const getRoomUser = () => {
  return roomUserStore.getRoomUser(props.id)?.username
}

const roomUserStore = useRoomUserStore()
const username = ref<string>(getRoomUser() || '')
const isURLHidden = ref<boolean>(false)

onMounted(() => {
  if (localStorage.getItem('url-hidden')) {
    isURLHidden.value = true
  }
})

watch(isURLHidden, (value) => {
  if (value) {
    const urlArrayJSON = JSON.stringify([
      {
        urlHidden: 'hidden-1',
        urlShown: props.id
      }
    ])
    localStorage.setItem('url-hidden', urlArrayJSON)
    history.pushState({}, '', '/room/hidden-1')
  } else {
    localStorage.removeItem('url-hidden')
    history.pushState({}, '', `/room/${props.id}`)
  }
})

const changeUsername = async () => {
  if (!getRoomUser()) {
    alert("Vous n'êtes pas dans ce salon")
    router.push({ name: 'home' })
  }
  if (!username.value) {
    alert('Veuillez choisir un pseudo')
    return
  }

  try {
    await apiFetchData(`room/${props.id}`, 'PUT', {
      action: 'change-username',
      username: getRoomUser(),
      newUsername: username.value
    })
    roomUserStore.setRoomUser(props.id, username.value)
  } catch (error) {
    console.error('Error:', error)
  }
}

const leaveRoom = async () => {
  try {
    await apiFetchData(`room/${props.id}`, 'PUT', {
      action: 'leave',
      username: getRoomUser()
    })
    roomUserStore.removeRoomUser(props.id, username.value)
    router.push({ name: 'home' })
  } catch (error) {
    console.error('Error:', error)
  }
}
</script>

<template>
  <div class="menu-wrapper">
    <div class="rounded-xl bg-white shadow-bottom border-ui">
      <div class="flex flex-col items-center justify-center p-4">
        <h3 class="my-2 text-xl text-center">Rejoignez une équipe !</h3>
      </div>
      <hr class="border-gray-300" />
      <div class="py-4 px-2 bg-gray-200">
        <div class="text-center px-2">
          <div class="mb-1">
            <label class="block" for="username-input">Pseudo</label>
            <input
              v-model="username"
              type="text"
              id="username-input"
              placeholder="Choisissez votre pseudo"
              class="text-center text-base rounded-xl shadow-inset text-black mb-1 py-2 border"
            />
          </div>
          <button class="button" @click="changeUsername">Changer de pseudo</button>
        </div>
      </div>
      <hr class="border-gray-300" />
      <div class="p-4 bg-gray-200">
        <div class="flex justify-start items-center cursor-pointer">
          <div class="w-14 mr-4">
            <div class="w-full relative">
              <label class="switch">
                <input v-model="isURLHidden" type="checkbox" />
                <span class="slider round"></span>
              </label>
            </div>
          </div>
          <p class="flex-1">Cacher l'URL du salon dans la barre d'adresse</p>
        </div>
      </div>
      <hr class="border-gray-300" />
      <div class="flex justify-center py-4 bg-gray-200 rounded-bl-xl rounded-br-xl">
        <button class="button shadow-bottom text-base" @click="leaveRoom">
          <div class="flex items-center justify-center">
            <svg
              class="flex-none w-5 mr-2 text-black fill-current"
              xmlns="http://www.w3.org/2000/svg"
              x="0px"
              y="0px"
              viewBox="0 0 48 48"
            >
              <g>
                <polygon
                  points="39.3,24.1 26.1,24.1 26.1,17.4 39.3,17.4 39.3,12.4 47,20.7 39.3,29.1 "
                ></polygon>
                <polygon points="19.2,47 2.5,39 2.5,2.5 19.2,10.4"></polygon>
              </g>
              <g>
                <path
                  d="M32.1,40.5H2.5C1.7,40.5,1,39.8,1,39V2.5C1,1.7,1.7,1,2.5,1h29.6c0.8,0,1.5,0.7,1.5,1.5v9.2    c0,0.8-0.7,1.5-1.5,1.5c-0.8,0-1.5-0.7-1.5-1.5V3.9H4v33.7h26.6v-7.7c0-0.8,0.7-1.5,1.5-1.5c0.8,0,1.5,0.7,1.5,1.5V39    C33.5,39.8,32.9,40.5,32.1,40.5z"
                ></path>
              </g>
            </svg>
            <div class="flex-1">Quitter le salon</div>
          </div>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.menu-wrapper {
  z-index: 9999;
  position: absolute;
  width: 360px;
  inset: 0px auto auto 0px;
  transform: translateX(-265px) translateY(45px) translateZ(0px);
}

.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: 0.4s;
  transition: 0.4s;
}

.slider:before {
  position: absolute;
  content: '';
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: 0.4s;
  transition: 0.4s;
}

input:checked + .slider {
  background-color: #fee400;
}

input:focus + .slider {
  box-shadow: 0 0 1px #fee400;
}

input:checked + .slider:before {
  -webkit-transform: translateX(26px);
  -ms-transform: translateX(26px);
  transform: translateX(26px);
}

.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
}
</style>
