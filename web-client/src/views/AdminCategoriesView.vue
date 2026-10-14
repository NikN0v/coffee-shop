<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {useCategoryStore} from "@/stores/categoryStore.ts";
import {computed, onMounted, reactive, ref} from "vue";
import {ElMessageBox, type FormInstance, type FormRules} from "element-plus";
import {notificationService} from "@/services/notificationService.ts";
import type {CategoryResponse, CategoryType} from "@/types/category.ts";
import {Delete, Edit, Plus} from "@element-plus/icons-vue";
import UiInput from "@/components/ui/UiInput.vue";
import UiButton from "@/components/ui/UiButton.vue";
import UiSelect from "@/components/ui/UiSelect.vue";

const {t} = useI18n({useScope: 'global'})
const categoryStore = useCategoryStore()
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)

const categoryTypes = computed(() => [
  { label: t('categories.types.drinks'), value: 'DRINKS' },
  { label: t('categories.types.desserts'), value: 'DESSERTS' },
  { label: t('categories.types.bakery'), value: 'BAKERY' },
  { label: t('categories.types.food'), value: 'FOOD' },
  { label: t('categories.types.other'), value: 'OTHER' },
])

const form = reactive<{
  nameRu: string
  nameEn: string
  type: CategoryType
}>({
  nameRu: '',
  nameEn: '',
  type: 'OTHER'
})

const rules: FormRules = {
  nameRu: [{required: true, message: 'Введите название на русском', trigger: 'blur'}, {
    max: 100,
    message: 'Максимум 100 символов',
    trigger: 'blur'
  },],
  nameEn: [{required: true, message: 'Введите название на английском', trigger: 'blur'}, {
    max: 100,
    message: 'Maximum 100 characters',
    trigger: 'blur'
  },],
  type: [
    {
      required: true,
      message: t('categories.selectType'),
      trigger: 'change'
    }
  ]
}

async function loadCategories() {
  try {
    await categoryStore.fetchCategories()
  } catch {
    notificationService.error({
      title: t('categories.loadErrorTitle'),
      message: t('categories.loadErrorMessage')
    })
  }
}

function openCreateDialog() {
  editingId.value = null
  form.nameEn = ''
  form.nameRu = ''
  form.type = 'OTHER'
  dialogVisible.value = true
}

function openEditDialog(category: CategoryResponse) {
  editingId.value = category.id
  form.nameRu = category.nameRu
  form.nameEn = category.nameEn
  form.type = category.type
  dialogVisible.value = true
}

async function saveCategory() {
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const request = {
    nameRu: form.nameRu.trim(),
    nameEn: form.nameEn.trim(),
    type: form.type
  }

  try {
    if (editingId.value === null) {
      await categoryStore.createCategory(request)

      notificationService.success({
        title: t('categories.createdTitle'),
        message: t('categories.createdMessage')
      })
    } else {
      await categoryStore.updateCategory(editingId.value, request)

      notificationService.success({
        title: t('categories.updatedTitle'),
        message: t('categories.updatedMessage')
      })
    }

    dialogVisible.value = false
  } catch {
    notificationService.error({
      title: t('categories.saveErrorTitle'),
      message: t('categories.saveErrorMessage')
    })
  }
}

async function removeCategory(category: CategoryResponse) {
  try {
    await ElMessageBox.confirm(t('categories.deleteConfirmation', {name: category.name}),
        t('categories.deleteTitle'),
        {
          confirmButtonText: t('categories.delete'),
          cancelButtonText: t('categories.cancel'),
          type: 'warning',
          confirmButtonClass: 'coffee-danger-button'
        }
    )

    await categoryStore.deleteCategory(category.id)

    notificationService.success({
      title: t('categories.deletedTitle'),
      message: t('categories.deletedMessage')
    })
  } catch (error) {
    if (error === 'cancel' || error === 'close') return
    notificationService.error({
      title: t('categories.deleteErrorTitle'),
      message: t('categories.deleteErrorMessage')
    })
  }
}

