<script setup lang="ts">
import type { Room, Player } from '@/types/types'
import { computed } from 'vue'

const props = defineProps<{
  room: Room
  isHost: boolean
  user: Player | null
}>()

const getInstructions = computed(() => {
  switch (props.room.status) {
    case 'PENDING':
      if (props.isHost) {
        return 'Configurer la partie :'
      } else {
        return "Choisissez votre rôle et attendez que l'hôte configure la partie."
      }
    case 'IN_PROGRESS':
      switch (props.room.teamTurn) {
        case 'RED':
          if (props.room.roleTurn === 'SPYMASTER') {
            return "L'espion rouge est en train de jouer."
          } else {
            return 'Les agents rouges sont en train de jouer.'
          }
        case 'BLUE':
          if (props.room.roleTurn === 'SPYMASTER') {
            return "L'espion bleu est en train de jouer."
          } else {
            return 'Les agents bleus sont en train de jouer.'
          }
        default:
          return 'Statut de la partie en cours non géré.'
      }
    case 'BLUE_TEAM_WINS':
      return "L'équipe bleue a gagné !"
    case 'RED_TEAM_WINS':
      return "L'équipe rouge a gagné !"
    default:
      return 'Statut de la partie non géré.'
  }
})

const getSupp = computed(() => {
  if (props.user?.playerRole === 'NONE') {
    return ' (Pour jouer, vous devez rejoindre une équipe.)'
  } else {
    return ''
  }
})
</script>

<template>
  <div class="relative flex items-center justify-center mx-auto h-16 w-full landscape:w-[1060px]">
    <div
      class="bg-white px-2 py-1 shadow-bottom rounded-lg text-base landscape:text-2xl font-bold mx-12 text-center"
    >
      <span>{{ getInstructions + getSupp }}</span>
    </div>
  </div>
</template>
