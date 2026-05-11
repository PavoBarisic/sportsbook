<template>
  <div class="card">
    <div class="card-header">
      <span class="sport-badge">{{ sportIkona(dogadaj.sport) }} {{ dogadaj.sport }}</span>
      <span class="status-badge" :class="dogadaj.status.toLowerCase()">{{ dogadaj.status }}</span>
    </div>

    <h3 class="card-title">{{ dogadaj.naziv }}</h3>
    <p class="card-date">{{ formatirajDatum(dogadaj.datum) }}</p>

    <div class="kvote" v-if="dogadaj.status === 'UPCOMING'">
      <button
        class="kvota-btn"
        :class="{ active: odabranaStavka?.tip === 'DOMACIN' }"
        @click="$emit('odaberi', { dogadaj, tip: 'DOMACIN', kvota: dogadaj.kvotaDomacin })"
      >
        <span class="kvota-label">1</span>
        <span class="kvota-value">{{ dogadaj.kvotaDomacin }}</span>
      </button>

      <button
        class="kvota-btn"
        :class="{ active: odabranaStavka?.tip === 'NERIJESENO' }"
        @click="$emit('odaberi', { dogadaj, tip: 'NERIJESENO', kvota: dogadaj.kvotaNerijeseno })"
      >
        <span class="kvota-label">X</span>
        <span class="kvota-value">{{ dogadaj.kvotaNerijeseno }}</span>
      </button>

      <button
        class="kvota-btn"
        :class="{ active: odabranaStavka?.tip === 'GOST' }"
        @click="$emit('odaberi', { dogadaj, tip: 'GOST', kvota: dogadaj.kvotaGost })"
      >
        <span class="kvota-label">2</span>
        <span class="kvota-value">{{ dogadaj.kvotaGost }}</span>
      </button>
    </div>

    <p v-else-if="dogadaj.status === 'FINISHED'" class="rezultat">
      Rezultat: {{ dogadaj.rezultat ?? 'N/A' }}
    </p>

    <p v-else class="live-label">🔴 UŽIVO</p>
  </div>
</template>

<script setup>
defineProps({
  dogadaj: Object,
  odabranaStavka: Object,
})

defineEmits(['odaberi'])

const sportIkona = (sport) => {
  const ikone = {
    FOOTBALL: '⚽',
    BASKETBALL: '🏀',
    TENNIS: '🎾',
    BASEBALL: '⚾',
    HOCKEY: '🏒',
  }
  return ikone[sport] ?? '🏅'
}

const formatirajDatum = (datum) => {
  return new Date(datum).toLocaleDateString('hr-HR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<style scoped>
.card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  transition: border-color 0.2s;
}

.card:hover {
  border-color: var(--accent);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sport-badge {
  font-size: 0.75rem;
  color: var(--text-secondary);
  font-weight: 500;
}

.status-badge {
  font-size: 0.7rem;
  font-weight: 700;
  padding: 0.25rem 0.6rem;
  border-radius: 999px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-badge.upcoming {
  background-color: rgba(59, 130, 246, 0.15);
  color: var(--accent);
}

.status-badge.live {
  background-color: rgba(239, 68, 68, 0.15);
  color: var(--danger);
}

.status-badge.finished {
  background-color: rgba(148, 163, 184, 0.1);
  color: var(--text-secondary);
}

.card-title {
  font-size: 1rem;
  font-weight: 600;
  line-height: 1.4;
}

.card-date {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.kvote {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 0.5rem;
  margin-top: 0.25rem;
}

.kvota-btn {
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.2rem;
  transition: all 0.2s;
}

.kvota-btn:hover {
  border-color: var(--accent);
}

.kvota-btn.active {
  background-color: var(--accent);
  border-color: var(--accent);
}

.kvota-label {
  font-size: 0.7rem;
  color: var(--text-secondary);
}

.kvota-btn.active .kvota-label {
  color: rgba(255,255,255,0.7);
}

.kvota-value {
  font-size: 1rem;
  font-weight: 700;
  color: var(--text-primary);
}

.rezultat {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.live-label {
  font-size: 0.875rem;
  color: var(--danger);
  font-weight: 600;
}
</style>