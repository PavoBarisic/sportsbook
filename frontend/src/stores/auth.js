import { defineStore } from 'pinia'
import api from '../services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    korisnik: null,
  }),

  getters: {
    isLoggedIn: state => !!state.token,
    isAdmin: state => state.korisnik?.rola === 'ADMIN',
  },

  actions: {
    async login(email, lozinka) {
      const response = await api.post('/auth/login', { email, lozinka })
      this.token = response.data.token
      localStorage.setItem('token', this.token)
      await this.dohvatiKorisnika()
    },

    async dohvatiKorisnika() {
      const response = await api.get('/api/korisnici/me')
      this.korisnik = response.data
    },

    logout() {
      this.token = null
      this.korisnik = null
      localStorage.removeItem('token')
    },
  },
})