<script setup lang="ts">
withDefaults(
    defineProps<{
      modelValue: string
      placeholder?: string
      rows?: number
      maxlength?: number
      disabled?: boolean
      resize?: 'none' | 'both' | 'horizontal' | 'vertical'
      showWordLimit?: boolean
    }>(),
    {
      placeholder: '',
      rows: 3,
      disabled: false,
      resize: 'vertical',
      showWordLimit: false,
    },
)

const emit = defineEmits<{
  'update:modelValue': [value: string]
  blur: [event: FocusEvent]
  change: [value: string]
}>()
</script>

<template>
  <el-input
      class="ui-textarea"
      :model-value="modelValue"
      type="textarea"
      :placeholder="placeholder"
      :rows="rows"
      :maxlength="maxlength"
      :disabled="disabled"
      :resize="resize"
      :show-word-limit="showWordLimit"
      @update:model-value="emit('update:modelValue', $event)"
      @blur="emit('blur', $event)"
      @change="emit('change', $event)"
  />
</template>