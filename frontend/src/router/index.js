import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Auth/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Auth/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('@/views/Admin/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/customer/dashboard',
    name: 'CustomerDashboard',
    component: () => import('@/views/Customer/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'user' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.role && to.meta.role !== userStore.role) {
    if (userStore.role === 'admin') {
      next('/admin/dashboard')
    } else {
      next('/customer/dashboard')
    }
  } else if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
    if (userStore.role === 'admin') {
      next('/admin/dashboard')
    } else {
      next('/customer/dashboard')
    }
  } else {
    next()
  }
})

export default router
