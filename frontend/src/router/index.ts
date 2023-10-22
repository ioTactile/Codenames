import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/room',
      name: 'room',
      component: () => import('@/views/room/RoomView.vue')
    },
    {
      path: '/room/create',
      name: 'create-room',
      component: () => import('@/views/room/CreateRoomView.vue')
    },
    {
      path: '/room/:id',
      name: 'room-details',
      component: () => import('@/views/room/RoomDetailsView.vue'),
      props: (route) => ({ id: Number(route.params.id) })
    }
  ]
})

export default router
