<script setup lang="ts">
withDefaults(defineProps<{
  modelValue: string
  type?: string
  placeholder?: string
  autocomplete?: string
  maxlength?: number
  disabled?: boolean
  showPassword?: boolean
}>(), {
  type: 'text',
  placeholder: '',
  autocomplete: 'off',
  disabled: false,
  showPassword: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()
</script>

<template>
  <el-input
      class="coffee-input"
      :model-value="modelValue"
      :type="type"
      :placeholder="placeholder"
      :autocomplete="autocomplete"
      :maxlength="maxlength"
      :disabled="disabled"
      :show-password="showPassword"
      @update:model-value="emit('update:modelValue', $event)"
      v-bind="$attrs"
  />
</template>