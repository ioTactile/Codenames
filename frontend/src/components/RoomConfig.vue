<script setup lang="ts">
import type { Room } from '@/types/types'
import { apiFetchData } from '@/utils/api'

const props = defineProps<{
  room: Room
}>()

const startRoom = async () => {
  const roomId = props.room.id
  try {
    await apiFetchData(`room/${roomId}`, 'PUT', {
      action: 'start'
    })
  } catch (error) {
    console.error(error)
  }
}
</script>

<template>
  <div class="config-wrapper absolute rounded-xl bg-white shadow-bottom">
    <div class="relative flex flex-1 flex-col justify-between">
      <section class="overflow-y-auto rounded-tl-xl rounded-tr-xl bg-gray-200">
        <div class="relative flex flex-col rounded-xl bg-gray-200">
          <div class="relative z-0 p-2 pr-4">
            <div class="config-bg absolute inset-0 z-[-1] bg-cover"></div>
            <section class="mb-2 flex justify-end landscape:mb-0">
              <button class="group flex items-center justify-center py-1">
                <div
                  class="flex items-center justify-center rounded-lg border-2 border-white bg-white px-2 py-1 hover:border-yellow"
                >
                  <div class="mr-4 rtl:ml-4">
                    <img src="/images/arrow-right.svg" alt="Arrow right icon" class="w-[30px]" />
                  </div>
                  <span class="mr-2">Choisir la langue :</span>
                  <figure class="flag-parent">
                    <div class="flag fr"></div>
                  </figure>
                </div>
              </button>
            </section>
            <section class="flex items-center justify-center">
              <img src="/images/box_cn.png" alt="Box image" class="relative ml-[-30px] h-[220px]" />
              <div class="ml-[-20px] rounded-lg bg-white/50 p-2">
                <div class="config-settings">
                  <p>Codenames est un jeu pour quatre joueurs et plus.</p>
                  <p>
                    Répartissez-vous équitablement en deux équipes – <em class="red">Rouge</em> et
                    <em class="blue">Bleu</em>. Désignez un joueur dans chaque équipe pour devenir
                    l' <strong>espion</strong>. Les autres joueurs sont des <strong>agents</strong>.
                    Les espions donnent des indices composés d'un seul mot à leurs agents, en
                    essayant de relier autant de mots que possible appartenant à son équipe.
                  </p>
                </div>
                <div class="mt-2 font-bold text-[#95004b]">
                  La première équipe à deviné tous ses mots remporte la partie.
                </div>
                <div class="config-settings mt-2">
                  Pour plus d'informations, vérifiez les <button>Règles</button>.
                </div>
              </div>
            </section>
          </div>
        </div>
      </section>
      <section class="mt-auto flex items-center justify-center py-4 portrait:pb-1.5 portrait:pt-1">
        <div class="m-2">
          <button class="button text-base shadow-bottom">Paramètres complets</button>
        </div>
        <div class="m-2">
          <button class="button color-green text-base shadow-bottom" @click="startRoom">
            Jouer avec les paramètres recommandés
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.config-wrapper {
  width: 750px;
  top: 150px;
  left: 50%;
  transform: translateX(-50%);
}

.config-bg {
  background-image: url('/images/bg_colorful.webp');
}

.config-settings {
  pointer-events: initial;
}

.config-settings em.red {
  color: #bb0202;
}

.config-settings em.blue {
  color: #006397;
}

.flag-parent {
  position: relative;
  width: 28px;
  height: 28px;
}

.flag {
  position: absolute;
  left: 50%;
  top: 50%;
  background-image: url('/images/flags.png');
  background-repeat: no-repeat;
  transform: scale(0.233333) translate(-214.286%, -214.286%);
  border-radius: 50%;
  box-shadow: rgba(0, 0, 0, 0.75) 0px 0px 15px 0px inset;
}

.fr {
  width: 120px;
  height: 120px;
  background-position: -534px -402px;
}

.config-settings button {
  color: #076d9d;
}
</style>
