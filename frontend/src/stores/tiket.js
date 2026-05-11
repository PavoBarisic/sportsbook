import { defineStore } from 'pinia'

export const useTiketStore = defineStore('tiket', {
  state: () => ({
    stavke: [],
  }),

  actions: {
    postaviStavke(stavke) {
      this.stavke = stavke
    },

    ocistiStavke() {
      this.stavke = []
    },
  },
})