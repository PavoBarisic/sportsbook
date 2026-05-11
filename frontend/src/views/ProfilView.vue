<template>
  <div class="page">
    <h2>Moj profil</h2>

    <div v-if="auth.korisnik" class="profil-card">
      <div class="profil-header">
        <div class="avatar">{{ inicijali }}</div>
        <div>
          <h3>{{ auth.korisnik.ime }} {{ auth.korisnik.prezime }}</h3>
          <p class="email">{{ auth.korisnik.email }}</p>
          <span class="rola-badge" :class="auth.korisnik.rola.toLowerCase()">
            {{ auth.korisnik.rola }}
          </span>
        </div>
      </div>

      <div class="profil-stats">
        <div class="stat-card">
          <span class="stat-label">Stanje računa</span>
          <span class="stat-value balance">{{ auth.korisnik.stanje.toFixed(2) }} KM</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">Datum registracije</span>
          <span class="stat-value">{{ formatirajDatum(auth.korisnik.datumRegistracije) }}</span>
        </div>
      </div>
    </div>

    <div v-else class="state-msg">Učitavanje...</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()

const inicijali = computed(() => {
  if (!auth.korisnik) return '?'
  return `${auth.korisnik.ime[0]}${auth.korisnik.prezime[0]}`.toUpperCase()
})

const formatirajDatum = (datum) => {
  return new Date(datum).toLocaleDateString('hr-HR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  })
}
</script>

<style scoped>
.page {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.page h2 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 2rem;
}

.profil-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.profil-header {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background-color: var(--accent);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.4rem;
  font-weight: 700;
  flex-shrink: 0;
}

.profil-header h3 {
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
}

.email {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.rola-badge {
  font-size: 0.7rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.rola-badge.admin {
  background-color: rgba(245, 158, 11, 0.15);
  color: var(--warning);
}

.rola-badge.user {
  background-color: rgba(59, 130, 246, 0.15);
  color: var(--accent);
}

.profil-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.stat-card {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.stat-label {
  font-size: 0.8rem;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 1.1rem;
  font-weight: 700;
}

.balance {
  color: var(--success);
  font-size: 1.4rem;
}

.state-msg {
  text-align: center;
  color: var(--text-secondary);
  padding: 4rem 0;
}
</style>