<script setup lang="ts">
import type { Player, Room } from '@/types/types'
import { apiFetchData } from '@/utils/api'
import { ref } from 'vue'

const props = defineProps<{
  id: number
  users: Player[]
  isHost: boolean
  roomStatus: Room['status']
}>()

const isPlayerMenuOpen = ref<number | null>(null)

const togglePlayerMenu = (playerIndex: number): void => {
  isPlayerMenuOpen.value = isPlayerMenuOpen.value === playerIndex ? null : playerIndex
}

const copyLink = (): void => {
  const link = document.getElementById('link') as HTMLInputElement
  link.select()
  document.execCommand('copy')
}

const shufflePlayers = async (): Promise<void> => {
  if (!props.isHost) return
  try {
    await apiFetchData(`room/${props.id}`, 'PUT', { action: 'shuffle-players' })
  } catch (error) {
    console.error(error)
  }
}

const resetPlayers = async (): Promise<void> => {
  if (!props.isHost) return
  try {
    await apiFetchData(`room/${props.id}`, 'PUT', { action: 'reset-players' })
  } catch (error) {
    console.error(error)
  }
}

const changeHost = async (name: string): Promise<void> => {
  if (!props.isHost) return
  try {
    await apiFetchData(`room/${props.id}`, 'PUT', { action: 'change-host', username: name })
  } catch (error) {
    console.error(error)
  }
}

const kickPlayer = async (name: string): Promise<void> => {
  if (!props.isHost) return
  try {
    await apiFetchData(`room/${props.id}`, 'PUT', { action: 'leave', username: name })
  } catch (error) {
    console.error(error)
  }
}
</script>

<template>
  <div class="menu-wrapper">
    <div class="border-ui rounded-xl bg-white shadow-bottom">
      <div class="flex flex-col items-center p-4">
        <h3 class="mb-2 text-center font-bold text-blue-700">
          Invitez des joueurs en leur envoyant ce lien :
        </h3>
        <input
          id="link"
          type="text"
          :value="`http://localhost:8080/room/${props.id}`"
          class="mb-1 w-[80%] rounded-xl border p-2 text-center text-base text-black shadow-inset"
        />
        <button @click="copyLink" class="button text-base shadow-bottom">
          Copier le lien dans le presse-papier.
        </button>
      </div>
      <hr class="border-gray-300" />
      <div class="bg-gray-200 px-4 pb-2 pt-4">
        <h3 class="mb-2 text-center font-bold">Joueurs dans ce salon</h3>
        <div class="flex flex-wrap items-start justify-start">
          <div class="relative" v-for="(player, i) in users" :key="i">
            <button
              class="m-1 inline-flex cursor-default items-center justify-start rounded border-2 border-white bg-white px-1.5 py-0.5 font-bold italic text-black outline-none hover:outline-none active:outline-none"
              @click="togglePlayerMenu(i)"
              :class="{
                'hover:cursor-pointer hover:bg-yellow': isHost && i !== 0
              }"
            >
              <span class="mr-1 h-2 w-2 rounded-full bg-green-online"></span>
              <span>{{ player.name }}</span>
            </button>
            <div v-if="isPlayerMenuOpen === i && i !== 0 && isHost" class="player-details">
              <div class="border-ui rounded-xl bg-white px-2 py-4 shadow-bottom">
                <h3 class="mb-2 text-center text-base font-bold">{{ player.name }}</h3>
                <div class="flex flex-wrap justify-center">
                  <div class="m-0.5">
                    <button class="button text-base shadow-bottom" @click="changeHost(player.name)">
                      Faire devenir hôte
                    </button>
                  </div>
                  <div class="m-0.5">
                    <button class="button text-base shadow-bottom" @click="kickPlayer(player.name)">
                      Expulser le joueur
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template v-if="isHost && roomStatus === 'PENDING'">
        <hr class="border-gray-300" />
        <div class="rounded-bl-xl rounded-br-xl bg-gray-200 p-2 text-center">
          <div class="m-2 inline-block">
            <button @click="shufflePlayers" class="button">Répartir les équipes au hasard</button>
          </div>
          <div class="m-2 inline-block">
            <button @click="resetPlayers" class="button">Réinitialiser les équipes</button>
          </div>
        </div>
      </template>
      <div v-else class="rounded-bl-xl rounded-br-xl bg-gray-200 p-2 text-center"></div>
    </div>
  </div>
</template>

<style scoped>
.menu-wrapper {
  z-index: 9999;
  position: absolute;
  width: 380px;
  inset: 0px auto auto 0px;
  transform: translateX(-10px) translateY(45px) translateZ(0px);
}

@media screen and (max-width: 500px) {
  .menu-wrapper {
    transform: translateX(-1px) translateY(45px) translateZ(0px);
  }
}

.player-details {
  z-index: 9999;
  position: absolute;
  width: 350px;
  inset: 0px auto auto 0px;
  transform: translateX(-90px) translateY(40px) translateZ(0px);
}
</style>
