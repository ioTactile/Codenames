import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import roomView from '@/views/room/RoomView.vue'
import CreateRoomView from '@/views/room/CreateRoomView.vue'
import RoomDetailsView from '@/views/room/RoomDetailsView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/room',
      name: 'room',
      component: roomView
    },
    {
      path: '/room/create',
      name: 'create-room',
      component: CreateRoomView
    },
    {
      path: '/room/:id',
      name: 'room-details',
      component: RoomDetailsView,
      props: (route) => ({ id: Number(route.params.id) })
    }
  ]
})

export default router
