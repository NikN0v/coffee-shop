<script setup lang="ts">
import { Client } from "@stomp/stompjs";

let accessToken

const client = new Client({
  brokerURL: 'ws://localhost:8080/ws',
  connectHeaders: {
    Authorization: `Bearer ${accessToken}`
  },
  onConnect: () => {
    client.subscribe('/user/queue/orders', (message) => {
      const notification = JSON.parse(message.body)
      console.log('Уведомление: ', notification)
    })
  },
  onStompError: (frame) => {
    console.error('STOMP error: ', frame.headers['message'])
  }
})

client.activate()
</script>

<template>
  <h1>You did it!</h1>
  <p>
    Visit <a href="https://vuejs.org/" target="_blank" rel="noopener">vuejs.org</a> to read the
    documentation
  </p>
</template>

<style scoped></style>
