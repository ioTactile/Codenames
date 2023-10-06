<script setup lang="ts">
import type { Room, Word } from '@/types/types'
import { useRoomUserStore } from '@/stores/roomUser'
import { computed } from 'vue'
import { apiFetchData } from '@/utils/api'

const props = defineProps<{
  room: Room | null
}>()

const roomUserStore = useRoomUserStore()

const getUsernameStore = () => {
  if (!props.room) return null
  return roomUserStore.getRoomUser(props.room!.id)?.username
}

const isUserSpy = computed(() => {
  return !!props.room?.players.find(
    (player) => player.name === getUsernameStore() && player.playerRole === 'SPYMASTER'
  )
})

const isUserTeamTurn = computed(() => {
  return !!props.room?.players.find(
    (player) => player.name === getUsernameStore() && player.playerTeam === props.room?.teamTurn
  )
})

const clickWord = async (word: Word) => {
  if (isUserSpy.value) return
  if (!isUserTeamTurn.value) return
  const roomId = props.room?.id
  try {
    await apiFetchData(`room/${roomId}`, 'PUT', {
      action: 'click-word',
      username: getUsernameStore(),
      wordname: word.wordName
    })
  } catch (error) {
    console.error('Error:', error)
  }
}

const selectWord = async (word: Word) => {
  if (isUserSpy.value) return
  if (!isUserTeamTurn.value) return
  const roomId = props.room?.id
  try {
    await apiFetchData(`room/${roomId}`, 'PUT', {
      action: 'select-word',
      username: getUsernameStore(),
      wordname: word.wordName
    })
  } catch (error) {
    console.error('Error:', error)
  }
}
</script>

<template>
  <div class="center">
    <div class="grid grid-cols-5 gap-y-1 gap-x-2">
      <div v-for="(word, i) in room?.words" :key="i">
        <div
          class="card-image relative"
          :class="{
            red: word.wordColor === 'RED' && isUserSpy,
            white: !isUserSpy || (word.wordColor === 'WHITE' && isUserSpy),
            blue: word.wordColor === 'BLUE' && isUserSpy,
            black: word.wordColor === 'BLACK' && isUserSpy
          }"
          @click="selectWord(word)"
        >
          <div
            class="font-fira flex justify-center items-end uppercase whitespace-nowrap break-all font-bold text-3xl h-full pb-5"
            :class="word.wordColor === 'BLACK' && isUserSpy ? 'text-white' : 'text-black'"
          >
            {{ word.wordName }}
          </div>
          <div v-for="(player, i) in word.selectedBy" :key="i">
            <div class="tips-wrapper absolute flex flex-wrap">
              <div
                class="inline-block rounded-sm p-px px-1 mr-0.5 mb-0.5 text-white truncate text-xxs leading-none landscape:text-sm"
                :class="{
                  'bg-blue-team-bg': room?.teamTurn === 'BLUE',
                  'bg-red-team-bg': room?.teamTurn === 'RED'
                }"
              >
                {{ player }}
              </div>
            </div>
          </div>
          <button
            @click="clickWord(word)"
            class="click-button absolute shadow-bottom bg-yellow rounded-full pointer-events-auto"
          ></button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.center {
  position: absolute;
  top: 150px;
  left: 50%;
  transform: translateX(-50%);
}

.card-image {
  width: 208.32px;
  height: 134.4px;
  background-image: url('/images/fronts.png');
  background-size: 100%;
  background-repeat: no-repeat;
}

.click-button {
  right: -4.2px;
  top: -2.1px;
  width: 49px;
  height: 49px;
  background-image: url('/images/icon_tap_card.png');
  background-size: 100%;
}

.tips-wrapper {
  top: 11.2px;
  left: 10.85px;
  width: 184.45px;
}

.tips-wrapper > div {
  max-width: calc(50% - 0.125rem);
}

.red {
  background-position-y: 100%;
}

.white {
  background-position-y: 50%;
}

.blue {
  background-position-y: 25%;
}

.black {
  background-position-y: 0%;
}
</style>
