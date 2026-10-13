import axios from "axios";

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 10_000,
    headers: {
        'Content-Type': 'application/json'
    }
})

api.interceptors.request.use((config) => {
    const token = sessionStorage.getItem('accessToken')
    if (token) config.headers.Authorization = `Bearer ${token}`

    const savedLocale = localStorage.getItem('locale')
    config.headers['Accept-Language'] = savedLocale === 'en' ? 'en' : 'ru'

    return config
})