<script setup lang="ts">
import type {InputProps} from "element-plus";

withDefaults(
    defineProps<{
      modelValue: string,
      type?: InputProps['type']
      placeholder?: string
      autocomplete?: string
      maxlength?: number
      disabled?: boolean
      showPassword?: boolean
    }>(),
    {
      type: 'text',
      placeholder: '',
      autocomplete: 'off',
      disabled: false,
      showPassword: false
    }
)

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()
</script>

<template>
  <el-input
    :model-value="modelValue"
    :type="type"
    :placeholder="placeholder"
    :autocomplete="autocomplete"
    :maxlength="maxlength"
    :disabled="disabled"
    :show-password="showPassword"
    class="coffee-input"
    @update:model-value="emit('update:modelValue', $event)"
  />
</template>

<style scoped>
.coffee-input {
  --el-input-border-radius: 12px;
  --el-input-bg-color: #fff;
  --el-input-border-color: #e5d8ca;
  --el-input-hover-border-color: #b68b6c;
  --el-input-focus-border-color: #a66b45;
  --el-input-text-color: #35251f;
  --el-input-placeholder-color: #a59689;
}

.coffee-input :deep(.el-input__wrapper) {
  min-height: 44px;
  padding: 1px 14px;
  box-shadow: 0 0 0 1px var(--el-input-border-color) inset;
}

.coffee-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--el-input-focus-border-color) inset;
}
</style>