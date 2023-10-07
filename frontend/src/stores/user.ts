import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '@/types/types'

export const useUserStore = defineStore(
  'user',
  () => {
    const user = ref<User[]>([])

    const setUser = (roomId: number, username: string) => {
      const userIndex = user.value.findIndex((u) => u.roomId === roomId)
      console.log(userIndex)
      if (userIndex !== -1) {
        user.value[userIndex].username = username
      } else {
        user.value.push({ roomId, username })
      }
    }

    const removeUser = (roomId: number, username: string) => {
      user.value = user.value.filter((u) => u.roomId !== roomId && u.username !== username)
    }

    const getUser = (roomId: number) => {
      return user.value.find((u) => u.roomId === roomId)
    }

    return { user, setUser, removeUser, getUser }
  },
  { persist: true }
)
