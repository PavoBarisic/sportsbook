import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', redirect: '/dogadaji' },
  { path: '/login', component: () => import('../views/LoginView.vue') },
  { path: '/register', component: () => import('../views/RegisterView.vue') },
  {
    path: '/dogadaji',
    component: () => import('../views/DogadajiView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/tiket',
    component: () => import('../views/TiketView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/moji-tiketi',
    component: () => import('../views/MojiTiketiView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/profil',
    component: () => import('../views/ProfilView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/dogadaji',
    component: () => import('../views/admin/AdminDogadajiView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/admin/tiketi',
    component: () => import('../views/admin/AdminTiketiView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/admin/korisnici',
    component: () => import('../views/admin/AdminKorisniciView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return '/login'
  }

  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return '/dogadaji'
  }
})

export default router