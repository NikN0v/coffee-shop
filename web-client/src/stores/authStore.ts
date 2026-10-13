import type {LoginRequest, RegisterRequest, UserResponse} from "@/types/auth.ts";
import {defineStore} from "pinia";
import {computed, ref} from "vue";
import {authApi} from "@/api/authApi.ts";

const TOKEN_KEY = 'accessToken'
const USER_KEY = 'currentUser'

function readStoredUser(): UserResponse | null {
    const value = sessionStorage.getItem(USER_KEY)
    if (!value) return null

    try {
        return JSON.parse(value) as UserResponse
    } catch {
        sessionStorage.removeItem(USER_KEY)
        return null
    }
}

export const useAuthStore = defineStore('auth', () => {
    const token = ref<string | null>(sessionStorage.getItem(TOKEN_KEY))
    const user = ref<UserResponse | null>(readStoredUser())
    const isAuthenticated = computed(() => Boolean(token.value && user.value))

    async function login(request: LoginRequest): Promise<void> {
        const res = await authApi.login(request)

        token.value = res.accessToken
        user.value = res.user

        sessionStorage.setItem(TOKEN_KEY, res.accessToken)
        sessionStorage.setItem(USER_KEY, JSON.stringify(res.user))
    }

    async function register(request: RegisterRequest): Promise<UserResponse> {
        return authApi.register(request)
    }

    function logout(): void {
        token.value = null
        user.value = null

        sessionStorage.removeItem(TOKEN_KEY)
        sessionStorage.removeItem(USER_KEY)
    }

    return {
        token, user, isAuthenticated,
        login, register, logout
    }
})