<template>
  <div class="page">
    <div class="page-header">
      <h2>Upravljanje događajima</h2>
      <button class="btn-primary" @click="otvoriDodajModal">+ Novi događaj</button>
    </div>

    <div v-if="ucitava" class="state-msg">Učitavanje...</div>

    <div v-else class="tablica-wrapper">
      <table class="tablica">
        <thead>
          <tr>
            <th>ID</th>
            <th>Naziv</th>
            <th>Sport</th>
            <th>Datum</th>
            <th>Status</th>
            <th>1</th>
            <th>X</th>
            <th>2</th>
            <th>Akcije</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="dogadaj in dogadaji" :key="dogadaj.id">
            <td>{{ dogadaj.id }}</td>
            <td>{{ dogadaj.naziv }}</td>
            <td>{{ dogadaj.sport }}</td>
            <td>{{ formatirajDatum(dogadaj.datum) }}</td>
            <td>
              <span class="status-badge" :class="dogadaj.status.toLowerCase()">
                {{ dogadaj.status }}
              </span>
            </td>
            <td>{{ dogadaj.kvotaDomacin }}</td>
            <td>{{ dogadaj.kvotaNerijeseno }}</td>
            <td>{{ dogadaj.kvotaGost }}</td>
            <td class="akcije">
              <button class="btn-edit" @click="otvoriUrediModal(dogadaj)">Uredi</button>
              <button
                class="btn-rezultat"
                @click="otvoriRezultatModal(dogadaj)"
                :disabled="dogadaj.status === 'FINISHED'"
              >
                Rezultat
              </button>
              <button class="btn-delete" @click="obrisiDogadaj(dogadaj.id)">Obriši</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal: Dodaj / Uredi -->
    <div v-if="prikaziFormModal" class="modal-overlay" @click.self="zatvoriModal">
      <div class="modal">
        <h3>{{ urediDogadaj ? 'Uredi događaj' : 'Novi događaj' }}</h3>

        <div class="form-group">
          <label>Naziv</label>
          <input v-model="forma.naziv" type="text" placeholder="npr. Barcelona vs Real Madrid" />
        </div>

        <div class="form-group">
          <label>Sport</label>
          <select v-model="forma.sport">
            <option v-for="sport in sportovi" :key="sport" :value="sport">{{ sport }}</option>
          </select>
        </div>

        <div class="form-group">
          <label>Datum i vrijeme</label>
          <input v-model="forma.datum" type="datetime-local" />
        </div>

        <div class="kvote-forma">
          <div class="form-group">
            <label>Kvota 1 (Domaćin)</label>
            <input v-model.number="forma.kvotaDomacin" type="number" step="0.01" min="1" />
          </div>
          <div class="form-group">
            <label>Kvota X (Neriješeno)</label>
            <input v-model.number="forma.kvotaNerijeseno" type="number" step="0.01" min="1" />
          </div>
          <div class="form-group">
            <label>Kvota 2 (Gost)</label>
            <input v-model.number="forma.kvotaGost" type="number" step="0.01" min="1" />
          </div>
        </div>

        <p v-if="modalGreska" class="error">{{ modalGreska }}</p>

        <div class="modal-akcije">
          <button class="btn-secondary" @click="zatvoriModal">Odustani</button>
          <button class="btn-primary" @click="spremiDogadaj" :disabled="modalUcitava">
            {{ modalUcitava ? 'Spremanje...' : 'Spremi' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal: Postavi rezultat -->
    <div v-if="prikaziRezultatModal" class="modal-overlay" @click.self="zatvoriModal">
      <div class="modal">
        <h3>Postavi rezultat</h3>
        <p class="modal-subtitle">{{ aktivniDogadaj?.naziv }}</p>

        <div class="rezultat-opcije">
          <label class="rezultat-opcija">
            <input type="radio" v-model="odabraniRezultat" value="DOMACIN" />
            <span>1 — Pobjeda domaćina</span>
          </label>
          <label class="rezultat-opcija">
            <input type="radio" v-model="odabraniRezultat" value="NERIJESENO" />
            <span>X — Neriješeno</span>
          </label>
          <label class="rezultat-opcija">
            <input type="radio" v-model="odabraniRezultat" value="GOST" />
            <span>2 — Pobjeda gosta</span>
          </label>
        </div>

        <div class="form-group">
          <label>Rezultat (npr. 2:1)</label>
          <input v-model="rezultatTekst" type="text" placeholder="2:1" />
        </div>

        <p v-if="modalGreska" class="error">{{ modalGreska }}</p>

        <div class="modal-akcije">
          <button class="btn-secondary" @click="zatvoriModal">Odustani</button>
          <button
            class="btn-primary"
            @click="postaviRezultat"
            :disabled="!odabraniRezultat || modalUcitava"
          >
            {{ modalUcitava ? 'Slanje...' : 'Potvrdi' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'

const dogadaji = ref([])
const ucitava = ref(false)
const prikaziFormModal = ref(false)
const prikaziRezultatModal = ref(false)
const urediDogadaj = ref(null)
const aktivniDogadaj = ref(null)
const modalGreska = ref('')
const modalUcitava = ref(false)
const odabraniRezultat = ref('')
const rezultatTekst = ref('')

const sportovi = ['FOOTBALL', 'BASKETBALL', 'TENNIS', 'BASEBALL', 'HOCKEY']

const forma = ref({
  naziv: '', sport: 'FOOTBALL', datum: '',
  kvotaDomacin: 1.50, kvotaNerijeseno: 3.00, kvotaGost: 2.50,
})

const dohvatiDogadaje = async () => {
  ucitava.value = true
  try {
    const response = await api.get('/api/dogadaji')
    dogadaji.value = response.data
  } catch (e) {
    console.error(e)
  } finally {
    ucitava.value = false
  }
}

const otvoriDodajModal = () => {
  urediDogadaj.value = null
  forma.value = { naziv: '', sport: 'FOOTBALL', datum: '', kvotaDomacin: 1.50, kvotaNerijeseno: 3.00, kvotaGost: 2.50 }
  modalGreska.value = ''
  prikaziFormModal.value = true
}

const otvoriUrediModal = (dogadaj) => {
  urediDogadaj.value = dogadaj
  forma.value = {
    naziv: dogadaj.naziv,
    sport: dogadaj.sport,
    datum: dogadaj.datum?.slice(0, 16),
    kvotaDomacin: dogadaj.kvotaDomacin,
    kvotaNerijeseno: dogadaj.kvotaNerijeseno,
    kvotaGost: dogadaj.kvotaGost,
  }
  modalGreska.value = ''
  prikaziFormModal.value = true
}

const otvoriRezultatModal = (dogadaj) => {
  aktivniDogadaj.value = dogadaj
  odabraniRezultat.value = ''
  rezultatTekst.value = ''
  modalGreska.value = ''
  prikaziRezultatModal.value = true
}

const zatvoriModal = () => {
  prikaziFormModal.value = false
  prikaziRezultatModal.value = false
}

const spremiDogadaj = async () => {
  modalGreska.value = ''
  modalUcitava.value = true
  try {
    if (urediDogadaj.value) {
      await api.put(`/api/dogadaji/${urediDogadaj.value.id}`, forma.value)
    } else {
      await api.post('/api/dogadaji', forma.value)
    }
    await dohvatiDogadaje()
    zatvoriModal()
  } catch (e) {
    modalGreska.value = 'Greška pri spremanju.'
  } finally {
    modalUcitava.value = false
  }
}

const postaviRezultat = async () => {
  modalGreska.value = ''
  modalUcitava.value = true
  try {
    await api.put(`/api/dogadaji/${aktivniDogadaj.value.id}/rezultat`, null, {
      params: {
    rezultat: rezultatTekst.value,
    pobjednickiTip: odabraniRezultat.value,
  }
    })
    await dohvatiDogadaje()
    zatvoriModal()
  } catch (e) {
    modalGreska.value = 'Greška pri postavljanju rezultata.'
  } finally {
    modalUcitava.value = false
  }
}

const obrisiDogadaj = async (id) => {
  if (!confirm('Sigurno želiš obrisati ovaj događaj?')) return
  try {
    await api.delete(`/api/dogadaji/${id}`)
    await dohvatiDogadaje()
  } catch (e) {
    console.error(e)
  }
}

const formatirajDatum = (datum) => {
  return new Date(datum).toLocaleDateString('hr-HR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
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
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
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
  color: var(--text-primary);
}

.tablica tr:hover td {
  background-color: var(--bg-secondary);
}

.status-badge {
  font-size: 0.7rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  text-transform: uppercase;
}

.status-badge.upcoming { background: rgba(59,130,246,0.15); color: var(--accent); }
.status-badge.live { background: rgba(239,68,68,0.15); color: var(--danger); }
.status-badge.finished { background: rgba(148,163,184,0.1); color: var(--text-secondary); }

.akcije {
  display: flex;
  gap: 0.5rem;
}

.btn-edit, .btn-rezultat, .btn-delete {
  padding: 0.3rem 0.75rem;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-edit {
  background: rgba(59,130,246,0.15);
  color: var(--accent);
}

.btn-edit:hover { background: rgba(59,130,246,0.3); }

.btn-rezultat {
  background: rgba(245,158,11,0.15);
  color: var(--warning);
}

.btn-rezultat:hover:not(:disabled) { background: rgba(245,158,11,0.3); }
.btn-rezultat:disabled { opacity: 0.4; cursor: not-allowed; }

.btn-delete {
  background: rgba(239,68,68,0.15);
  color: var(--danger);
}

.btn-delete:hover { background: rgba(239,68,68,0.3); }

.btn-primary {
  background-color: var(--accent);
  color: white;
  padding: 0.6rem 1.25rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  transition: background-color 0.2s;
}

.btn-primary:hover:not(:disabled) { background-color: var(--accent-hover); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-secondary {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  color: var(--text-secondary);
  padding: 0.6rem 1.25rem;
  border-radius: 8px;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.btn-secondary:hover { color: var(--text-primary); }

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}

.modal {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 2rem;
  width: 100%;
  max-width: 520px;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.modal h3 {
  font-size: 1.1rem;
  font-weight: 700;
}

.modal-subtitle {
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin-top: -0.75rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.form-group label {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.form-group input,
.form-group select {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0.65rem 1rem;
  color: var(--text-primary);
  font-size: 0.9rem;
}

.form-group input:focus,
.form-group select:focus {
  border-color: var(--accent);
}

.kvote-forma {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.75rem;
  min-width: 0;
}

.kvote-forma .form-group {
  min-width: 0;
}

.kvote-forma input {
  width: 100%;
  min-width: 0;
}

.rezultat-opcije {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.rezultat-opcija {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: border-color 0.2s;
  font-size: 0.9rem;
}

.rezultat-opcija:hover { border-color: var(--accent); }

.modal-akcije {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.error {
  color: var(--danger);
  font-size: 0.875rem;
}

.state-msg {
  text-align: center;
  color: var(--text-secondary);
  padding: 4rem 0;
}
</style>