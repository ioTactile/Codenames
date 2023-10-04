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
  } catch (error) {
    console.error('Error:', error)
  } finally {
    roomUserStore.setRoomUser(props.id, username.value)
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
    </div>
  </div>
</template>

<style scoped>
.menu-wrapper {
  z-index: 9999;
  position: absolute;
  width: 360px;
  inset: 0px auto auto 0px;
  transform: translateX(-265px) translateY(45px);
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
