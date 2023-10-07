<script setup lang="ts">
import type { Room, Player } from '@/types/types'
import { apiFetchData } from '@/utils/api'
import { computed } from 'vue'

const props = defineProps<{
  room: Room
  user: Player | null
}>()

const redAgents = computed(() => {
  if (!props.room) return []
  return props.room.players.filter(
    (player) => player.playerTeam === 'RED' && player.playerRole === 'OPERATIVE'
  )
})

const redSpymaster = computed(() => {
  if (!props.room) return []
  return props.room.players.filter(
    (player) => player.playerTeam === 'RED' && player.playerRole === 'SPYMASTER'
  )
})

const nonePlayers = computed(() => {
  if (!props.room) return []
  return props.room.players.filter((player) => player.playerTeam === 'NONE')
})

const joinRole = async (role: string) => {
  if (props.user?.playerTeam === 'BLUE') return
  try {
    const roomId = props.room.id
    await apiFetchData(`room/${roomId}`, 'PUT', {
      action: 'select-role',
      username: props.user?.name,
      role,
      team: 'RED'
    })
  } catch (error) {
    console.error('Error:', error)
  }
}
</script>

<template>
  <div class="left flex flex-col">
    <div
      class="flex-1 landscape:flex-none landscape:rounded-xl landscape:border-ui landscape:shadow-bottom bg-red-team-bg"
    >
      <div class="w-full p-2 box-border">
        <section class="relative h-12 landscape:h-28">
          <span
            class="score w-12 text-center absolute text-white top-6 landscape:top-14 right-[20px]"
            >{{ room.redRemainingWords || '-' }}</span
          >
        </section>
        <section>
          <span class="mt-1 text-base w-full relative text-red-light">Agents</span>
          <div v-if="redAgents.length" class="flex justify-start items-start flex-wrap">
            <div
              v-for="(user, i) in redAgents"
              :key="i"
              class="user-wrapper inline-block py-1 px-1 mr-1 mb-1 truncate text-white border border-white/40 rounded leading-none font-bold"
            >
              {{ user.name }}
            </div>
          </div>
          <div v-else class="pl-2 text-white">–</div>
          <button
            v-if="
              nonePlayers.length && (user?.playerTeam === 'RED' || user?.playerRole === 'OPERATIVE')
            "
            class="button shadow-bottom text-base"
            @click="joinRole('OPERATIVE')"
          >
            Rejoindre en tant qu'agent
          </button>
        </section>
        <section>
          <span class="mt-1 text-base w-full relative text-red-light">Espions</span>
          <div v-if="redSpymaster.length" class="flex justify-start items-start flex-wrap">
            <div
              v-for="(user, i) in redSpymaster"
              :key="i"
              class="user-wrapper inline-block py-1 px-1 mr-1 mb-1 truncate text-white border border-white/40 rounded leading-none font-bold"
            >
              {{ user.name }}
            </div>
          </div>
          <div v-else class="pl-2 text-white">–</div>
          <button
            v-if="
              !redSpymaster.length && (user?.playerTeam === 'RED' || user?.playerTeam === 'NONE')
            "
            class="button shadow-bottom text-base"
            @click="joinRole('SPYMASTER')"
          >
            Rejoindre en tant qu'espion
          </button>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
.left {
  position: absolute;
  top: 150px;
  left: 20px;
  width: 340px;
  height: 900px;
}

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
@/stores/userStore
