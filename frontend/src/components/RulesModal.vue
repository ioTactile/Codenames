<script setup lang="ts">
import { Dialog, TransitionRoot, TransitionChild } from '@headlessui/vue'
import { ref } from 'vue'

defineProps<{
  isRulesMenuOpen: boolean
}>()

const isCodenames = ref<boolean>(true)
const isCodenamesDuet = ref<boolean>(false)

const switchGame = () => {
  isCodenames.value = !isCodenames.value
  isCodenamesDuet.value = !isCodenamesDuet.value
}

const emit = defineEmits<{
  (e: 'closeRulesMenu', value: boolean): void
}>()

const close = (): void => {
  emit('closeRulesMenu', false)
}
</script>

<template>
  <TransitionRoot as="template" :show="isRulesMenuOpen">
    <Dialog as="div" class="relative z-10" @close="emit('closeRulesMenu', false)">
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
        <div class="flex min-h-full items-start justify-center text-center sm:p-0">
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
              class="modal-wrapper absolute left-1/2 top-4 flex w-11/12 -translate-x-1/2 transform flex-col overflow-hidden rounded-xl bg-white landscape:w-3/4"
            >
              <button
                class="cross-button z-100 absolute right-2 top-2 flex h-6 w-6 items-center justify-center rounded-full text-base shadow-bottom"
                @click="close"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  class="h-4 w-4"
                >
                  <line x1="18" y1="6" x2="6" y2="18" class="jsx-3984956064"></line>
                  <line x1="6" y1="6" x2="18" y2="18" class="jsx-3984956064"></line>
                </svg>
              </button>
              <nav class="flex flex-none justify-center bg-gray-200 p-2 pr-10">
                <button
                  :class="isCodenames ? 'border-gray-500 bg-white' : 'border-gray-300'"
                  class="dark:shadow-inset-dark flex flex-1 items-center justify-center rounded-lg border-2 px-0.5 py-1 shadow-inset"
                  @click="switchGame"
                >
                  <figure class="flex-initial">
                    <img src="/images/box_cn.png" alt="" class="ml-auto max-h-12" />
                  </figure>
                  <div class="mr-2 text-left">
                    <h3
                      class="text-xs font-bold leading-none text-pink-800 dark:text-pink-600 sm:text-base sm:leading-tight"
                    >
                      Codenames
                    </h3>
                    <p class="text-[0.7rem] leading-[0.8rem]">Team VS Team, 4+ players</p>
                  </div>
                </button>
                <div class="w-2"></div>
                <button
                  :class="isCodenamesDuet ? 'border-gray-500 bg-white' : 'border-gray-300'"
                  class="flex flex-1 items-center justify-center rounded-lg border-2 px-0.5 py-1"
                  @click="switchGame"
                >
                  <figure class="flex-initial">
                    <img src="/images/box_cnd.png" class="ml-auto max-h-12" />
                  </figure>
                  <section class="mr-2 text-left">
                    <h3
                      class="text-xs font-bold leading-none text-green-800 dark:text-green-600 sm:text-base sm:leading-tight"
                    >
                      Codenames Duet
                    </h3>
                    <p class="text-[0.7rem] leading-[0.8rem]">Cooperative, 2+ players</p>
                  </section>
                </button>
              </nav>
              <template v-if="isCodenames">
                <div
                  class="user-select-text rules-global pointer-events-auto overflow-y-auto px-4 py-2 text-start"
                >
                  <p>
                    Codenames is a game for two teams. There is a grid of 25 words. Some of them are
                    secretly assigned to the <b class="text-[#a21414]">Red Team</b>, some to the
                    <b class="text-[#144da2]">Blue Team</b>. One player from each team is the
                    <b>Spymaster</b>, and only Spymasters see which words belong to which team.
                    Spymasters take turns giving clues to their teammates (<b>Operatives</b>),
                    trying to lead them to guessing their team's words. The team that guesses all
                    their words first wins the game.
                  </p>
                  <blockquote>
                    <strong>Please note:</strong> Codenames Online is a tool that lets you play the
                    game over the Internet, but it doesn't enforce all the rules. Spymasters in
                    particular need to know the rules so they can follow them.
                  </blockquote>
                  <h2>Dividing into Teams</h2>
                  <p>
                    Divide all players into two teams, <b class="text-[#a21414]">red</b> and
                    <b class="text-[#144da2]">blue</b>.
                  </p>
                  <section class="grid items-end gap-4 sm:grid-cols-2">
                    <figure>
                      <figcaption>
                        One player from each team should click on
                        <strong class="bg-yellow">Join as Spymaster</strong>. He/she will then see
                        the colors of the cards.
                      </figcaption>
                      <div class="relative pt-[56%]">
                        <img
                          src="https://cdn.codenames.game/v20210210/rules/step1_1_spymaster.gif"
                          class="absolute inset-0"
                        />
                      </div>
                    </figure>
                    <figure>
                      <figcaption>
                        Everyone else should click on
                        <strong class="bg-yellow">Join as Operative</strong>. They do not see the
                        colors of the cards.
                      </figcaption>
                      <div class="relative pt-[56%]">
                        <img
                          src="https://cdn.codenames.game/v20210210/rules/step1_2_operative.gif"
                          class="absolute inset-0"
                        />
                      </div>
                    </figure>
                  </section>
                  <h2>Giving Clues</h2>
                  <p>
                    Spymasters give clues. When it's your turn to give a clue, tap some words in
                    your color that you want to give a clue for. Then type in a one word clue that
                    relates to all selected words. Your Operatives will only see the clue and the
                    number of marked cards.
                  </p>
                  <figure>
                    <figcaption class="flex items-start justify-between">
                      <section class="flex-1">When you give a clue ...</section>
                      <section class="flex-1 text-right">
                        ... your Operatives only see the word and the number.
                      </section>
                    </figcaption>
                    <div class="relative pt-[30%]">
                      <img
                        src="https://cdn.codenames.game/v20210210/rules/step2_1_clue.gif"
                        class="absolute inset-0"
                      />
                    </div>
                  </figure>
                  <p>&nbsp;</p>
                  <p>
                    <img
                      width="90"
                      src="https://cdn.codenames.game/v20210210/rules/assassin_card_blank.png"
                      alt="assassin card"
                      class="float-left pr-4 pt-1"
                    />
                    Watch out for the black card – it's an Assassin! Avoid clues that would lead to
                    the assassin or to the other team's words.
                  </p>
                  <p>&nbsp;</p>
                  <blockquote>
                    <strong>Note:</strong> Some clues are not allowed, for example using any form of
                    any word on the board. See <a href="#valid-clues">Valid Clues</a> later.
                  </blockquote>
                  <h2>Guessing</h2>
                  <p>Operatives guess the words based on the Spymaster's clue.</p>
                  <section class="grid items-end gap-4 sm:grid-cols-2">
                    <figure>
                      <figcaption>
                        You can discuss the clue with your teammates. You can also suggest a guess
                        by tapping the card you think matches the clue.
                      </figcaption>
                      <div class="relative pt-[62.50%]">
                        <img
                          src="https://cdn.codenames.game/v20210210/rules/step3_1_guessing_suggest.gif"
                          class="absolute inset-0"
                        />
                      </div>
                    </figure>
                    <figure>
                      <figcaption>
                        To make your guess official, tap the
                        <img
                          src="https://stage.codenames.game/img/icon/icon_tap_card.png"
                          class="inline w-6"
                        />
                        button. The game will then reveal the color of the chosen word.
                      </figcaption>
                      <div class="relative pt-[62.50%]">
                        <img
                          src="https://cdn.codenames.game/v20210210/rules/step3_2_guessing_tap.gif"
                          class="absolute inset-0"
                        />
                      </div>
                    </figure>
                  </section>
                  <p>&nbsp;</p>
                  <p>
                    If you guess a word of your team's color, you may guess again. You'll want to
                    guess as many words as your Spymaster indicated.
                  </p>
                  <blockquote>
                    <strong>Note:</strong> You can also guess unsolved clues from previous turns.
                    See <a href="#number-of-guesses">Number of Guesses</a> later.
                  </blockquote>
                  <h2>End of Turn</h2>
                  <p>Your turn can end in one of three ways:</p>
                  <section class="grid items-end gap-2 sm:grid-cols-3">
                    <figure>
                      <figcaption>
                        Guessing a word of the opponent's color or neutral color.
                      </figcaption>
                      <div class="relative pt-[62.50%]">
                        <img
                          src="https://cdn.codenames.game/v20210210/rules/step4_1_end_neutral.gif"
                          class="absolute inset-0"
                        />
                      </div>
                    </figure>
                    <figure>
                      <figcaption>Ending guessing manually by clicking the button.</figcaption>
                      <div class="relative pt-[62.50%]">
                        <img
                          src="https://cdn.codenames.game/v20210210/rules/step4_2_end_button.gif"
                          class="absolute inset-0"
                        />
                      </div>
                    </figure>
                    <figure>
                      <figcaption>
                        Reaching the maximum number of guesses (clue number + 1).
                      </figcaption>
                      <div class="relative pt-[62.50%]">
                        <img
                          src="https://cdn.codenames.game/v20210210/rules/step4_3_end_max.gif"
                          class="absolute inset-0"
                        />
                      </div>
                    </figure>
                  </section>
                  <h2>Winning and Losing</h2>
                  <p>
                    Teams alternate turns. A team wins once all their words have been guessed. They
                    lose if they guess the Assassin!
                  </p>
                  <h2 id="notes">Notes</h2>
                  <details>
                    <summary id="valid-clues">Valid clues</summary>
                    <section>
                      <ul>
                        <li>
                          The clue is strictly limited to <b>one word and one number</b>. The
                          Spymaster is expected to keep a straight face and he shouldn't comment on
                          players' guesses, to avoid giving up any unwanted information.
                        </li>
                        <li>
                          Your clue is supposed to be <b>a single word</b>, but Spymasters can agree
                          on more flexible rules. For example, you can agree to allow two-word place
                          names, like NEW YORK.
                        </li>
                        <li>
                          Don't use <b>any form of a word in the grid</b> as a clue. Don't type in
                          SUGAR as a clue for SUGAR and CHOCOLATE, and don't try to get around this
                          rule using clues like SUGARY or SUGARCANE.
                        </li>
                        <li>
                          Give clues <b>in the language you have agreed on</b>. Don't use foreign
                          words to give extra information.
                        </li>
                        <li>
                          The clue needs to relate to the <b>meaning of the words</b>. Don't give
                          clues about the letters in the word or its position in the grid.
                        </li>
                      </ul>
                      <p>
                        Keep your clues in the spirit of the game, and if you aren't sure, ask the
                        opposing Spymaster (using some chat channel that the other players can't
                        hear or see).
                      </p>
                    </section>
                  </details>
                  <details>
                    <summary id="number-of-guesses">Number of Guesses</summary>
                    <section>
                      <p class="mb-4">
                        The number in your clue is supposed to tell your team how many words relate
                        to that clue. They can always guess <b>one extra word</b>, usually an
                        unguessed word from previous turns.
                      </p>
                      <section class="flex flex-wrap items-start justify-start sm:flex-nowrap">
                        <ul>
                          <li>
                            If you need your Operatives to guess more extra words, you can manually
                            select <b>Infinity ∞</b>. They can guess any number of words then (but
                            they don't know how many words your current clue relates to).
                          </li>
                          <li>
                            You can also use <b>Zero 0</b> for your clue. It also allows operatives
                            to guess as many words as they want. The difference is that you are
                            telling your teammates that your clue relates to no words of your color.
                            You may use it for example to prevent them from guessing the Assassin or
                            the opponent's word.
                          </li>
                        </ul>
                        <div class="relative ml-1 w-[320px] flex-none">
                          <img src="https://cdn.codenames.game/v20210210/rules/step5_1_zero.gif" />
                        </div>
                      </section>
                      <p>
                        No matter what type of clue you use, your team always needs to try
                        <b>at least 1 guess</b>.
                      </p>
                    </section>
                  </details>
                </div>
              </template>
              <template v-if="isCodenamesDuet">
                <div class="user-select-text rules-global pointer-events-auto px-4 py-2">
                  <p>
                    Codenames Duet is a cooperative version of Codenames. It can be played by two or
                    more players. To learn the game, please check the How to Play video.
                  </p>
                  <blockquote>
                    Please note: The video assumes you know how to play the classic Codenames.
                  </blockquote>
                  <figure class="video-wrapper">
                    <iframe
                      width="560"
                      height="349"
                      src="https://www.youtube.com/embed/mjlkGVDRwHg"
                      frameborder="0"
                      allowfullscreen
                    ></iframe>
                  </figure>
                </div>
              </template>
            </div>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<style scoped>
.modal-wrapper {
  min-height: 408px;
  height: 775.2px;
  bottom: auto;
  max-width: 960px;
}

.rules-global {
  font-size: 16px;
}

.rules-global blockquote {
  margin: 0.5rem 0px;
  background-color: rgb(192, 235, 238);
  padding: 0.5rem 0.75rem;
}

.rules-global h2 {
  font-size: 1.4rem;
  font-weight: bold;
  margin-top: 2.2rem;
  margin-bottom: 0.5rem;
}

.rules-global p {
  margin: 0.5rem 0px 0.9rem;
}

.rules-global figcaption {
  margin: 0.5rem 0px;
  text-align: left;
}

.rules-global a {
  text-decoration: underline;
  color: rgb(0, 153, 255);
}

.rules-global details summary {
  font-weight: bold;
  cursor: pointer;
  color: rgb(0, 163, 224);
  line-height: 1.5rem;
  text-transform: uppercase;
}

.rules-global ul {
  list-style: outside disc;
  margin-left: 1rem;
}

.video-wrapper {
  position: relative;
  padding-bottom: 56.25%;
  height: 0px;
  margin-top: 0.5rem;
  background-color: rgb(204, 204, 204);
}

.video-wrapper iframe {
  position: absolute;
  top: 0px;
  left: 0px;
  width: 100%;
  height: 100%;
}

.cross-button {
  transition: all 200ms ease 0s;
  background-color: rgb(255, 228, 0);
  border-width: 2px 1px;
  border-style: solid;
  border-color: rgb(255, 246, 167) rgb(255, 228, 0) rgb(243, 187, 0);
}

::-webkit-scrollbar {
  width: 12px;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  background: rgb(204, 204, 204);
  border-radius: 10px;
}
</style>
