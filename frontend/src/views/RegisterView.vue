<template>
  <div class="auth-wrapper">
    <div class="auth-card">
      <div class="auth-header">
        <h1>⚽ Sportsbook</h1>
        <p>Kreiraj novi račun</p>
      </div>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>Ime</label>
          <input v-model="ime" type="text" placeholder="Ime" required />
        </div>

        <div class="form-group">
          <label>Prezime</label>
          <input v-model="prezime" type="text" placeholder="Prezime" required />
        </div>

        <div class="form-group">
          <label>Email</label>
          <input v-model="email" type="email" placeholder="email@mail.com" required />
        </div>

        <div class="form-group">
          <label>Lozinka</label>
          <input v-model="lozinka" type="password" placeholder="••••••••" required />
        </div>

        <p v-if="greska" class="error">{{ greska }}</p>

        <button type="submit" class="btn-primary" :disabled="ucitava">
          {{ ucitava ? 'Registracija...' : 'Registriraj se' }}
        </button>
      </form>

      <p class="auth-footer">
        Već imaš račun? <RouterLink to="/login">Prijavi se</RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'

const auth = useAuthStore()
const router = useRouter()

const ime = ref('')
const prezime = ref('')
const email = ref('')
const lozinka = ref('')
const greska = ref('')
const ucitava = ref(false)

const handleRegister = async () => {
  greska.value = ''
  ucitava.value = true
  try {
    await api.post('/auth/register', {
      ime: ime.value,
      prezime: prezime.value,
      email: email.value,
      lozinka: lozinka.value,
    })
    await auth.login(email.value, lozinka.value)
    router.push('/dogadaji')
  } catch (e) {
    greska.value = 'Registracija nije uspjela. Provjeri podatke.'
  } finally {
    ucitava.value = false
  }
}
</script>

<style scoped>
.auth-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-primary);
}

.auth-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 2.5rem;
  width: 100%;
  max-width: 420px;
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-header h1 {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--accent);
  margin-bottom: 0.5rem;
}

.auth-header p {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  margin-bottom: 1.25rem;
}

.form-group label {
  font-size: 0.875rem;
  color: var(--text-secondary);
  font-weight: 500;
}

.form-group input {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0.75rem 1rem;
  color: var(--text-primary);
  font-size: 0.95rem;
  transition: border-color 0.2s;
}

.form-group input:focus {
  border-color: var(--accent);
}

.btn-primary {
  width: 100%;
  background-color: var(--accent);
  color: white;
  padding: 0.75rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  transition: background-color 0.2s;
  margin-top: 0.5rem;
}

.btn-primary:hover:not(:disabled) {
  background-color: var(--accent-hover);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  color: var(--danger);
  font-size: 0.875rem;
  margin-bottom: 0.75rem;
}

.auth-footer {
  text-align: center;
  margin-top: 1.5rem;
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.auth-footer a {
  color: var(--accent);
  font-weight: 500;
}
</style>