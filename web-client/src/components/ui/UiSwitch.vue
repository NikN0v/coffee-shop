<script setup lang="ts">
withDefaults(
    defineProps<{
      modelValue: boolean
      disabled?: boolean
      activeText?: string
      inactiveText?: string
      inlinePrompt?: boolean
    }>(),
    {
      disabled: false,
      activeText: '',
      inactiveText: '',
      inlinePrompt: false,
    },
)

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  change: [value: boolean]
}>()
</script>

<template>
  <el-switch
      class="ui-switch"
      :model-value="modelValue"
      :disabled="disabled"
      :active-text="activeText"
      :inactive-text="inactiveText"
      :inline-prompt="inlinePrompt"
      @update:model-value="emit('update:modelValue', $event)"
      @change="emit('change', $event)"
  />
</template>