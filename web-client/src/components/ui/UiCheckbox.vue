<script setup lang="ts">
withDefaults(
    defineProps<{
      modelValue: boolean
      label?: string
      disabled?: boolean
    }>(),
    {
      label: '',
      disabled: false,
    },
)

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  change: [value: boolean]
}>()
</script>

<template>
  <el-checkbox
      class="ui-checkbox"
      :model-value="modelValue"
      :label="label || undefined"
      :disabled="disabled"
      @update:model-value="emit('update:modelValue', $event)"
      @change="emit('change', $event)"
  >
    <slot>{{ label }}</slot>
  </el-checkbox>
</template>