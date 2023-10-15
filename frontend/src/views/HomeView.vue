<script setup lang="ts">
import Newsletter from '@/components/HomeNewsletter.vue'
import Faq from '@/components/HomeFaq.vue'
import Footer from '@/components/HomeFooter.vue'
</script>

<template>
  <div class="relative">
    <main class="relative flex w-full flex-col overflow-x-hidden xl:min-h-700px">
      <div class="absolute -z-10 h-full w-full bg-purple">
        <span class="background-container">
          <img
            src="/images/bg_colorful.webp"
            alt="Background image"
            class="background object-cover"
          />
        </span>
      </div>
      <div class="relative z-40 flex flex-col items-center justify-start">
        <div class="mt-2 w-full max-w-2xl object-contain md:mt-6">
          <img src="/images/logo.webp" alt="Codenames logo" />
        </div>
        <h1 class="text-shadow xl-mt-8 mt-2 text-vw font-black text-white sm:text-3xl">
          Jouez avec vos amis.
        </h1>
        <div class="mb-6 flex flex-col lg:mb-12">
          <RouterLink
            to="/room/create"
            class="create-button-wrapper group mb-2 mt-6 transform transition-transform duration-75 active:scale-90 lg:mt-10"
          >
            <div class="create-button m-1 group-hover:bg-tutorial group-hover:bg-none lg:px-8">
              CRÉER UN SALON
            </div>
          </RouterLink>
        </div>
      </div>
      <div
        class="align-center relative mt-auto grid w-full auto-cols-min grid-cols-2 justify-center md:grid-cols-3"
      >
        <div class="relative md:col-start-1">
          <div
            class="absolute -left-6 bottom-0 flex w-38vw justify-end rtl:left-20 sm:-left-10 md:w-30vw 1201px:-left-50 1201px:w-auto 1460px:left-10"
          >
            <img src="/images/agent_male.png" alt="Male agent" />
          </div>
        </div>
        <div
          class="tutorial relative col-span-2 justify-self-center text-xl leading-7 md:col-span-1 1201px:leading-8"
        >
          <h2 class="mb-2 text-2xl font-bold text-purple">Comment jouer :</h2>
          <b>1.</b>
          Cliquez sur le bouton CRÉER UN SALON.
          <br />
          <b>2.</b>
          Choisissez la configuration de jeu et commencez la partie.
          <br />
          <b>3.</b>
          Connectez-vous avec vos amis en utilisant votre chat audio ou vidéo préféré.
          <br />
          <b>4.</b>
          Partagez l'URL du salon avec vos amis.
          <br />
          <b>5.</b>
          Amusez-vous bien !
          <br />
          <div class="absolute bottom-2.5 right-2.5">
            <a href="https://www.iotactile.com" target="_blank">Réalisé par ioTactile</a>
          </div>
        </div>
        <div class="relative col-start-2 row-start-1 md:col-start-3">
          <div
            class="absolute -right-6 bottom-0 flex w-32vw justify-end rtl:right-20 sm:-right-10 md:w-26vw 1201px:-right-50 1201px:w-auto 1460px:right-10"
          >
            <img src="/images/agent_female.png" alt="Female agent" width="412" height="432" />
          </div>
        </div>
      </div>
    </main>
    <Newsletter location="home" />
    <Faq />
    <Footer />
  </div>
</template>

<style scoped>
.background-container {
  position: absolute;
  inset: 0px;
  box-sizing: border-box;
  display: block;
  overflow: hidden;
  width: initial;
  height: initial;
  background: none;
  opacity: 1;
  border: 0px;
  margin: 0px;
  padding: 0px;
}

.background {
  position: absolute;
  inset: 0px;
  box-sizing: border-box;
  display: block;
  width: 0px;
  height: 0px;
  min-width: 100%;
  max-width: 100%;
  min-height: 100%;
  max-height: 100%;
  border: none;
  margin: auto;
  padding: 0px;
}
</style>
