<script setup lang="ts">
import MainLayout from "@/layouts/MainLayout.vue";
import AuthLayout from "@/layouts/AuthLayout.vue";

// const token = sessionStorage.getItem('accessToken')
//
// const client = new Client({
//   brokerURL: 'ws://localhost:8080/ws',
//   connectHeaders: {
//     Authorization: `Bearer ${token}`
//   },
//   onConnect: () => {
//     client.subscribe('/user/queue/orders', (message) => {
//       const notification = JSON.parse(message.body)
//       console.log('Уведомление: ', notification)
//     })
//   },
//   onStompError: (frame) => {
//     console.error('STOMP error: ', frame.headers['message'])
//   }
// })
//
// client.activate()
</script>

<template>
  <RouterView v-slot="{ Component, route }">
    <component :is="route.meta.layout === 'auth' ? AuthLayout : MainLayout">
      <component :is="Component"/>
    </component>
  </RouterView>
</template>