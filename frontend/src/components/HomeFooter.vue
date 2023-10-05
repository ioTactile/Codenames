<script setup lang="ts">
import SocialMedia from '@/components/SocialMedia.vue'
</script>

<template>
  <footer class="bg-black py-10">
    <div class="flex flex-col mx-auto text-white justify-center xl:flex-row xl:justify-around">
      <a href="" class="flex flex-col xl:flex-row justify-between items-center">
        <div class="xl:mr-24 flex justify-center">
          <img src="/images/footer-logo.webp" alt="Czech Games Edition Logo" />
        </div>
        <p class="mt-6 xl:mt-0">
          Copyright ©
          <!-- -->
          2023
          <!-- -->
          Czech Games Edition
        </p>
      </a>
      <div class="flex justify-center xl:justify-between items-center text-darkGray mt-6 xl:mt-0">
        <a
          href="http://czechgames.com/"
          target="_blank"
          class="transition duration-500 hover:text-white"
        >
          CzechGames.com
        </a>
        <span class="text-2xl font-bold text-gray-800 mx-6">.</span>
        <a
          href="http://codenamesgame.com/"
          target="_blank"
          class="transition duration-500 hover:text-white"
        >
          CodenamesGame.com
        </a>
      </div>
      <div class="flex flex-wrap items-center justify-center mx-6 mt-6 xl:mt-0 gap-6">
        <a
          href="http://www.facebook.com/codenamesgame/"
          target="_blank"
          class="bg-white rounded-full hover:opacity-78 w-10 h-10"
        >
          <div class="w-full h-full flex justify-center items-center">
            <img src="/images/facebook.svg" alt="Facebook icon" class="w-7/12" />
          </div>
        </a>
        <a
          href="http://twitter.com/CodenamesGame"
          target="_blank"
          class="bg-white rounded-full hover:opacity-78 w-10 h-10"
        >
          <div class="w-full h-full flex justify-center items-center">
            <img src="/images/twitter.svg" alt="Twitter icon" class="w-7/12" />
          </div>
        </a>
        <a
          href="http://www.instagram.com/codenamesgame/"
          target="_blank"
          class="bg-white rounded-full hover:opacity-78 w-10 h-10"
        >
          <div class="w-full h-full flex justify-center items-center">
            <img src="/images/instagram.svg" alt="Instagram icon" class="w-7/12" />
          </div>
        </a>
      </div>
    </div>
  </footer>
</template>
