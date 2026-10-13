<script setup lang="ts">
import {useAppLocale} from "@/composables/useAppLocale.ts";

const { currentLocale } = useAppLocale()

const languages = [
  { value: 'ru', label: 'RU' },
  { value: 'en', label: 'EN' },
]
</script>

<template>
  <el-select
    v-model="currentLocale"
    class="coffee-language-select"
    popper-class="coffee-select-dropdown"
    aria-label="Language"
    size="large"
    :teleported="true"
  >
    <el-option
      v-for="language in languages"
      :key="language.value"
      :value="language.value"
      :label="language.label"
    />
  </el-select>
</template>

<style scoped>
.coffee-language-select {
  width: 125px;
}

.coffee-language-select :deep(.el-select__wrapper) {
  min-height: 42px;
  border-radius: 9999px;
  background: #fff;
  box-shadow: 0 0 0 1px #e9dfd3 inset;
}

.coffee-language-select :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #a66b45 inset;
}

.coffee-language-select :deep(.el-select__selected-item) {
  color: #593a2b;
  font-weight: 600;
}
</style>