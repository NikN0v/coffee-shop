<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {useRoute, useRouter} from "vue-router";
import {useAuthStore} from "@/stores/authStore.ts";
import {reactive, ref} from "vue";
import {type FormInstance, type FormRules} from "element-plus";
import type {LoginRequest} from "@/types/auth.ts";
import UiInput from "@/components/ui/UiInput.vue";
import UiButton from "@/components/ui/UiButton.vue";
import {notificationService} from "@/services/notificationService.ts";

const {t} = useI18n()
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const isSubmitting = ref(false)

const form = reactive<LoginRequest>({
  email: '', password: ''
})

const rules: FormRules<LoginRequest> = {
  email: [
    {
      required: true,
      message: t('auth.required'),
      trigger: 'blur'
    },
    {
      type: 'email',
      message: t('auth.invalidEmail'),
      trigger: ['blur', 'change']
    },
    {
      max: 255,
      message: t('auth.emailLength'),
      trigger: 'blur'
    }
  ],
  password: [
    {
      required: true,
      message: t('auth.required'),
      trigger: 'blur'
    },
    {
      max: 72,
      message: t('auth.passwordLength'),
      trigger: 'blur'
    }
  ]
}

async function submit(): Promise<void> {
  if (isSubmitting.value) return

  const formInstance = formRef.value
  if (!formInstance) return

  const valid = await formInstance.validate().catch(() => false)
  if (!valid) return

  isSubmitting.value = true

  try {
    await authStore.login(form)
    notificationService.success({
      title: t('auth.loginSuccess'),
      message: t('auth.loginSuccessMessage')
    })

    const redirect = route.query.redirect
    const target =
        typeof redirect === 'string' &&
        redirect.startsWith('/') &&
        !redirect.startsWith('//')
          ? redirect
          : '/'
    await router.replace(target)
  } catch {
    notificationService.error({
      title: t('auth.loginError'),
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
        {{ t('auth.loginTitle') }}
      </h1>

      <p class="mt-2 text-[#78675c]">
        {{ t('auth.loginDescription') }}
      </p>

      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="mt-7"
          @submit.prevent="submit"
      >
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
            autocomplete="current-password"
            :show-password="true"
            :disabled="isSubmitting"
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
        {{ t('auth.noAccount') }}

        <RouterLink
            to="/register"
            class="font-semibold text-[#a66b45] hover:text-[#754c37]"
        >
          {{ t('nav.register') }}
        </RouterLink>
      </p>
    </div>
  </section>
</template>