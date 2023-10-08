<script setup lang="ts">
import type { Room, Word, Player } from '@/types/types'
import { computed } from 'vue'
import { apiFetchData } from '@/utils/api'

const props = defineProps<{
  room: Room
  user: Player | null
}>()

const isUserTeamTurn = computed(() => {
  return !!props.room.players.find(
    (player) => player.name === props.user?.name && player.playerTeam === props.room.teamTurn
  )
})

const isUserSpy = () => {
  return props.user?.playerRole === 'SPYMASTER'
}

const clickWord = async (word: Word) => {
  if (isUserSpy()) {
    alert("Vous êtes l'espion, vous ne pouvez pas cliquer sur les mots !")
    return
  }
  if (!isUserTeamTurn.value) return
  const roomId = props.room.id
  try {
    await apiFetchData(`room/${roomId}`, 'PUT', {
      action: 'click-word',
      username: props.user?.name,
      wordname: word.wordName
    })
  } catch (error) {
    console.error('Error:', error)
  }
}

const selectWord = async (word: Word) => {
  if (isUserSpy()) {
    alert("Vous êtes l'espion, vous ne pouvez pas cliquer sur les mots !")
    return
  }
  if (!isUserTeamTurn.value) return
  const roomId = props.room.id
  try {
    await apiFetchData(`room/${roomId}`, 'PUT', {
      action: 'select-word',
      username: props.user?.name,
      wordname: word.wordName
    })
  } catch (error) {
    console.error('Error:', error)
  }
}
</script>

<template>
  <div class="center">
    <div class="grid grid-cols-5 gap-x-2 gap-y-1">
      <div v-for="(word, i) in room.words" :key="i">
        <div
          class="card-image relative"
          :class="{
            red: word.wordColor === 'RED' && isUserSpy(),
            white: !isUserSpy() || (word.wordColor === 'WHITE' && isUserSpy()),
            blue: word.wordColor === 'BLUE' && isUserSpy(),
            black: word.wordColor === 'BLACK' && isUserSpy()
          }"
          @click="selectWord(word)"
        >
          <div
            class="flex h-full items-end justify-center whitespace-nowrap break-all pb-5 font-fira text-3xl font-bold uppercase"
            :class="word.wordColor === 'BLACK' && isUserSpy() ? 'text-white' : 'text-black'"
          >
            {{ word.wordName }}
          </div>
          <div v-for="(player, i) in word.selectedBy" :key="i">
            <div class="tips-wrapper absolute flex flex-wrap">
              <div
                class="text-xxs mb-0.5 mr-0.5 inline-block truncate rounded-sm p-px px-1 leading-none text-white landscape:text-sm"
                :class="{
                  'bg-blue-team-bg': room.teamTurn === 'BLUE',
                  'bg-red-team-bg': room.teamTurn === 'RED'
                }"
              >
                {{ player }}
              </div>
            </div>
          </div>
          <button
            @click.stop="clickWord(word)"
            class="click-button pointer-events-auto absolute z-50 rounded-full bg-yellow shadow-bottom"
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
@/stores/userStore @/stores/user
