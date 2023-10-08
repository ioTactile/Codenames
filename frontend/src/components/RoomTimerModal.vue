<script setup lang="ts">
import { Dialog, TransitionRoot, TransitionChild } from '@headlessui/vue'
import { ref } from 'vue'

defineProps<{
  isTimerMenuOpen: boolean
}>()

const emit = defineEmits<{
  (e: 'closeTimerMenu', value: boolean): void
}>()

const timerType = ref<'off' | 'manual' | 'side' | 'role'>('off')

const defineTimer = () => {
  alert('Timer defined')
  emit('closeTimerMenu', false)
}
</script>

<template>
  <TransitionRoot as="template" :show="isTimerMenuOpen">
    <Dialog as="div" class="relative z-10" @close="emit('closeTimerMenu', false)">
      <TransitionChild
        as="template"
        enter="ease-out duration-300"
        enter-from="opacity-0"
        enter-to="opacity-100"
        leave="ease-in duration-200"
        leave-from="opacity-100"
        leave-to="opacity-0"
      >
        <div class="fixed inset-0 bg-gray-500 bg-opacity-50 transition-opacity" />
      </TransitionChild>

      <div class="fixed inset-0 z-10 w-screen overflow-y-auto">
        <div
          class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0"
        >
          <TransitionChild
            as="template"
            enter="ease-out duration-300"
            enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            enter-to="opacity-100 translate-y-0 sm:scale-100"
            leave="ease-in duration-200"
            leave-from="opacity-100 translate-y-0 sm:scale-100"
            leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
          >
            <div
              class="border-ui rounded-xl bg-white py-2 pb-4 shadow-bottom portrait:w-11/12 landscape:w-1/3"
            >
              <h1 class="mb-2 text-center text-xl font-bold">Sablier</h1>
              <p class="mb-4 px-4 text-center text-base">
                Choisissez la variante sablier et sa durée. Lorsque le sablier est écoulé, rien ne
                se passe : c'est à vous de décider ce qu'il faut faire.
              </p>
              <div class="flex flex-col items-center justify-center px-4">
                <div class="mb-4 block text-center text-base font-bold">
                  Choisissez la variante sablier :
                </div>
                <div class="mb-2 flex flex-col items-stretch justify-center text-base">
                  <label class="mb-2 cursor-pointer">
                    <input
                      v-model="timerType"
                      type="radio"
                      name="timerTypeGroup"
                      value="off"
                      class="rounded-xl border border-gray-600"
                    />
                    <span class="ml-2">Pas de sablier</span>
                  </label>
                  <label class="mb-2 cursor-pointer">
                    <input
                      v-model="timerType"
                      type="radio"
                      name="timerTypeGroup"
                      value="manual"
                      class="rounded-xl border border-gray-600"
                    />
                    <span class="ml-2">Manuel</span>
                  </label>
                  <label class="mb-2 cursor-pointer">
                    <input
                      v-model="timerType"
                      type="radio"
                      name="timerTypeGroup"
                      value="side"
                      class="rounded-xl border border-gray-600"
                    />
                    <span class="ml-2">Automatique</span>
                  </label>
                  <label class="mb-2 cursor-pointer">
                    <input
                      v-model="timerType"
                      type="radio"
                      name="timerTypeGroup"
                      value="role"
                      class="rounded-xl border border-gray-600"
                    />
                    <span class="ml-2">Automatique pour le rôle</span>
                  </label>
                </div>
              </div>
              <div class="w-full text-center">
                <div class="mr-2 inline-block">
                  <button class="button" @click="defineTimer">Définir le sablier</button>
                </div>
                <button class="button" @click="emit('closeTimerMenu', false)">Annuler</button>
              </div>
            </div>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>
