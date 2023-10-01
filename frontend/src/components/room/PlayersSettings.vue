<script setup lang="ts">
const props = defineProps<{
  id: string
  players: string[]
}>()

const copyLink = () => {
  const link = document.getElementById('link') as HTMLInputElement
  link.select()
  document.execCommand('copy')
}

const shufflePlayers = async () => {
  try {
    const response = await fetch(`http://localhost:8080/room/${props.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ action: 'shuffle-players' })
    })

    if (!response.ok) {
      console.error('Failed to shuffle players')
    }
  } catch (error) {
    console.error('Error:', error)
  }
}
</script>

<template>
  <div>
    <div>
      <div>Invitez des joueurs en leur envoyant ce lien :</div>
      <input id="link" type="text" :value="`http://localhost:8080/room/${props.id}`" />
      <button @click="copyLink">Copier le lien dans le presse-papier.</button>
    </div>
    <div>
      <div>Joueurs dans ce salon</div>
      <div v-for="player in props.players" :key="player">{{ player }}</div>
      <button @click="shufflePlayers">Répartir les équipes au hasard</button>
    </div>
  </div>
</template>
