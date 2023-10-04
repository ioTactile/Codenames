<script setup lang="ts">
import type { Player } from '@/types/types'
import { apiFetchData } from '@/utils/api'

const props = defineProps<{
  id: number
  players: Player[]
  isHost: boolean
}>()

const copyLink = () => {
  const link = document.getElementById('link') as HTMLInputElement
  link.select()
  document.execCommand('copy')
}

const shufflePlayers = async () => {
  try {
    await apiFetchData(`room/${props.id}`, 'PUT', { action: 'shuffle-players' })
  } catch (error) {
    console.error('Error:', error)
  } finally {
    alert('Les équipes ont été réparties au hasard.')
  }
}

const resetPlayers = async () => {
  try {
    await apiFetchData(`room/${props.id}`, 'PUT', { action: 'reset-players' })
  } catch (error) {
    console.error('Error:', error)
  } finally {
    alert('Les équipes ont été réinitialisées.')
  }
}
</script>

<template>
  <div class="menu-wrapper">
    <div class="bg-white rounded-xl shadow-bottom border-ui">
      <div class="flex flex-col items-center p-4">
        <h3 class="text-blue-700 text-center font-bold mb-2">
          Invitez des joueurs en leur envoyant ce lien :
        </h3>
        <input
          id="link"
          type="text"
          :value="`http://localhost:8080/room/${props.id}`"
          class="text-center text-base rounded-xl shadow-inset text-black mb-1 w-[80%] p-2 border"
        />
        <button @click="copyLink" class="button">Copier le lien dans le presse-papier.</button>
      </div>
      <hr class="border-gray-300" />
      <div class="px-4 pt-4 pb-2 bg-gray-200">
        <h3 class="text-center font-bold mb-2">Joueurs dans ce salon</h3>
        <div class="flex justify-start items-start flex-wrap">
          <div class="relative">
            <button
              v-for="(player, i) in players"
              :key="i"
              class="inline-flex justify-start items-center py-0.5 px-1.5 m-1 outline-none hover:outline-none active:outline-none border-2 border-white rounded bg-white text-black italic cursor-default font-bold"
            >
              <span class="w-2 h-2 mr-1 rounded-full bg-green-online"></span>
              <span>{{ player.name }}</span>
            </button>
          </div>
        </div>
      </div>
      <template v-if="isHost">
        <hr class="border-gray-300" />
        <div class="p-2 bg-gray-200 text-center rounded-bl-xl rounded-br-xl">
          <div class="m-2 inline-block">
            <button @click="shufflePlayers" class="button">Répartir les équipes au hasard</button>
          </div>
          <div class="m-2 inline-block">
            <button @click="resetPlayers" class="button">Réinitialiser les équipes</button>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.menu-wrapper {
  z-index: 9999;
  position: absolute;
  width: 380px;
  inset: 0px auto auto 0px;
  transform: translateX(-10px) translateY(45px);
}
</style>
