<template>
  <div class="page">
    <div class="page-header">
      <h2>Moji tiketi</h2>

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

    <div v-else class="tiketi-lista">
      <TiketCard v-for="tiket in filtrianiTiketi" :key="tiket.id" :tiket="tiket" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../services/api'
import TiketCard from '../components/TiketCard.vue'

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
    const response = await api.get('/api/tiketi')
    tiketi.value = response.data
  } catch (e) {
    console.error('Greška pri dohvatu tiketa', e)
  } finally {
    ucitava.value = false
  }
}

onMounted(dohvatiTikete)
</script>

<style scoped>
.page {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
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

.tiketi-lista {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.state-msg {
  text-align: center;
  color: var(--text-secondary);
  padding: 4rem 0;
}
</style>