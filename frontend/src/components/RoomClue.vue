<script setup lang="ts">
import type { Room } from '@/types/types'
import { useRoomUserStore } from '@/stores/roomUser'
import { computed, ref, watch } from 'vue'
import { apiFetchData } from '@/utils/api'

const props = defineProps<{
  room: Room | null
}>()

const roomUserStore = useRoomUserStore()

const isClueModalOpen = ref<boolean>(false)
const clueNumber = ref<number>(0)
const clueName = ref<string>('')
const clueOptions = ref<number[]>([0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
const showClue = ref<boolean>(false)

const isUserSpyTurn = computed(() => {
  const userStore = roomUserStore.getRoomUser(props.room!.id)?.username
  const user = props.room?.players.find((player) => player.name === userStore)
  return !!(
    user?.playerRole === 'SPYMASTER' &&
    user?.playerTeam === props.room?.teamTurn &&
    props.room?.roleTurn === 'SPYMASTER' &&
    props.room?.status === 'IN_PROGRESS'
  )
})

const isUserOperativeTurn = computed(() => {
  const userStore = roomUserStore.getRoomUser(props.room!.id)?.username
  const user = props.room?.players.find((player) => player.name === userStore)
  return !!(
    user?.playerRole === 'OPERATIVE' &&
    user?.playerTeam === props.room?.teamTurn &&
    props.room?.roleTurn === 'OPERATIVE' &&
    props.room?.status === 'IN_PROGRESS'
  )
})

const getLastClue = computed(() => {
  const lastClue = props.room?.clues[props.room?.clues.length - 1]
  return lastClue
})

const getRoleTurn = () => {
  if (props.room?.roleTurn === 'OPERATIVE') {
    showClue.value = true
  } else {
    showClue.value = false
  }
}

watch(
  () => props.room?.roleTurn,
  () => {
    getRoleTurn()
  }
)

const sendClue = async () => {
  if (!isUserSpyTurn.value) return
  if (!clueName.value) return
  if (!clueNumber.value) return
  const roomId = props.room?.id
  if (!roomId) return

  try {
    await apiFetchData(`room/${roomId}`, 'PUT', {
      action: 'add-clue',
      clue: {
        clueName: clueName.value,
        attempts: clueNumber.value,
        remaining: clueNumber.value + 1,
        spyName: roomUserStore.getRoomUser(roomId)?.username
      },
      username: roomUserStore.getRoomUser(roomId)?.username
    })
  } catch (error) {
    console.error('Error:', error)
  }
}

const teamTurn = async () => {
  if (!isUserOperativeTurn.value) return
  const roomId = props.room?.id
  if (!roomId) return

  try {
    await apiFetchData(`room/${roomId}`, 'PUT', {
      action: 'team-turn'
    })
  } catch (error) {
    console.error('Error:', error)
  }
}
</script>

<template>
  <template v-if="isUserSpyTurn">
    <div class="absolute top-[860px] w-full z-70">
      <div class="w-full flex justify-end landscape:justify-center">
        <div
          class="max-w-screen-md flex items-center justify-center flex-wrap w-10/12 landscape:w-full"
        >
          <div class="flex-grow">
            <input
              v-model="clueName"
              name="clue"
              autocomplete="off"
              type="text"
              tabindex="0"
              placeholder="Tapez votre indice ici"
              @input="clueName = clueName.toUpperCase()"
              class="w-full text-2xl px-3 rounded-xl shadow-bottom focus:outline-none text-black border-none h-10"
            />
          </div>
          <div class="mx-2 flex-none">
            <div class="relative">
              <div
                class="flex justify-center items-center bg-white rounded-lg shadow-bottom border-ui w-10 h-10 text-2xl cursor-pointer"
                @click="isClueModalOpen = !isClueModalOpen"
              >
                {{ clueNumber || '–' }}
              </div>
              <div v-if="isClueModalOpen" class="clue-number-modal absolute z-50">
                <div class="flex p-2 bg-white rounded-lg shadow-bottom border-ui">
                  <div v-for="number in clueOptions" :key="number">
                    <div
                      class="clue-number-option hover:bg-yellow text-2xl rounded-lg leading-6 inline-block cursor-pointer"
                      @click="(clueNumber = number), (isClueModalOpen = false)"
                    >
                      {{ number }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="relative flex-initial">
            <button class="button color-green shadow-bottom text-2xl" @click="sendClue">
              Donner un indice
            </button>
          </div>
        </div>
      </div>
    </div>
  </template>
  <template v-if="room?.roleTurn === 'OPERATIVE'">
    <div class="bottom">
      <div class="w-full flex flex-col justify-center items-center text-xl landscape:text-3xl">
        <Transition name="slide-fade">
          <div class="flex justify-center items-center">
            <span
              :class="{
                'ring-blue-light': props.room?.teamTurn === 'BLUE',
                'ring-red-light': props.room?.teamTurn === 'RED'
              }"
              class="bg-white rounded-lg uppercase font-bold select-text ml-1 px-3 py-1 ring-4 landscape:px-4 landscape:py-2 landscape:ml-2 landscape:ring-8"
            >
              {{ getLastClue?.clueName }}
            </span>
            <span
              :class="{
                'ring-blue-light': props.room?.teamTurn === 'BLUE',
                'ring-red-light': props.room?.teamTurn === 'RED'
              }"
              class="bg-white rounded-lg uppercase font-bold select-text ml-1 px-3 py-1 ring-4 landscape:px-4 landscape:py-2 landscape:ml-2 landscape:ring-8"
            >
              {{ getLastClue?.attempts }}
            </span>
          </div>
        </Transition>
        <div v-if="isUserOperativeTurn" class="mt-2 landscape:mt-3">
          <button class="button shadow-button text-base" @click="teamTurn">Fini de deviner</button>
        </div>
      </div>
    </div>
  </template>
</template>

<style scoped>
.bottom {
  z-index: 70;
  position: absolute;
  width: 100%;
  top: 860px;
  transition: top 1s;
}

.clue-number-modal {
  inset: 0px auto auto 0px;
  transform: translateX(-55%) translateY(-4rem) translateZ(0px);
}

.clue-number-option {
  padding: 0.3rem 0.5rem 0.25rem;
  margin: 0.1rem 0.2rem;
  border: 1px solid gray;
  box-shadow: rgba(0, 0, 0, 0.5) 0.1rem 0.1rem 1px 0px;
}

.slide-fade-enter-active {
  animation: appearing-in 1s;
}

.slide-fade-enter-from {
  top: 460px;
}

@keyframes appearing-in {
  0% {
    transform: scale(1.5);
  }
  100% {
    transform: scale(1);
  }
}
</style>
