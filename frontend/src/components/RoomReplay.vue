<script setup lang="ts">
import type { Player, Room } from '@/types/types'
import { apiFetchData } from '@/utils/api'
import { computed } from 'vue'

const props = defineProps<{
  room: Room
  user: Player | null
  isHost: boolean
}>()

const isTeamWin = computed((): string => {
  if (props.user?.playerTeam === 'RED' && props.room.status === 'RED_TEAM_WINS') {
    return 'Vous avez gagné !'
  } else if (props.user?.playerTeam === 'BLUE' && props.room.status === 'BLUE_TEAM_WINS') {
    return 'Vous avez gagné !'
  } else {
    return 'Vous avez perdu !'
  }
})

const details = computed((): string => {
  if (props.room.teamTurn === 'RED' && props.room.redRemainingWords === 0) {
    return "L'équipe rouge a trouvé tous les mots !"
  } else if (props.room.teamTurn === 'BLUE' && props.room.blueRemainingWords === 0) {
    return `L'équipe bleue a trouvé tous les mots !`
  }
  return `L'équipe ${props.room.teamTurn} a trouvé l'assassin !`
})

const replay = async (): Promise<void> => {
  if (props.room.status === 'IN_PROGRESS') return
  if (!props.isHost) return
  try {
    await apiFetchData(`room/${props.room.id}`, 'PUT', {
      action: 'replay',
      usernames: getUsernames()
    })
  } catch (error) {
    console.error(error)
  }
}

const getUsernames = (): String[] => {
  const usernames = props.room.players.map((player) => player.name)
  return usernames
}
</script>

<template>
  <div class="absolute top-[860px] z-70 w-full">
    <div class="flex flex-col items-center justify-center">
      <section class="rounded-xl bg-white px-4 py-2 pb-2">
        <h1 class="text-center text-lg font-bold landscape:text-2xl">{{ isTeamWin }}</h1>
        <p class="text-md text-center landscape:text-lg">{{ details }}</p>
      </section>
      <section class="mt-2" v-if="isHost">
        <button class="button text-base shadow-bottom" @click="replay">Refaire une partie</button>
      </section>
    </div>
  </div>
</template>
