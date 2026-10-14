import {createRouter, createWebHistory, type RouteRecordRaw} from 'vue-router'
import {useAuthStore} from "@/stores/authStore.ts";

declare module 'vue-router' {
    interface RouteMeta {
        layout: 'main' | 'auth'
        requiresAuth?: boolean
        guestOnly?: boolean
        adminOnly?: boolean
    }
}

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        name: 'home',
        component: () => import('@/views/HomeView.vue'),
        meta: { layout: 'main' }
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/LoginView.vue'),
        meta: { layout: 'auth', guestOnly: true }
    },
    {
        path: '/register',
        name: 'register',
        component: () => import('@/views/RegisterView.vue'),
        meta: { layout: 'auth', guestOnly: true }
    },
    {
        path: '/admin',
        name: 'admin',
        component: () => import('@/views/AdminView.vue'),
        meta: { layout: 'main', requiresAuth: true, adminOnly: true }
    },
    {
        path: '/admin/categories',
        name: 'admin-categories',
        component: () => import('@/views/AdminCategoriesView.vue'),
        meta: { layout: 'main', requiresAuth: true, adminOnly: true }
    },
    {
      path: '/admin/products',
      name: 'admin-products',
      component: () => import('@/views/AdminProductsView.vue'),
      meta: { layout: 'main', requiresAuth: true, adminOnly: true }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'not-found',
        component: () => import('@/views/NotFound.vue'),
        meta: { layout: 'main' }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior: () => ({top: 0})
})

router.beforeEach((to) => {
    if (to.meta.requiresAuth && !useAuthStore().isAuthenticated) {
        return {
            name: 'login',
            query: { redirect: to.fullPath }
        }
    }

    if (to.meta.guestOnly && useAuthStore().isAuthenticated) return { name: 'home' }
    if (to.meta.adminOnly && useAuthStore().user?.role !== 'ADMIN') return { name: 'home' }

    return true
})

export default router
