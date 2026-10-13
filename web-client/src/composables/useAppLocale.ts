import {useI18n} from "vue-i18n";
import {computed} from "vue";
import type {AppLocale} from "@/i18n";

export function useAppLocale() {
    const { locale } = useI18n({ useScope: 'global' })

    const currentLocale = computed<AppLocale>({
        get: () => locale.value as AppLocale,
        set: (val) => {
            locale.value = val
            localStorage.setItem('locale', val)
        }
    })

    return { currentLocale }
}