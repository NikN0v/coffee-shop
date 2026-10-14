<script setup lang="ts">
type SelectValue = string | number | boolean

type SelectOption = {
  label: string
  value: SelectValue
  disabled?: boolean
}

withDefaults(defineProps<{
  modelValue: SelectValue | null | undefined
  options: SelectOption[]
  placeholder?: string
  disabled?: boolean
  clearable?: boolean
  filterable?: boolean
}>(), {
  placeholder: '',
  disabled: false,
  clearable: false,
  filterable: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: SelectValue | null]
  change: [value: SelectValue | null]
}>()
</script>

<template>
  <el-select
      class="coffee-select"
      :model-value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :clearable="clearable"
      :filterable="filterable"
      @update:model-value="emit('update:modelValue', $event ?? null)"
      @change="emit('change', $event ?? null)"
  >
    <el-option
        v-for="option in options"
        :key="option.value"
        :label="option.label"
        :value="option.value"
        :disabled="option.disabled"
    />
  </el-select>
</template>