<template>
  <div class="page">
    <div class="page-header">
      <button class="btn-back" @click="router.back()">← Natrag</button>
      <h2>Postavi tiket</h2>
    </div>

    <div v-if="stavke.length === 0" class="state-msg">
      Nema odabranih stavki. <RouterLink to="/dogadaji">Odaberi događaje.</RouterLink>
    </div>

    <div v-else class="tiket-wrapper">
      <div class="tiket-card">
        <h3>Odabrane stavke</h3>

        <div class="stavke-lista">
          <div v-for="stavka in stavke" :key="stavka.dogadajId" class="stavka-row">
            <div class="stavka-info">
              <span class="stavka-naziv">{{ stavka.naziv }}</span>
              <span class="stavka-tip">{{ tipLabel(stavka.tip) }}</span>
            </div>
            <span class="stavka-kvota">{{ stavka.kvota }}</span>
          </div>
        </div>

        <div class="tiket-summary">
          <div class="summary-row">
            <span>Ukupna kvota</span>
            <strong>{{ ukupnaKvota }}</strong>
          </div>

          <div class="form-group">
            <label>Iznos uloga (KM)</label>
            <input
              v-model.number="ulog"
              type="number"
              min="1"
              step="0.50"
              placeholder="0.00"
            />
          </div>

          <div class="summary-row">
            <span>Potencijalna dobit</span>
            <strong class="dobit">{{ potencijalnaDobit }} KM</strong>
          </div>
        </div>

        <p v-if="greska" class="error">{{ greska }}</p>
        <p v-if="uspjeh" class="uspjeh">Tiket uspješno postavljen!</p>

        <button
          class="btn-primary"
          @click="postaviTiket"
          :disabled="ucitava || ulog <= 0"
        >
          {{ ucitava ? 'Slanje...' : 'Postavi tiket' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'
import { useAuthStore } from '../stores/auth'
import { useTiketStore } from '../stores/tiket'

const router = useRouter()
const auth = useAuthStore()
const tiketStore = useTiketStore()

const stavke = ref(tiketStore.stavke)
const ulog = ref(10)
const greska = ref('')
const uspjeh = ref(false)
const ucitava = ref(false)

const ukupnaKvota = computed(() => {
  if (stavke.value.length === 0) return '1.00'
  return stavke.value.reduce((acc, s) => acc * s.kvota, 1).toFixed(2)
})

const potencijalnaDobit = computed(() => {
  return (ulog.value * parseFloat(ukupnaKvota.value)).toFixed(2)
})

const tipLabel = (tip) => {
  const mapa = { DOMACIN: '1 (Domaćin)', NERIJESENO: 'X (Neriješeno)', GOST: '2 (Gost)' }
  return mapa[tip] ?? tip
}

const postaviTiket = async () => {
  greska.value = ''
  uspjeh.value = false
  ucitava.value = true
  try {
    await api.post('/api/tiketi', {
      ulog: ulog.value,
      stavke: stavke.value.map(s => ({
        dogadajId: s.dogadajId,
        tip: s.tip,
      })),
    })
    uspjeh.value = true
    tiketStore.ocistiStavke()
    await auth.dohvatiKorisnika()
    setTimeout(() => router.push('/moji-tiketi'), 1500)
  } catch (e) {
    greska.value = e.response?.data?.message ?? 'Greška pri postavljanju tiketa.'
  } finally {
    ucitava.value = false
  }
}
</script>

<style scoped>
.page {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.page-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
}

.btn-back {
  background: none;
  color: var(--text-secondary);
  font-size: 0.9rem;
  padding: 0;
  transition: color 0.2s;
}

.btn-back:hover {
  color: var(--text-primary);
}

.state-msg {
  text-align: center;
  color: var(--text-secondary);
  padding: 4rem 0;
}

.state-msg a {
  color: var(--accent);
}

.tiket-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1.75rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.tiket-card h3 {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stavke-lista {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.stavka-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0.75rem 1rem;
}

.stavka-info {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.stavka-naziv {
  font-size: 0.9rem;
  font-weight: 600;
}

.stavka-tip {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.stavka-kvota {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--accent);
}

.tiket-summary {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  border-top: 1px solid var(--border);
  padding-top: 1.25rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.summary-row strong {
  color: var(--text-primary);
  font-size: 1rem;
}

.dobit {
  color: var(--success) !important;
  font-size: 1.25rem !important;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.form-group label {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.form-group input {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0.75rem 1rem;
  color: var(--text-primary);
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus {
  border-color: var(--accent);
}

.btn-primary {
  width: 100%;
  background-color: var(--accent);
  color: white;
  padding: 0.85rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  transition: background-color 0.2s;
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
}

.uspjeh {
  color: var(--success);
  font-size: 0.875rem;
  text-align: center;
}
</style>