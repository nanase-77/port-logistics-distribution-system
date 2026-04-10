import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        username: localStorage.getItem('username') || '',
        role: localStorage.getItem('role') || ''
    }),
    getters: {
        isLoggedIn: (state) => !!state.token,
        isAdmin: (state) => state.role === 'admin',
        isUser: (state) => state.role === 'user'
    },
    actions: {
        login(userData) {
            this.token = userData.token
            this.username = userData.username
            this.role = userData.role
            localStorage.setItem('token', userData.token)
            localStorage.setItem('username', userData.username)
            localStorage.setItem('role', userData.role)
        },
        logout() {
            this.token = ''
            this.username = ''
            this.role = ''
            localStorage.removeItem('token')
            localStorage.removeItem('username')
            localStorage.removeItem('role')
        }
    }
})