import type { Room } from '@/types/types'
import { apiFetchData } from './api'
import { useRouter } from 'vue-router'

const router = useRouter()

export const userMiddleware = async (username: string, roomId: string) => {
  const data: Room = await apiFetchData(`room/${roomId}`, 'GET')
  const users = data.players.map((player) => player.name)
  if (!users.includes(username)) {
    router.push({ name: 'home' })
  }
}
