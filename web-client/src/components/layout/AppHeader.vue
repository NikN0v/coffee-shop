<script setup lang="ts">
import {ref, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useI18n} from 'vue-i18n'
import {ArrowDown, Close, Menu, Setting} from '@element-plus/icons-vue'
import LanguageSwitcher from '@/components/common/LanguageSwitcher.vue'
import {useAuthStore} from '@/stores/authStore'
import {notificationService} from '@/services/notificationService'
import UiButton from "@/components/ui/UiButton.vue";

const {t} = useI18n()
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isMenuOpen = ref(false)

function handleAdminCommand(path: string) {
  router.push(path)
}

watch(
    () => route.fullPath,
    () => {
      isMenuOpen.value = false
    },
)

function logout(): void {
  authStore.logout()

  notificationService.success({
    title: t('nav.logout'),
    message: t('auth.logoutSuccess'),
  })

  router.push('/login')
}
</script>

<template>
  <header class="sticky top-0 z-50 border-b border-[#e9dfd3] bg-[#faf7f2]/95 backdrop-blur">
    <div class="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-4 sm:px-6 lg:px-8">
      <RouterLink to="/" class="shrink-0 text-xl font-bold sm:text-2xl">
        Coffee<span class="text-[#a66b45]">House</span>
      </RouterLink>

      <nav class="hidden items-center gap-8 md:flex">
        <RouterLink to="/" class="text-sm font-medium hover:text-[#a66b45]">
          {{ t('nav.home') }}
        </RouterLink>

        <RouterLink to="/menu" class="text-sm font-medium hover:text-[#a66b45]">
          {{ t('nav.menu') }}
        </RouterLink>

        <a href="#about" class="text-sm font-medium hover:text-[#a66b45]">
          {{ t('nav.about') }}
        </a>
      </nav>

      <div class="hidden items-center gap-3 md:flex">
        <LanguageSwitcher/>

        <template v-if="!authStore.isAuthenticated">
          <RouterLink
              to="/login"
              class="rounded-full px-4 py-2 font-semibold hover:bg-[#efe5d9]"
          >
            {{ t('nav.login') }}
          </RouterLink>

          <RouterLink
              to="/register"
              class="rounded-full bg-[#593a2b] px-5 py-2.5 font-semibold text-white hover:bg-[#754c37]"
          >
            {{ t('nav.register') }}
          </RouterLink>
        </template>

        <template v-else>
          <span class="max-w-64 truncate text-sm font-bold">
            {{ authStore.user?.name }}
          </span>

          <UiButton @click="logout">
            {{ t('nav.logout') }}
          </UiButton>
        </template>
      </div>

      <el-dropdown
        v-if="authStore.user?.role === 'ADMIN'"
        trigger="click"
        @command="handleAdminCommand"
        popper-class="admin-dropdown-popper"
      >
        <UiButton class="admin-menu-button" type="default">
          <el-icon class="mr-2">
            <Setting/>
          </el-icon>
          {{ t('nav.control') }}
          <el-icon class="ml-2">
            <ArrowDown/>
          </el-icon>
        </UiButton>

        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="/admin/categories">
              {{ t('nav.category') }}
            </el-dropdown-item>
            <el-dropdown-item command="/admin/products">
              {{ t('nav.product') }}
            </el-dropdown-item>
            <el-dropdown-item command="/admin/orders" disabled>
              {{ t('nav.order') }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <UiButton class="!md:hidden" aria-label="Toggle navigation" @click="isMenuOpen = !isMenuOpen">
        <el-icon>
          <component :is="isMenuOpen ? Close : Menu"/>
        </el-icon>
      </UiButton>
    </div>

    <div v-if="isMenuOpen" class="border-t border-[#e9dfd3] p-4 md:hidden">
      <div class="mx-auto flex max-w-7xl flex-col gap-3">
        <RouterLink to="/" class="py-2">{{ t('nav.home') }}</RouterLink>
        <a href="#menu" class="py-2">{{ t('nav.menu') }}</a>
        <a href="#about" class="py-2">{{ t('nav.about') }}</a>

        <LanguageSwitcher/>

        <template v-if="!authStore.isAuthenticated">
          <RouterLink to="/login" class="py-2">{{ t('nav.login') }}</RouterLink>
          <RouterLink to="/register" class="py-2">{{ t('nav.register') }}</RouterLink>
        </template>

        <template v-else>
          <span>{{ authStore.user?.name }}</span>
          <UiButton @click="logout">
            {{ t('nav.logout') }}
          </UiButton>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
@media (max-width: 767px) {
  .el-button {
    display: inline-flex;
  }
}
</style>