export const CATEGORY_TYPES = [
    'DRINKS',
    'DESSERTS',
    'BAKERY',
    'FOOD',
    'OTHER'
] as const

export type CategoryType = (typeof CATEGORY_TYPES)[number]

export type CategoryResponse = {
    id: number
    name: string,
    nameRu: string,
    nameEn: string,
    type: CategoryType
}

export type CategoryCreateRequest = {
    nameRu: string
    nameEn: string,
    type: CategoryType
}

export type CategoryUpdateRequest = {
    nameRu: string
    nameEn: string
    type: CategoryType
}