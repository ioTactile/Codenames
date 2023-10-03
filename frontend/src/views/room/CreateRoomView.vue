<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import type { Room } from '@/types/types'
import { apiFetchData } from '@/utils/api'
import { useUsernameStore } from '@/stores/username'
import Newsletter from '@/components/HomeNewsletter.vue'

const username = ref<string>('')
const router = useRouter()

const usernameStore = useUsernameStore()

const createRoom = async () => {
  if (!username.value) {
    alert('Veuillez choisir un pseudo')
    return
  }

  try {
    const data: Room = await apiFetchData('room/create', 'POST', { username: username.value })
    usernameStore.setUsername(username.value)
    router.push({ name: 'room-details', params: { id: data.id } })
  } catch (error) {
    console.error('Error:', error)
  }
}
</script>

<template>
  <main class="flex justify-center items-center h-screen px-2">
    <div class="first-layer absolute inset-0 transition-all"></div>
    <div class="second-layer bg-cover absolute inset-0"></div>
    <div
      class="bg-white rounded-xl pt-4 shadow-bottom border-ui flex flex-col will-change-transform w-96 max-w-9/10 overflow-hidden"
    >
      <section class="px-2 pb-4 text-center">
        <h1 class="text-xl font-bold mb-4">Bienvenue dans Codenames</h1>
        <div class="mb-1 flex flex-col px-2">
          <label for="username-input"
            >Avant d'entrer dans ce salon, veuillez choisir votre pseudo</label
          >
          <input
            placeholder="Choisissez votre pseudo"
            id="username-input"
            type="text"
            v-model="username"
            class="text-center text-base rounded-xl shadow-inset mb-1 border border-slate-300 w-[60%] mx-auto py-2"
          />
        </div>
        <button @click="createRoom" class="button shadow-xl rounded-2xl border-2 text-base">
          Créer un salon
        </button>
      </section>
      <Newsletter emplacement="create" />
    </div>
  </main>
</template>

<style scoped>
.first-layer {
  z-index: -1;
  background: radial-gradient(circle at 50% 50%, rgb(231, 102, 60) 0%, rgb(72, 12, 2) 100%);
}

.second-layer {
  z-index: -1;
  background-image: url('https://cdn.codenames.game/v20210210/img/bg-raster.svg');
  mix-blend-mode: overlay;
}

.button {
  padding: 0.4rem 1rem 0.3rem;
  background-color: rgb(255, 228, 0);
  border-color: rgb(255, 246, 167) rgb(255, 228, 0) rgb(243, 187, 0);
}
</style>
