<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import type { Room } from '@/types/types'
import { apiFetchData } from '@/utils/api'

const username = ref<string>('')
const router = useRouter()

const createRoom = async () => {
  if (!username.value) {
    alert('Veuillez choisir un pseudo')
    return
  }

  try {
    const data: Room = await apiFetchData('room/create', 'POST', { pseudo: username.value })
    console.log('data', data)
    router.push({ name: 'room-details', params: { id: data.id } })
  } catch (error) {
    console.error('Error:', error)
  }
}
</script>

<template>
  <div>
    <div>Bienvenue dans Codenames</div>
    <div>Avant d'entrer dans ce salon, veuillez choisir votre pseudo</div>
    <input type="text" v-model="username" />
    <button @click="createRoom">Créer un salon</button>
  </div>
</template>
