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
    path: '/admin',
    component: () => import('@/views/SharedLayout.vue'),
    meta: { requiresAuth: true, role: 'admin' },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/Admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/Admin/Users.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/Admin/Orders.vue')
      },
      {
        path: 'ports',
        name: 'AdminPorts',
        component: () => import('@/views/Admin/Ports.vue')
      },
      {
        path: 'ships',
        name: 'AdminShips',
        component: () => import('@/views/Admin/Ships.vue')
      },
      {
        path: 'vehicles',
        name: 'AdminVehicles',
        component: () => import('@/views/Admin/Vehicles.vue')
      },
      {
        path: 'companies',
        name: 'AdminCompanies',
        component: () => import('@/views/Admin/Companies.vue')
      },
      {
        path: 'logistics',
        name: 'AdminLogistics',
        component: () => import('@/views/Admin/Logistics.vue')
      },
      {
        path: 'containers',
        name: 'AdminContainers',
        component: () => import('@/views/Admin/Containers.vue')
      },
      {
        path: 'dispatch',
        name: 'AdminDispatch',
        component: () => import('@/views/Admin/Dispatch.vue')
      },
      {
        path: 'report',
        name: 'AdminReport',
        component: () => import('@/views/Admin/Report.vue')
      }
    ]
  },
  {
    path: '/customer',
    component: () => import('@/views/SharedLayout.vue'),
    meta: { requiresAuth: true, role: 'user' },
    children: [
      {
        path: '',
        redirect: '/customer/dashboard'
      },
      {
        path: 'dashboard',
        name: 'CustomerDashboard',
        component: () => import('@/views/Customer/Dashboard.vue')
      },
      {
        path: 'orders',
        name: 'CustomerOrders',
        component: () => import('@/views/Customer/Orders.vue')
      },
      {
        path: 'logistics',
        name: 'CustomerLogistics',
        component: () => import('@/views/Customer/Logistics.vue')
      },
      {
        path: 'containers',
        name: 'CustomerContainers',
        component: () => import('@/views/Customer/Containers.vue')
      },
      {
        path: 'ships',
        name: 'CustomerShips',
        component: () => import('@/views/Customer/Ships.vue')
      },
      {
        path: 'vehicles',
        name: 'CustomerVehicles',
        component: () => import('@/views/Customer/Vehicles.vue')
      },
      {
        path: 'companies',
        name: 'CustomerCompanies',
        component: () => import('@/views/Customer/Companies.vue')
      },
      {
        path: 'ports',
        name: 'CustomerPorts',
        component: () => import('@/views/Customer/Ports.vue')
      }
    ]
  },
  {
    path: '/ai/chat',
    name: 'AIChat',
    component: () => import('@/views/AIAgent/Chat.vue'),
    meta: { requiresAuth: true }
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
    return
  }

  const routeRole = to.matched.find(r => r.meta.role)?.meta?.role
  if (routeRole && routeRole !== userStore.role) {
    if (userStore.role === 'admin') {
      next('/admin/dashboard')
    } else {
      next('/customer/dashboard')
    }
    return
  }

  if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
    if (userStore.role === 'admin') {
      next('/admin/dashboard')
    } else {
      next('/customer/dashboard')
    }
    return
  }

  next()
})

export default router
