import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUsernameStore = defineStore(
  'username',
  () => {
    const username = ref<string>('')

    const setUsername = (newUsername: string) => {
      username.value = newUsername
    }

    return { username, setUsername }
  },
  { persist: true }
)
