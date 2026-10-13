export type RegisterRequest = {
    email: string,
    password: string,
    name: string
}

export type LoginRequest = {
    email: string,
    password: string
}

export type UserResponse = {
    id: number,
    email: string,
    role: string,
    name: string
}

export type AuthResponse = {
    accessToken: string,
    tokenType: string,
    user: UserResponse
}