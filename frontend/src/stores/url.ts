import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUrlStore = defineStore(
  'url',
  () => {
    const urlId = ref<string>('')
    const isUrlHidden = ref<boolean>(false)

    const setUrl = (url: string) => {
      urlId.value = url
    }

    const removeUrl = () => {
      urlId.value = ''
      isUrlHidden.value = false
    }

    const getUrl = () => {
      if (isUrlHidden.value) {
        return 'hidden-1'
      }
      return urlId.value
    }

    return { urlId, setUrl, removeUrl, getUrl, isUrlHidden }
  },
  { persist: true }
)
