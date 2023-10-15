<script setup lang="ts">
import type { Room, Player } from '@/types/types'
import { apiFetchData } from '@/utils/api'
import { computed } from 'vue'

const props = defineProps<{
  room: Room
  user: Player | null
}>()

const blueAgents = computed((): Player[] => {
  if (!props.room) return []
  return props.room.players.filter(
    (player) => player.playerTeam === 'BLUE' && player.playerRole === 'OPERATIVE'
  )
})

const blueSpymaster = computed((): Player[] => {
  if (!props.room) return []
  return props.room.players.filter(
    (player) => player.playerTeam === 'BLUE' && player.playerRole === 'SPYMASTER'
  )
})

const noRolePlayer = computed((): boolean => {
  if (!props.room) return false
  return props.room.players.some(
    (player) => props.user?.name === player.name && player.playerRole === 'NONE'
  )
})

const joinRole = async (role: string): Promise<void> => {
  if (props.user?.playerTeam === 'RED') return
  try {
    const roomId = props.room.id
    await apiFetchData(`room/${roomId}`, 'PUT', {
      action: 'select-role',
      username: props.user?.name,
      role,
      team: 'BLUE'
    })
  } catch (error) {
    console.error(error)
  }
}
</script>

<template>
  <div
    class="landscape:border-ui flex-1 overflow-y-auto bg-blue-team-bg landscape:flex-none landscape:rounded-xl landscape:shadow-bottom"
  >
    <div class="box-border w-full p-2">
      <section class="relative h-12 landscape:h-28">
        <span
          class="score absolute left-[20px] top-6 w-12 text-center text-white landscape:top-14"
          >{{ room.blueRemainingWords || '-' }}</span
        >
      </section>
      <section>
        <span class="relative mt-1 w-full text-base text-blue-light">Agents</span>
        <div v-if="blueAgents.length" class="flex flex-wrap items-start justify-start">
          <div
            v-for="(user, i) in blueAgents"
            :key="i"
            class="user-wrapper mb-1 mr-1 inline-block truncate rounded border border-white/40 px-1 py-1 font-bold leading-none text-white"
          >
            {{ user.name }}
          </div>
        </div>
        <div v-else class="pl-2 text-white">–</div>
        <button
          v-if="noRolePlayer && (user?.playerTeam === 'RED' || user?.playerTeam === 'NONE')"
          class="button text-base shadow-bottom"
          @click="joinRole('OPERATIVE')"
        >
          Rejoindre en tant qu'agent
        </button>
      </section>
      <section>
        <span class="relative mt-1 w-full text-base text-blue-light">Espions</span>
        <div v-if="blueSpymaster.length" class="flex flex-wrap items-start justify-start">
          <div
            v-for="(user, i) in blueSpymaster"
            :key="i"
            class="user-wrapper mb-1 mr-1 inline-block truncate rounded border border-white/40 px-1 py-1 font-bold leading-none text-white"
          >
            {{ user.name }}
          </div>
        </div>
        <div v-else class="pl-2 text-white">–</div>
        <button
          v-if="
            !blueSpymaster.length && (user?.playerTeam === 'BLUE' || user?.playerTeam === 'NONE')
          "
          class="button text-base shadow-bottom"
          @click="joinRole('SPYMASTER')"
        >
          Rejoindre en tant qu'espion
        </button>
      </section>
    </div>
  </div>
</template>

<style scoped>
.score {
  font-size: 50px;
  font-weight: bold;
  transform: translateY(-50%);
  z-index: 10;
  text-shadow: rgba(0, 0, 0, 0.8) 0px 0px 0.8rem;
  font-feature-settings: 'tnum';
  font-variant-numeric: tabular-nums;
}

.user-wrapper {
  scale: 1;
  transform: translate3d(0, 0, 0);
  transform-origin: 50% 50% 0px;
}
</style>
