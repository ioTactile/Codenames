<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { Room } from '@/types/types'
import Nav from '@/components/RoomNav.vue'
import TimerModal from '@/components/RoomTimerModal.vue'
import { apiFetchData } from '@/utils/api'

const room = ref<Room | null>(null)
const props = defineProps<{
  id: string
}>()

const isTimerMenuOpen = ref(false)

onMounted(async () => {
  const roomId = props.id
  if (!roomId) {
    alert('No room id provided')
    return
  }

  try {
    const data: Room = await apiFetchData(`room/${roomId}`, 'GET')
    room.value = data
    console.log('data', data)
  } catch (error) {
    console.error('Error:', error)
  }
})
</script>

<template>
  <div class="first-layer absolute inset-0 transition-all"></div>
  <div class="second-layer bg-cover absolute inset-0"></div>
  <div id="scaler">
    <div id="gamescene">
      <main>
        <Nav :room="room" @open-timer-menu="isTimerMenuOpen = $event" />
      </main>
    </div>
  </div>
  <TimerModal :isTimerMenuOpen="isTimerMenuOpen" @close-timer-menu="isTimerMenuOpen = $event" />
</template>

<style scoped>
.first-layer {
  z-index: -1;
  background: radial-gradient(circle at 50% 50%, rgb(231, 102, 60) 0%, rgb(72, 12, 2) 100%);
}

.second-layer {
  z-index: -1;
  background-image: url('https://cdn.codenames.game/v20210210/img/bg-raster.svg');
  mix-blend-mode: overlay;
}

#scaler {
  position: relative;
  transform: scale(calc(var(--vh, 1vh) / 0.01));
}

#gamescene {
  position: absolute;
  left: 50%;
  top: 0px;
  transform: translateX(-50%);
  width: 1920px;
  height: 1080px;
}
</style>
