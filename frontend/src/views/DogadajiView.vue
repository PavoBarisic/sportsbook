<template>
  <div class="page">
    <div class="page-header">
      <h2>Sportski događaji</h2>

      <div class="filters">
        <select v-model="filterSport" @change="dohvatiDogadaje">
          <option value="">Svi sportovi</option>
          <option v-for="sport in sportovi" :key="sport" :value="sport">{{ sport }}</option>
        </select>

        <select v-model="filterStatus" @change="dohvatiDogadaje">
          <option value="">Svi statusi</option>
          <option value="UPCOMING">Upcoming</option>
          <option value="LIVE">Live</option>
          <option value="FINISHED">Finished</option>
        </select>
      </div>
    </div>

    <div v-if="ucitava" class="state-msg">Učitavanje...</div>
    <div v-else-if="dogadaji.length === 0" class="state-msg">Nema događaja.</div>

    <div v-else class="dogadaji-grid">
      <DogadajCard
        v-for="dogadaj in dogadaji"
        :key="dogadaj.id"
        :dogadaj="dogadaj"
        :odabrana-stavka="odabraneStavke[dogadaj.id]"
        @odaberi="odaberiStavku"
      />
    </div>

    <div v-if="Object.keys(odabraneStavke).length > 0" class="tiket-panel">
      <h3>Tiket ({{ Object.keys(odabraneStavke).length }} stavki)</h3>
      <div class="tiket-stavke">
        <div v-for="(stavka, dogadajId) in odabraneStavke" :key="dogadajId" class="tiket-stavka">
          <span>{{ stavka.naziv }}</span>
          <span class="kvota">{{ stavka.tip }} @ {{ stavka.kvota }}</span>
          <button class="btn-ukloni" @click="ukloniStavku(dogadajId)">✕</button>
        </div>
      </div>
      <div class="tiket-ukupno">
        <span>Ukupna kvota:</span>
        <strong>{{ ukupnaKvota }}</strong>
      </div>
      <button class="btn-primary" @click="idiNaTiket">Postavi tiket →</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'
import DogadajCard from '../components/DogadajCard.vue'
import { useTiketStore } from '../stores/tiket'

const router = useRouter()
const tiketStore = useTiketStore()

const dogadaji = ref([])
const ucitava = ref(false)
const filterSport = ref('')
const filterStatus = ref('')
const odabraneStavke = ref({})

const sportovi = ['FOOTBALL', 'BASKETBALL', 'TENNIS', 'BASEBALL', 'HOCKEY']

const dohvatiDogadaje = async () => {
  ucitava.value = true
  try {
    const params = {}
    if (filterSport.value) params.sport = filterSport.value
    if (filterStatus.value) params.status = filterStatus.value
    const response = await api.get('/api/dogadaji', { params })
    dogadaji.value = response.data
  } catch (e) {
    console.error('Greška pri dohvatu događaja', e)
  } finally {
    ucitava.value = false
  }
}

const odaberiStavku = ({ dogadaj, tip, kvota }) => {
  if (odabraneStavke.value[dogadaj.id]?.tip === tip) {
    ukloniStavku(dogadaj.id)
    return
  }
  odabraneStavke.value[dogadaj.id] = {
    dogadajId: dogadaj.id,
    naziv: dogadaj.naziv,
    tip,
    kvota,
  }
}

const ukloniStavku = (dogadajId) => {
  delete odabraneStavke.value[dogadajId]
}

const ukupnaKvota = computed(() => {
  const stavke = Object.values(odabraneStavke.value)
  if (stavke.length === 0) return '1.00'
  return stavke.reduce((acc, s) => acc * s.kvota, 1).toFixed(2)
})

const idiNaTiket = () => {
  tiketStore.postaviStavke(Object.values(odabraneStavke.value))
  router.push('/tiket')
}

onMounted(dohvatiDogadaje)
</script>

<style scoped>
.page {
  max-width: 1200px;
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
  gap: 1rem;
}

.filters select {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0.5rem 1rem;
  color: var(--text-primary);
  font-size: 0.875rem;
}

.dogadaji-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 1.25rem;
}

.state-msg {
  text-align: center;
  color: var(--text-secondary);
  padding: 4rem 0;
}

.tiket-panel {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: var(--bg-secondary);
  border-top: 1px solid var(--border);
  padding: 1.25rem 2rem;
  display: flex;
  align-items: center;
  gap: 2rem;
  z-index: 99;
}

.tiket-panel h3 {
  font-size: 1rem;
  font-weight: 600;
  white-space: nowrap;
}

.tiket-stavke {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  flex: 1;
}

.tiket-stavka {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0.4rem 0.75rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.8rem;
}

.kvota {
  color: var(--accent);
  font-weight: 600;
}

.btn-ukloni {
  background: none;
  color: var(--text-secondary);
  font-size: 0.75rem;
  padding: 0;
  transition: color 0.2s;
}

.btn-ukloni:hover {
  color: var(--danger);
}

.tiket-ukupno {
  display: flex;
  flex-direction: column;
  align-items: center;
  white-space: nowrap;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.tiket-ukupno strong {
  color: var(--success);
  font-size: 1.1rem;
}

.btn-primary {
  background-color: var(--accent);
  color: white;
  padding: 0.65rem 1.5rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 600;
  white-space: nowrap;
  transition: background-color 0.2s;
}

.btn-primary:hover {
  background-color: var(--accent-hover);
}
</style>