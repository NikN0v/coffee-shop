<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {useAuthStore} from "@/stores/authStore.ts";
import {Setting} from "@element-plus/icons-vue";

const { t } = useI18n()
const authStore = useAuthStore()
</script>

<template>
  <section class="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
    <el-card shadow="never" class="admin-card">
      <div class="flex items-center gap-3">
        <el-icon :size="28" color="#593a2b">
          <Setting />
        </el-icon>

        <div>
          <h1 class="text-2xl font-bold text-[#35251f]">
            {{ t('admin.title') }}
          </h1>

          <p class="mt-1 text-sm text-[#78675c]">
            {{ authStore.user?.name }}
          </p>
        </div>
      </div>

      <el-divider />

      <p class="text-[#78675c]">
        {{ t('admin.description') }}
      </p>
    </el-card>
  </section>
</template>

<style scoped>
.admin-card {
  border-color: #e9dfd3;
  border-radius: 16px;
  background-color: #fffdf9;
}
</style>