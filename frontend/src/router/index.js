import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import HomeView from '../views/HomeView.vue'
import AdminLoginView from '../views/admin/AdminLoginView.vue'
import AdminDashboardView from '../views/admin/AdminDashboardView.vue'
import AdminResultsView from '../views/admin/AdminResultsView.vue'
import AdminCertificatesView from '../views/admin/AdminCertificatesView.vue'
import NotFoundView from '../views/NotFoundView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: HomeView,
      meta: { title: 'NutritionistHacksDaily — Hasan Abazi' },
    },
    {
      path: '/admin/login',
      component: AdminLoginView,
      meta: { title: 'Admin Login — NutritionistHacksDaily' },
    },
    {
      path: '/admin/dashboard',
      component: AdminDashboardView,
      meta: { requiresAuth: true, title: 'Dashboard — Admin' },
    },
    {
      path: '/admin/results',
      component: AdminResultsView,
      meta: { requiresAuth: true, title: 'Rezultatet — Admin' },
    },
    {
      path: '/admin/certificates',
      component: AdminCertificatesView,
      meta: { requiresAuth: true, title: 'Çertifikatat — Admin' },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: NotFoundView,
      meta: { title: '404 — Faqja nuk u gjet' },
    },
  ],
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !useAuthStore().isAuthenticated) {
    return '/admin/login'
  }
})

router.afterEach((to) => {
  document.title = to.meta.title || 'NutritionistHacksDaily'
})

export default router
