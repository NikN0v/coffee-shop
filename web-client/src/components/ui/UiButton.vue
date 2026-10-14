<script setup lang="ts">
import type { ButtonProps } from 'element-plus'

withDefaults(defineProps<{
  type?: ButtonProps['type']
  nativeType?: ButtonProps['nativeType']
  loading?: boolean
  disabled?: boolean
  block?: boolean
}>(), {
  type: 'primary',
  nativeType: 'button',
  loading: false,
  disabled: false,
  block: false,
})
</script>

<template>
  <el-button
      class="coffee-button"
      :type="type"
      :native-type="nativeType"
      :loading="loading"
      :disabled="disabled"
      :class="{ 'coffee-button--block': block }"
  >
    <slot />
  </el-button>
</template>