<script setup lang="ts">
import type { Player, Room } from '@/types/types'
import { useUserStore } from '@/stores/user'
import { onMounted, ref, watch } from 'vue'
import { apiFetchData } from '@/utils/api'
import { useRouter } from 'vue-router'

const props = defineProps<{
  id: number
  status: Room['status']
  user: Player | null
}>()

const router = useRouter()

const userStore = useUserStore()
const username = ref<string>(props.user?.name || '')
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

const changeUsername = async (): Promise<void> => {
  if (!username.value) {
    alert('Veuillez choisir un pseudo')
    return
  }

  try {
    await apiFetchData(`room/${props.id}`, 'PUT', {
      action: 'change-username',
      username: props.user?.name,
      newUsername: username.value
    })
    userStore.setUser(props.id, username.value)
  } catch (error) {
    console.error(error)
  }
}

const leaveRoom = async (): Promise<void> => {
  if (props.status !== 'PENDING') return
  try {
    await apiFetchData(`room/${props.id}`, 'PUT', {
      action: 'leave',
      username: props.user?.name
    })
    userStore.removeUser(props.id, username.value)
    router.push({ name: 'home' })
  } catch (error) {
    console.error(error)
  }
}

const selectTeam = async (team: string): Promise<void> => {
  if (props.status !== 'PENDING') return
  if (props.user?.playerTeam === 'NONE') return
  try {
    await apiFetchData(`room/${props.id}`, 'PUT', {
      action: 'select-team',
      username: props.user?.name,
      team
    })
  } catch (error) {
    console.error(error)
  }
}
</script>

<template>
  <div class="menu-wrapper">
    <div class="border-ui rounded-xl bg-white shadow-bottom">
      <template v-if="status === 'PENDING'">
        <div class="flex flex-col items-center justify-center p-4">
          <h3 v-if="user?.playerTeam === 'NONE'" class="my-2 text-center text-xl">
            Rejoignez une équipe !
          </h3>
          <template v-else>
            <button
              class="button-wrapper button shadow-button color-beige text-base"
              @click="selectTeam('NONE')"
            >
              <img src="/images/icon_spectator.png" alt="Spectator icon" class="h-7 w-7" />
              <div class="ml-1">Devenir spectateur</div>
            </button>
            <div class="mt-2">
              <button
                v-if="user?.playerTeam === 'BLUE'"
                class="button-wrapper button shadow-button color-red text-base"
                @click="selectTeam('RED')"
              >
                <img
                  src="/images/icon_red_operative.png"
                  alt="Red operative icon"
                  class="h-7 w-7"
                />
                <div class="ml-1">Passer dans l'équipe rouge</div>
              </button>
              <button
                v-else-if="user?.playerTeam === 'RED'"
                class="button-wrapper button shadow-button color-blue text-base"
                @click="selectTeam('BLUE')"
              >
                <img
                  src="/images/icon_blue_operative.png"
                  alt="Blue operative icon"
                  class="h-7 w-7"
                />
                <div class="ml-1">Passer dans l'équipe bleue</div>
              </button>
            </div>
          </template>
        </div>
        <hr class="border-gray-300" />
      </template>
      <div
        class="bg-gray-200 px-2 py-4"
        :class="{
          'rounded-xl': status !== 'PENDING'
        }"
      >
        <div class="px-2 text-center">
          <div class="mb-1">
            <label class="block" for="username-input">Pseudo</label>
            <input
              v-model="username"
              type="text"
              id="username-input"
              placeholder="Choisissez votre pseudo"
              class="mb-1 rounded-xl border py-2 text-center text-base text-black shadow-inset"
            />
          </div>
          <button class="button" @click="changeUsername">Changer de pseudo</button>
        </div>
      </div>
      <hr class="border-gray-300" />
      <div class="bg-gray-200 p-4">
        <div class="flex cursor-pointer items-center justify-start">
          <div class="mr-4 w-14">
            <div class="relative w-full">
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
      <div class="flex justify-center rounded-bl-xl rounded-br-xl bg-gray-200 py-4">
        <button class="button text-base shadow-bottom" @click="leaveRoom">
          <div class="flex items-center justify-center">
            <svg
              class="mr-2 w-5 flex-none fill-current text-black"
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
  transform: translateX(-245px) translateY(45px) translateZ(0px);
}

@media screen and (max-width: 500px) {
  .menu-wrapper {
    transform: translateX(-275px) translateY(45px) translateZ(0px);
  }
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

.button-wrapper {
  display: flex !important;
  align-items: center;
}
</style>
