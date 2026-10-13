<script setup lang="ts">
import type {ButtonProps} from "element-plus";

withDefaults(
    defineProps<{
      type?: ButtonProps['type']
      nativeType?: ButtonProps['nativeType']
      loading?: boolean
      disabled?: boolean
      block?: boolean
    }>(),
    {
      type: 'primary',
      nativeType: 'button',
      loading: false,
      disabled: false,
      block: false
    }
)
</script>

<template>
  <el-button
    :type="type"
    :native-type="nativeType"
    :loading="loading"
    :disabled="disabled"
    :class="[
        'coffee-button',
        { 'coffee-button--block': block }
    ]"
  >
    <slot/>
  </el-button>
</template>

<style scoped>
.coffee-button {
  min-height: 44px;
  padding: 0 22px;
  border-radius: 9999px;
  font-weight: 600;
  transition: background-color .2s, border-color .2s;
}

.coffee-button.el-button--primary {
  --el-button-bg-color: #593a2b;
  --el-button-border-color: #593a2b;
  --el-button-hover-bg-color: #754c37;
  --el-button-hover-border-color: #754c37;
  --el-button-active-bg-color: #40291f;
  --el-button-active-border-color: #40291f;
  --el-button-text-color: #fff;
}

.coffee-button--block {
  width: 100%;
}
</style>