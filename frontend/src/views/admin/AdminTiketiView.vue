<template>
  <div class="page">
    <div class="page-header">
      <h2>Svi tiketi</h2>

      <div class="filters">
        <button
          v-for="status in statusi"
          :key="status.value"
          class="filter-btn"
          :class="{ active: filterStatus === status.value }"
          @click="filterStatus = status.value"
        >
          {{ status.label }}
        </button>
      </div>
    </div>

    <div v-if="ucitava" class="state-msg">Učitavanje...</div>
    <div v-else-if="filtrianiTiketi.length === 0" class="state-msg">Nema tiketa.</div>

    <div v-else class="tablica-wrapper">
      <table class="tablica">
        <thead>
          <tr>
            <th>ID</th>
            <th>Korisnik</th>
            <th>Datum</th>
            <th>Ulog</th>
            <th>Pot. dobit</th>
            <th>Stavke</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tiket in filtrianiTiketi" :key="tiket.id">
            <td>#{{ tiket.id }}</td>
            <td>{{ tiket.korisnik?.ime }} {{ tiket.korisnik?.prezime }}</td>
            <td>{{ formatirajDatum(tiket.datumPostavljanja) }}</td>
            <td>{{ tiket.ukupnaUloga }} KM</td>
            <td class="dobit">{{ tiket.potencijalnaDobitak }} KM</td>
            <td>{{ tiket.stavke?.length ?? 0 }}</td>
            <td>
              <span class="status-badge" :class="tiket.status.toLowerCase()">
                {{ tiket.status }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../services/api'

const tiketi = ref([])
const ucitava = ref(false)
const filterStatus = ref('')

const statusi = [
  { value: '', label: 'Svi' },
  { value: 'AKTIVAN', label: 'Aktivni' },
  { value: 'DOBITAN', label: 'Dobitni' },
  { value: 'GUBITAN', label: 'Gubitni' },
]

const filtrianiTiketi = computed(() => {
  if (!filterStatus.value) return tiketi.value
  return tiketi.value.filter(t => t.status === filterStatus.value)
})

const dohvatiTikete = async () => {
  ucitava.value = true
  try {
    const response = await api.get('/api/tiketi/sve')
    tiketi.value = response.data
  } catch (e) {
    console.error(e)
  } finally {
    ucitava.value = false
  }
}

const formatirajDatum = (datum) => {
  return new Date(datum).toLocaleDateString('hr-HR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

onMounted(dohvatiTikete)
</script>

<style scoped>
.page {
  max-width: 1100px;
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

.filters {
  display: flex;
  gap: 0.5rem;
}

.filter-btn {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  color: var(--text-secondary);
  padding: 0.4rem 1rem;
  border-radius: 999px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.filter-btn:hover {
  border-color: var(--accent);
  color: var(--text-primary);
}

.filter-btn.active {
  background-color: var(--accent);
  border-color: var(--accent);
  color: white;
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

.dobit {
  color: var(--success);
  font-weight: 600;
}

.status-badge {
  font-size: 0.7rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  text-transform: uppercase;
}

.status-badge.aktivan { background: rgba(59,130,246,0.15); color: var(--accent); }
.status-badge.dobitan { background: rgba(34,197,94,0.15); color: var(--success); }
.status-badge.gubitan { background: rgba(239,68,68,0.15); color: var(--danger); }

.state-msg {
  text-align: center;
  color: var(--text-secondary);
  padding: 4rem 0;
}
</style>