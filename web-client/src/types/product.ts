export type ProductResponse = {
    id: number
    categoryId: number
    name: string
    nameRu: string
    nameEn: string
    description: string | null
    descriptionRu: string | null
    descriptionEn: string | null
    price: number
    available: boolean
    weight: number | null
    isDrink: boolean
    volume: number | null
}

export type ProductCreateRequest = {
    categoryId: number
    nameRu: string
    nameEn: string
    descriptionRu: string | null
    descriptionEn: string | null
    price: number
    available: boolean
    weight: number | null
    isDrink: boolean
    volume: number | null
}

export type ProductUpdateRequest = ProductCreateRequest

export type ProductPatchRequest = {
    categoryId?: number | null
    nameRu?: string | null
    nameEn?: string | null
    descriptionRu?: string | null
    descriptionEn?: string | null
    price?: number | null
    available?: boolean | null
    weight?: number | null
    isDrink?: boolean | null
    volume?: number | null
}

export type ProductQueryParams = {
    categoryId?: number
    available?: boolean
    page?: number
    size?: number
    sortBy?: string
    direction?: 'asc' | 'desc'
}

export type PageResponse<T> = {
    content: T[]
    page: number
    size: number
    totalElements: number
    totalPages: number
    first: boolean
    last: boolean
}