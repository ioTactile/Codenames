import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RoomUser } from '@/types/types'

export const useRoomUserStore = defineStore(
  'roomUser',
  () => {
    const roomUser = ref<RoomUser[]>([])

    const setRoomUser = (roomId: number, username: string) => {
      roomUser.value.push({ roomId, username })
    }

    const removeRoomUser = (roomId: number, username: string) => {
      roomUser.value = roomUser.value.filter(
        (roomUser) => roomUser.roomId !== roomId && roomUser.username !== username
      )
    }

    const getRoomUser = (roomId: number) => {
      return roomUser.value.find((roomUser) => roomUser.roomId === roomId)
    }

    return { roomUser, setRoomUser, removeRoomUser, getRoomUser }
  },
  { persist: true }
)