onMounted(loadCategories)
</script>

<template>
  <section class="mx-auto w-full max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
    <div class="mb-6 flex flex-wrap items-center justify-between gap-4">
      <div><h1 class="text-2xl font-bold text-[#35251f] sm:text-3xl"> {{ t('categories.adminTitle') }} </h1>
        <p class="mt-2 text-sm text-[#78675c]"> {{ t('categories.adminDescription') }} </p></div>
      <UiButton @click="openCreateDialog">
        <el-icon class="mr-2">
          <Plus/>
        </el-icon>
        {{ t('categories.add') }}
      </UiButton>
    </div>
    <el-card shadow="never" class="category-admin-card">
      <el-table v-loading="categoryStore.isLoading" :data="categoryStore.categories" row-key="id" class="coffee-table"
                empty-text="Нет категорий">
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="nameRu" :label="t('categories.nameRu')" min-width="180"/>
        <el-table-column prop="nameEn" :label="t('categories.nameEn')" min-width="180"/>
        <el-table-column :label="t('categories.type')" minWidth="150">
          <template #default="{row}">
            <el-tag effect="plain" round class="category-type-tag">
              {{ t(`categories.types.${row.type.toLowerCase()}`) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('categories.actions')" width="150" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <UiButton type="default" circle plain aria-label="Редактировать категорию" :disabled="categoryStore.isSaving"
                        @click="openEditDialog(row)">
                <el-icon>
                  <Edit/>
                </el-icon>
              </UiButton>
              <UiButton circle plain type="danger" aria-label="Удалить категорию" :disabled="categoryStore.isSaving"
                        @click="removeCategory(row)">
                <el-icon>
                  <Delete/>
                </el-icon>
              </UiButton>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible"
               :title="editingId === null ? t('categories.createTitle') : t('categories.editTitle')"
               width="min(480px, 94vw)" align-center destroy-on-close class="coffee-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="saveCategory">
        <el-form-item prop="nameRu" :label="t('categories.nameRu')">
          <UiInput v-model="form.nameRu" :maxlength="100" :disabled="categoryStore.isSaving"
                   placeholder="Например, Кофе"/>
        </el-form-item>
        <el-form-item prop="nameEn" :label="t('categories.nameEn')">
          <UiInput v-model="form.nameEn" :maxlength="100" :disabled="categoryStore.isSaving"
                   placeholder="For example, Coffee"/>
        </el-form-item>
        <el-form-item prop="type" :label="t('categories.type')" required>
          <UiSelect v-model="form.type" :options="categoryTypes" :placeholder="t('categories.selectType')"
                    :disabled="categoryStore.isSaving"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-2">
          <UiButton type="default" :disabled="categoryStore.isSaving" @click="dialogVisible = false">
            {{ t('categories.cancel') }}
          </UiButton>
          <UiButton :loading="categoryStore.isSaving" :disabled="categoryStore.isSaving" @click="saveCategory">
            {{ t('categories.save') }}
          </UiButton>
        </div>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.category-admin-card {
  border-color: #e9dfd3;
  border-radius: 16px;
  background: #fffdf9;
}

.category-admin-card :deep(.el-card__body) {
  padding: 12px;
}

.category-admin-card :deep(.el-table) {
  --el-table-header-bg-color: #f5eee6;
  --el-table-row-hover-bg-color: #fff8f0;
  --el-table-border-color: #eee3d8;
  --el-table-text-color: #59483d;
  --el-table-header-text-color: #593a2b;
  background: transparent;
}

.category-type-tag {
  --el-tag-bg-color: #f5eee6;
  --el-tag-border-color: #e5d8ca;
  --el-tag-text-color: #593a2b;
  font-weight: 500;
}

@media (min-width: 640px) {
  .category-admin-card :deep(.el-card__body) {
    padding: 20px;
  }
}
</style>