import type {AuthResponse, LoginRequest, RegisterRequest, UserResponse} from "@/types/auth.ts";
import {api} from "@/api/axios.ts";

export const authApi = {
    async register(
        request: RegisterRequest
    ): Promise<UserResponse> {
        const { data } = await api.post<UserResponse>('/auth/register', request)
        return data
    },

    async login(
      request: LoginRequest
    ): Promise<AuthResponse> {
        const { data } = await api.post('/auth/login', request)
        return data
    }
}