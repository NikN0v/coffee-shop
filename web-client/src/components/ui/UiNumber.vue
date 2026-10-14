<script setup lang="ts">
withDefaults(
    defineProps<{
      modelValue: number | null
      min?: number
      max?: number
      step?: number
      precision?: number
      placeholder?: string
      disabled?: boolean
      controls?: boolean
    }>(),
    {
      min: 0,
      step: 1,
      precision: 0,
      placeholder: '',
      disabled: false,
      controls: false,
    },
)

const emit = defineEmits<{
  'update:modelValue': [value: number | null]
  change: [value: number | null, oldValue: number | undefined]
}>()

function handleChange(
    value: number | undefined,
    oldValue: number | undefined
): void {
  emit('change', value ?? null, oldValue)
}
</script>

<template>
  <el-input-number
      class="ui-number"
      :model-value="modelValue"
      :min="min"
      :max="max"
      :step="step"
      :precision="precision"
      :placeholder="placeholder"
      :disabled="disabled"
      :controls="controls"
      @update:model-value="emit('update:modelValue', $event ?? null)"
      @change="handleChange"
  />
</template>