<script setup lang="ts">
import Newsletter from '@/components/HomeNewsletter.vue'
import Faq from '@/components/HomeFaq.vue'
import Footer from '@/components/HomeFooter.vue'
</script>

<template>
  <main class="flex flex-col w-full xl:min-h-700px overflow-x-hidden relative">
    <div class="absolute w-full h-full -z-10 bg-purple">
      <span class="background-container">
        <img
          src="/images/bg_colorful.webp"
          alt="Background image"
          class="background object-cover"
        />
      </span>
    </div>
    <div class="relative flex flex-col justify-start items-center z-40">
      <div class="object-contain w-full mt-2 md:mt-6 max-w-2xl">
        <img src="/images/logo.webp" alt="Codenames logo" />
      </div>
      <h1 class="text-vw sm:text-3xl font-black text-white text-shadow mt-2 xl-mt-8">
        Jouez avec vos amis.
      </h1>
      <div class="flex flex-col mb-6 lg:mb-12">
        <RouterLink
          to="/room/create"
          class="group transition-transform duration-75 transform active:scale-90 mt-6 mb-2 lg:mt-10 create-button-wrapper"
        >
          <div class="create-button group-hover:bg-tutorial group-hover:bg-none m-1 lg:px-8">
            CRÉER UN SALON
          </div>
        </RouterLink>
      </div>
    </div>
    <div
      class="grid relative auto-cols-min grid-cols-2 md:grid-cols-3 justify-center align-center w-full mt-auto"
    >
      <div class="relative md:col-start-1">
        <div
          class="absolute flex justify-end bottom-0 w-38vw md:w-30vw 1201px:w-auto -left-6 sm:-left-10 1201px:-left-50 1460px:left-10 rtl:left-20"
        >
          <img src="/images/agent_male.png" alt="Male agent" />
        </div>
      </div>
      <div
        class="tutorial relative text-xl leading-7 1201px:leading-8 justify-self-center col-span-2 md:col-span-1"
      >
        <h2 class="font-bold text-2xl text-purple mb-2">Comment jouer :</h2>
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
        <div class="absolute right-2.5 bottom-2.5">
          <a href="https://www.iotactile.com" target="_blank">Réalisé par ioTactile</a>
        </div>
      </div>
      <div class="relative col-start-2 row-start-1 md:col-start-3">
        <div
          class="absolute flex justify-end bottom-0 w-32vw md:w-26vw 1201px:w-auto -right-6 sm:-right-10 1201px:-right-50 1460px:right-10 rtl:right-20"
        >
          <img src="/images/agent_female.png" alt="Female agent" width="412" height="432" />
        </div>
      </div>
    </div>
  </main>
  <Newsletter emplacement="home" />
  <Faq />
  <Footer />
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
