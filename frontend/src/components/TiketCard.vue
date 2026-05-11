<template>
  <div class="tiket-card" :class="tiket.status.toLowerCase()">
    <div class="tiket-header">
      <div>
        <span class="tiket-id">#{{ tiket.id }}</span>
        <span class="tiket-datum">{{ formatirajDatum(tiket.datumPostavljanja) }}</span>
      </div>
      <span class="status-badge" :class="tiket.status.toLowerCase()">{{ tiket.status }}</span>
    </div>

    <div class="stavke">
      <div v-for="stavka in tiket.stavke" :key="stavka.id" class="stavka-row">
        <span class="stavka-naziv">{{ stavka.dogadaj?.naziv ?? 'Događaj' }}</span>
        <span class="stavka-tip">{{ tipLabel(stavka.tip) }}</span>
        <span class="stavka-kvota">{{ stavka.odabranaKvota }}</span>
      </div>
    </div>

    <div class="tiket-footer">
      <div class="tiket-iznos">
        <span>Ulog: <strong>{{ tiket.ukupnaUloga }} KM</strong></span>
        <span>Potencijalna dobit: <strong class="dobit">{{ tiket.potencijalnaDobitak }} KM</strong></span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ tiket: Object })

const tipLabel = (tip) => {
  const mapa = { DOMACIN: '1', NERIJESENO: 'X', GOST: '2' }
  return mapa[tip] ?? tip
}

const formatirajDatum = (datum) => {
  return new Date(datum).toLocaleDateString('hr-HR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}
</script>

<style scoped>
.tiket-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.tiket-card.dobitan { border-left: 3px solid var(--success); }
.tiket-card.gubitan { border-left: 3px solid var(--danger); }
.tiket-card.aktivan { border-left: 3px solid var(--accent); }

.tiket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tiket-id {
  font-weight: 700;
  margin-right: 0.75rem;
}

.tiket-datum {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.status-badge {
  font-size: 0.7rem;
  font-weight: 700;
  padding: 0.25rem 0.6rem;
  border-radius: 999px;
  text-transform: uppercase;
}

.status-badge.aktivan { background: rgba(59,130,246,0.15); color: var(--accent); }
.status-badge.dobitan { background: rgba(34,197,94,0.15); color: var(--success); }
.status-badge.gubitan { background: rgba(239,68,68,0.15); color: var(--danger); }

.stavke {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.stavka-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.875rem;
  color: var(--text-secondary);
  padding: 0.4rem 0;
  border-bottom: 1px solid var(--border);
}

.stavka-row:last-child { border-bottom: none; }

.stavka-naziv { flex: 1; color: var(--text-primary); }
.stavka-tip { width: 30px; text-align: center; font-weight: 600; }
.stavka-kvota { color: var(--accent); font-weight: 600; }

.tiket-footer {
  display: flex;
  justify-content: flex-end;
}

.tiket-iznos {
  display: flex;
  gap: 1.5rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.dobit { color: var(--success); }
</style>