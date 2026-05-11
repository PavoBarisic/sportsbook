<template>
  <nav class="navbar">
    <div class="navbar-brand">⚽ SportsBook</div>

    <div class="navbar-links">
      <template v-if="!auth.isAdmin">
        <RouterLink to="/dogadaji">Događaji</RouterLink>
        <RouterLink to="/moji-tiketi">Moji tiketi</RouterLink>
        <RouterLink to="/profil">Profil</RouterLink>
      </template>
      <template v-else>
        <RouterLink to="/admin/dogadaji">Događaji</RouterLink>
        <RouterLink to="/admin/tiketi">Tiketi</RouterLink>
        <RouterLink to="/admin/korisnici">Korisnici</RouterLink>
      </template>
    </div>

    <div class="navbar-right">
      <span v-if="auth.korisnik" class="balance">
        💰 {{ auth.korisnik.stanje.toFixed(2) }} KM
      </span>
      <button class="btn-logout" @click="handleLogout">Odjava</button>
    </div>
  </nav>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

const handleLogout = () => {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 2rem;
  height: 64px;
  background-color: var(--bg-secondary);
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-brand {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--accent);
  letter-spacing: 0.5px;
}

.navbar-links {
  display: flex;
  gap: 2rem;
}

.navbar-links a {
  color: var(--text-secondary);
  font-weight: 500;
  transition: color 0.2s;
}

.navbar-links a:hover,
.navbar-links a.router-link-active {
  color: var(--text-primary);
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.balance {
  color: var(--success);
  font-weight: 600;
  font-size: 0.95rem;
}

.btn-logout {
  background-color: transparent;
  border: 1px solid var(--border);
  color: var(--text-secondary);
  padding: 0.4rem 1rem;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-logout:hover {
  border-color: var(--danger);
  color: var(--danger);
}
</style>