<script setup lang="ts">
import type { SelectProps } from 'element-plus'

export type UiSelectOption = {
  label: string
  value: string | number
  disabled?: boolean
}

withDefaults(
    defineProps<{
      modelValue: string | number | undefined
      options: UiSelectOption[]
      placeholder?: string
      disabled?: boolean
      clearable?: boolean
      filterable?: boolean
      size?: SelectProps['size']
    }>(),
    {
      placeholder: 'Выберите значение',
      disabled: false,
      clearable: false,
      filterable: false,
      size: 'large',
    },
)

const emit = defineEmits<{
  'update:modelValue': [value: string | number | undefined]
  change: [value: string | number | undefined]
}>()
</script>

<template>
  <el-select
      :model-value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :clearable="clearable"
      :filterable="filterable"
      :size="size"
      class="coffee-select"
      popper-class="coffee-select-dropdown"
      @update:model-value="emit('update:modelValue', $event)"
      @change="emit('change', $event)"
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

<style>
/* Поле выбора */
.coffee-select {
  --el-select-border-color-hover: #b68b6c;
  --el-select-input-focus-border-color: #a66b45;
  --el-select-border-color: #e5d8ca;
  --el-select-input-color: #35251f;
  --el-select-disabled-border: #e5d8ca;
  --el-select-disabled-bg-color: #f5efe8;

  width: 100%;
}

.coffee-select .el-select__wrapper {
  min-height: 44px;
  padding: 4px 14px;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 0 0 1px #e5d8ca inset;
  transition: box-shadow 0.2s ease;
}

.coffee-select .el-select__wrapper:hover {
  box-shadow: 0 0 0 1px #b68b6c inset;
}

.coffee-select .el-select__wrapper.is-focused {
  box-shadow: 0 0 0 1px #a66b45 inset;
}

.coffee-select .el-select__placeholder {
  color: #a59689;
}

.coffee-select .el-select__selected-item {
  color: #35251f;
  font-weight: 500;
}

.coffee-select .el-select__caret {
  color: #8a6a55;
}

/* Выпадающий список.
   Класс popper находится вне компонента, поэтому стили глобальные. */
.coffee-select-dropdown {
  padding: 6px !important;
  border: 1px solid #e9dfd3 !important;
  border-radius: 14px !important;
  background-color: #fffdf9 !important;
  box-shadow: 0 12px 35px rgb(53 37 31 / 12%) !important;
}

.coffee-select-dropdown .el-select-dropdown__item {
  min-height: 40px;
  padding: 0 12px;
  border-radius: 9px;
  color: #59483d;
  transition:
      color 0.15s ease,
      background-color 0.15s ease;
}

.coffee-select-dropdown .el-select-dropdown__item.hover,
.coffee-select-dropdown .el-select-dropdown__item:hover {
  background-color: #f5eee6;
  color: #593a2b;
}

.coffee-select-dropdown .el-select-dropdown__item.is-selected {
  background-color: #eee1d4;
  color: #593a2b;
  font-weight: 600;
}

.coffee-select-dropdown .el-select-dropdown__item.is-disabled {
  color: #c2b7ad;
  cursor: not-allowed;
}
</style>