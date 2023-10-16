import { defineStore } from 'pinia'
import { CompatClient, Stomp } from '@stomp/stompjs'
import SockJS from 'sockjs-client/dist/sockjs.min.js'
import { ref } from 'vue'
import type { Room } from '@/types/types'

export const useWebsocketStore = defineStore('websocket', () => {
  const room = ref<Room | null>(null)
  const stompClient = ref<CompatClient | null>(null)
  const isDisconnected = ref<boolean>(false)
  const afkTimer = ref<ReturnType<typeof setTimeout> | undefined>(undefined)

  const socketUrl = import.meta.env.DEV
    ? import.meta.env.WEBSOCKET_URL_DEV
    : import.meta.env.WEBSOCKET_URL_PROD

  const connect = (roomId: number) => {
    const socket = new SockJS(socketUrl)
    stompClient.value = Stomp.over(socket)

    stompClient.value.connect({}, () => {
      if (!stompClient.value) return
      stompClient.value.subscribe(`/topic/room/${roomId}`, (response: any) => {
        const data = JSON.parse(response.body)
        room.value = data
      })
    })

    handleUserActivity()
  }

  const disconnect = () => {
    if (stompClient.value) {
      stompClient.value.disconnect(() => {
        clearTimeout(afkTimer.value)
      })
    }
  }

  const reconnect = (roomId: number) => {
    const socket = new SockJS(socketUrl)
    stompClient.value = Stomp.over(socket)

    stompClient.value.connect({}, () => {
      if (!stompClient.value) return
      stompClient.value.subscribe(`/topic/room/${roomId}`, (response: any) => {
        const data = JSON.parse(response.body)
        room.value = data
      })
    })

    isDisconnected.value = false
    handleUserActivity()
  }

  const disconnnectOnAfk = () => {
    if (stompClient.value) {
      stompClient.value.disconnect(() => {
        clearTimeout(afkTimer.value)
        isDisconnected.value = true
      })
    }
  }

  const handleUserActivity = () => {
    if (afkTimer.value) clearTimeout(afkTimer.value)
    afkTimer.value = setTimeout(disconnnectOnAfk, 600000)
  }

  return {
    room,
    stompClient,
    isDisconnected,
    connect,
    disconnect,
    reconnect,
    disconnnectOnAfk,
    handleUserActivity
  }
})
