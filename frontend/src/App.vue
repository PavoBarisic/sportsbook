<template>
  <div id="app">
    <Navbar v-if="auth.isLoggedIn" />
    <RouterView />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from './stores/auth'
import Navbar from './components/Navbar.vue'

const auth = useAuthStore()

onMounted(async () => {
  if (auth.token && !auth.korisnik) {
    try {
      await auth.dohvatiKorisnika()
    } catch (e) {
      auth.logout()
    }
  }
})
</script>