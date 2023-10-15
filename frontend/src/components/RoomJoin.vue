<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { Room } from '@/types/types'
import { apiFetchData } from '@/utils/api'
import { useUserStore } from '@/stores/user'
import Newsletter from '@/components/HomeNewsletter.vue'

const props = defineProps<{
  location: string
}>()

const username = ref<string>('')
const router = useRouter()
const route = useRoute()

const userStore = useUserStore()

const joinRoom = async (): Promise<void> => {
  if (!username.value) {
    alert('Veuillez choisir un pseudo')
    return
  }

  try {
    if (props.location === 'create') {
      const data: Room = await apiFetchData('room/create', 'POST', { username: username.value })
      userStore.setUser(data.id, username.value)
      router.push({ name: 'room-details', params: { id: data.id } })
    } else if (props.location === 'join') {
      const roomId = Number(route.params.id)
      await apiFetchData(`room/${roomId}`, 'PUT', {
        action: 'join',
        username: username.value
      })
      userStore.setUser(roomId, username.value)
    }
  } catch (error) {
    console.error(error)
  } finally {
    username.value = ''
  }
}
</script>

<template>
  <div
    class="border-ui max-w-9/10 flex w-96 flex-col overflow-hidden rounded-xl bg-white pt-4 text-black shadow-bottom will-change-transform"
  >
    <section class="px-2 pb-4 text-center">
      <h1 class="mb-4 text-xl font-bold">Bienvenue dans Codenames</h1>
      <div class="mb-1 px-2">
        <label for="username-input" class="block">
          Avant d'entrer dans ce salon, veuillez choisir votre pseudo
        </label>
        <input
          placeholder="Choisissez votre pseudo"
          id="username-input"
          type="text"
          v-model="username"
          class="mx-auto mb-1 rounded-xl border p-2 text-center text-base text-black shadow-inset"
        />
      </div>
      <button @click="joinRoom" class="button rounded-2xl border-2 text-base shadow-xl">
        {{ location === 'create' ? 'Créer un salon' : 'Rejoindre le salon' }}
      </button>
    </section>
    <Newsletter location="create" />
  </div>
</template>

<style scoped>
.button {
  padding: 0.4rem 1rem 0.3rem;
  background-color: rgb(255, 228, 0);
  border-color: rgb(255, 246, 167) rgb(255, 228, 0) rgb(243, 187, 0);
}
</style>
