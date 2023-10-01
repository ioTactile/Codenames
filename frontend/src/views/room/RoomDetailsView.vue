<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { Player, Room } from '@/types/types'
import RoomPlayers from '@/components/room/PlayersSettings.vue'
import { apiFetchData } from '@/utils/api'

const room = ref<Room | null>(null)
const props = defineProps<{
  id: string
}>()

onMounted(async () => {
  const roomId = props.id
  if (!roomId) {
    alert('No room id provided')
    return
  }

  try {
    const data: Room = await apiFetchData(`room/${roomId}`, 'GET')
    room.value = data
  } catch (error) {
    console.error('Error:', error)
  }
})

const getNames = (players: Player[]): string[] => {
  const names = []
  for (const player of players) {
    names.push(player.name)
  }
  return names
}
</script>

<template>
  <div v-if="room">
    <RoomPlayers :id="props.id.toString()" :players="getNames(room.players)" />
  </div>
</template>
