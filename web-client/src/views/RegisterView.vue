<script setup lang="ts">
import {reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useI18n} from 'vue-i18n'
import {type FormInstance, type FormRules} from 'element-plus'

import {useAuthStore} from '@/stores/authStore'
import type {RegisterRequest} from '@/types/auth'
import UiInput from "@/components/ui/UiInput.vue";
import UiButton from "@/components/ui/UiButton.vue";
import {notificationService} from "@/services/notificationService.ts";

type RegisterForm = RegisterRequest & {
  confirmPassword: string
}

const {t, locale} = useI18n()
const router = useRouter()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const isSubmitting = ref(false)

const form = reactive<RegisterForm>({
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const validatePasswordConfirmation = (
    _rule: unknown,
    value: string,
    callback: (error?: Error) => void,
): void => {
  if (!value) {
    callback(new Error(t('auth.required')))
    return
  }

  if (value !== form.password) {
    callback(new Error(t('auth.passwordMismatch')))
    return
  }

  callback()
}

const rules: FormRules<RegisterForm> = {
  name: [
    {
      required: true,
      message: t('auth.required'),
      trigger: 'blur',
    },
    {
      max: 100,
      message: 'Name must not exceed 100 characters',
      trigger: 'blur',
    },
  ],
  email: [
    {
      required: true,
      message: t('auth.required'),
      trigger: 'blur',
    },
    {
      type: 'email',
      message: t('auth.invalidEmail'),
      trigger: ['blur', 'change'],
    },
    {
      max: 255,
      message: 'Email must not exceed 255 characters',
      trigger: 'blur',
    },
  ],
  password: [
    {
      required: true,
      message: t('auth.required'),
      trigger: 'blur',
    },
    {
      min: 8,
      max: 72,
      message: t('auth.passwordLength'),
      trigger: 'blur',
    },
  ],
  confirmPassword: [
    {
      validator: validatePasswordConfirmation,
      trigger: ['blur', 'change'],
    },
  ],
}

async function submit(): Promise<void> {
  if (isSubmitting.value) return

  const formInstance = formRef.value
  if (!formInstance) return

  const valid = await formInstance.validate().catch(() => false)
  if (!valid) return

  isSubmitting.value = true

  try {
    await authStore.register({
      name: form.name.trim(),
      email: form.email.trim(),
      password: form.password,
    })

    notificationService.success({
      title: t('auth.regSuccess'),
      message: t('auth.regSuccessMessage')
    })

    await router.push({name: 'login'})
  } catch {
    notificationService.error({
      title: t('auth.regError'),
      message: t('auth.requestFailed')
    })
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <section class="mx-auto flex min-h-[65vh] max-w-md items-center px-4 py-12">
    <div class="w-full rounded-3xl border border-[#e9dfd3] bg-white p-6 shadow-sm sm:p-8">
      <p class="text-sm font-semibold uppercase tracking-widest text-[#a66b45]">
        CoffeeHouse
      </p>

      <h1 class="mt-4 text-3xl font-bold">
        {{ t('auth.registerTitle') }}
      </h1>

      <p class="mt-2 text-[#78675c]">
        {{ t('auth.registerDescription') }}
      </p>

      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="mt-7"
          @submit.prevent="submit"
      >
        <el-form-item :label="t('auth.name')" prop="name">
          <UiInput
              v-model="form.name"
              type="text"
              autocomplete="name"
              :maxlength="100"
              :disabled="isSubmitting"
          />
        </el-form-item>

        <el-form-item :label="t('auth.email')" prop="email">
          <UiInput
              v-model="form.email"
              type="email"
              autocomplete="email"
              :maxlength="255"
              :disabled="isSubmitting"
              placeholder="email@example.com"
          />
        </el-form-item>

        <el-form-item :label="t('auth.password')" prop="password">
          <UiInput
              v-model="form.password"
              type="password"
              autocomplete="new-password"
              :show-password="true"
              :disabled="isSubmitting"
          />
        </el-form-item>

        <el-form-item
            :label="t('auth.confirmPassword')"
            prop="confirmPassword"
        >
          <UiInput
              v-model="form.confirmPassword"
              type="password"
              autocomplete="new-password"
              :show-password="true"
              :disabled="isSubmitting"
              @keyup.enter="submit"
          />
        </el-form-item>

        <UiButton
            nativeType="submit"
            :loading="isSubmitting"
            :disabled="isSubmitting"
            block
        >
          {{ isSubmitting ? t('auth.loading') : t('auth.submitLogin') }}
        </UiButton>
      </el-form>

      <p class="mt-6 text-center text-sm text-[#78675c]">
        {{ t('auth.hasAccount') }}

        <RouterLink
            to="/login"
            class="font-semibold text-[#a66b45] hover:text-[#754c37]"
        >
          {{ t('nav.login') }}
        </RouterLink>
      </p>
    </div>
  </section>
</template>