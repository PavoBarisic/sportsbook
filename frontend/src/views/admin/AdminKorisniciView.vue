<template>
  <div class="page">
    <div class="page-header">
      <h2>Svi korisnici</h2>
      <span class="ukupno">Ukupno: {{ korisnici.length }}</span>
    </div>

    <div v-if="ucitava" class="state-msg">Učitavanje...</div>
    <div v-else-if="korisnici.length === 0" class="state-msg">Nema korisnika.</div>

    <div v-else class="tablica-wrapper">
      <table class="tablica">
        <thead>
          <tr>
            <th>ID</th>
            <th>Ime i prezime</th>
            <th>Email</th>
            <th>Rola</th>
            <th>Stanje računa</th>
            <th>Datum registracije</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="korisnik in korisnici" :key="korisnik.id">
            <td>{{ korisnik.id }}</td>
            <td>{{ korisnik.ime }} {{ korisnik.prezime }}</td>
            <td class="email">{{ korisnik.email }}</td>
            <td>
              <span class="rola-badge" :class="korisnik.rola.toLowerCase()">
                {{ korisnik.rola }}
              </span>
            </td>
            <td :class="korisnik.stanje > 0 ? 'pozitivno' : 'negativno'">
              {{ Number(korisnik.stanje).toFixed(2) }} KM
            </td>
            <td>{{ formatirajDatum(korisnik.datumRegistracije) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'

const korisnici = ref([])
const ucitava = ref(false)

const dohvatiKorisnike = async () => {
  ucitava.value = true
  try {
    const response = await api.get('/api/korisnici')
    korisnici.value = response.data
  } catch (e) {
    console.error(e)
  } finally {
    ucitava.value = false
  }
}

const formatirajDatum = (datum) => {
  return new Date(datum).toLocaleDateString('hr-HR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  })
}

onMounted(dohvatiKorisnike)
</script>

<style scoped>
.page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
}

.ukupno {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.tablica-wrapper {
  overflow-x: auto;
}

.tablica {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.875rem;
}

.tablica th {
  background-color: var(--bg-secondary);
  color: var(--text-secondary);
  font-weight: 600;
  text-align: left;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid var(--border);
  white-space: nowrap;
}

.tablica td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid var(--border);
}

.tablica tr:hover td {
  background-color: var(--bg-secondary);
}

.email {
  color: var(--text-secondary);
}

.rola-badge {
  font-size: 0.7rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  text-transform: uppercase;
}

.rola-badge.admin {
  background: rgba(245,158,11,0.15);
  color: var(--warning);
}

.rola-badge.user {
  background: rgba(59,130,246,0.15);
  color: var(--accent);
}

.pozitivno {
  color: var(--success);
  font-weight: 600;
}

.negativno {
  color: var(--danger);
  font-weight: 600;
}

.state-msg {
  text-align: center;
  color: var(--text-secondary);
  padding: 4rem 0;
}
</style>