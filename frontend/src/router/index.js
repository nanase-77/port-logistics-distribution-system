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
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('@/views/Admin/Users.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/orders',
    name: 'AdminOrders',
    component: () => import('@/views/Admin/Orders.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/ports',
    name: 'AdminPorts',
    component: () => import('@/views/Admin/Ports.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/ships',
    name: 'AdminShips',
    component: () => import('@/views/Admin/Ships.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/report',
    name: 'AdminReport',
    component: () => import('@/views/Statistics/Report.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/ai/chat',
    name: 'AIChat',
    component: () => import('@/views/AIAgent/Chat.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/customer/dashboard',
    name: 'CustomerDashboard',
    component: () => import('@/views/Customer/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'user' }
  },
  {
    path: '/customer/orders',
    name: 'CustomerOrders',
    component: () => import('@/views/Customer/Orders.vue'),
    meta: { requiresAuth: true, role: 'user' }
  },
  {
    path: '/customer/logistics',
    name: 'CustomerLogistics',
    component: () => import('@/views/Customer/Logistics.vue'),
    meta: { requiresAuth: true, role: 'user' }
  },
  {
    path: '/customer/containers',
    name: 'CustomerContainers',
    component: () => import('@/views/Customer/Containers.vue'),
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
