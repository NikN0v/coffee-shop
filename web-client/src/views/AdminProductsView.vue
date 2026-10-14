<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {useProductStore} from "@/stores/productStore.ts";
import {useCategoryStore} from "@/stores/categoryStore.ts";
import {computed, onMounted, reactive, ref} from "vue";
import {ElMessageBox, type FormInstance, type FormRules} from "element-plus";
import type {ProductCreateRequest, ProductResponse} from "@/types/product.ts";
import {productApi} from "@/api/productApi.ts";
import {notificationService} from "@/services/notificationService.ts";
import UiButton from "@/components/ui/UiButton.vue";
import {Delete, Edit, Plus} from "@element-plus/icons-vue";
import UiInput from "@/components/ui/UiInput.vue";
import UiTextarea from "@/components/ui/UiTextarea.vue";
import UiNumber from "@/components/ui/UiNumber.vue";
import UiSwitch from "@/components/ui/UiSwitch.vue";
import UiCheckbox from "@/components/ui/UiCheckbox.vue";

const { t, locale } = useI18n()

const productStore = useProductStore()
const categoryStore = useCategoryStore()

const formRef = ref<FormInstance>()
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)

const form = reactive({
  categoryId: undefined as number | undefined,
  nameRu: '', nameEn: '',
  descriptionRu: '', descriptionEn: '',
  price: 0, available: true, weight: null as number | null,
  isDrink: false, volume: null as number | null
})

const rules: FormRules = {
  categoryId: [
    {
      required: true,
      message: t('products.validation.category'),
      trigger: 'change',
    },
  ],
  nameRu: [
    {
      required: true,
      message: t('products.validation.nameRu'),
      trigger: 'blur',
    },
    {
      max: 150,
      message: t('products.validation.nameLength'),
      trigger: 'blur',
    },
  ],
  nameEn: [
    {
      required: true,
      message: t('products.validation.nameEn'),
      trigger: 'blur',
    },
    {
      max: 150,
      message: t('products.validation.nameLength'),
      trigger: 'blur',
    },
  ],
  price: [
    {
      required: true,
      type: 'number',
      min: 0.01,
      message: t('products.validation.price'),
      trigger: 'change',
    },
  ],
}

const categoryOptions = computed(() => categoryStore.categories)

const availabilityOptions = computed(() => [
  { label: t('products.filters.all'), value: undefined },
  { label: t('products.available'), value: true },
  { label: t('products.unavailable'), value: false }
])

function getCategoryName(id: number): string {
  return categoryOptions.value.find(category => category.id === id)?.name ?? '-'
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat(locale.value, {
    style: 'currency',
    currency: 'RUB'
  }).format(price)
}

function resetForm(): void {
  Object.assign(form, {
    categoryId: undefined,
    nameRu: '',
    nameEn: '',
    descriptionRu: '',
    descriptionEn: '',
    price: 0,
    available: true,
    weight: null,
    isDrink: false,
    volume: null
  })

  formRef.value?.clearValidate()
}

function openCreateDialog(): void {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

async function openEditDialog(product: ProductResponse): Promise<void> {
  try {
    const { data } = await productApi.getById(product.id)

    editingId.value = data.id

    Object.assign(form, {
      categoryId: data.categoryId,
      nameRu: data.nameRu,
      nameEn: data.nameEn,
      descriptionRu: data.descriptionRu ?? '',
      descriptionEn: data.descriptionEn ?? '',
      price: data.price,
      available: data.available,
      weight: data.weight,
      isDrink: data.isDrink,
      volume: data.volume
    })

    dialogVisible.value = true
  } catch {
    notificationService.error({
      title: t('common.error'),
      message: t('products.errors.loadOne')
    })
  }
}

function saveProduct(): void {
  if (!formRef.value) return

  formRef.value.validate(async (valid) => {
    if (!valid) return

    if (form.isDrink && (form.volume === null || form.volume < 1)) {
      notificationService.warning({
        title: t('common.warning'),
        message: t('products.validation.volume'),
      })
      return
    }

    const request: ProductCreateRequest = {
      categoryId: form.categoryId!,
      nameRu: form.nameRu.trim(),
      nameEn: form.nameEn.trim(),
      descriptionRu: form.descriptionRu.trim() || null,
      descriptionEn: form.descriptionEn.trim() || null,
      price: form.price,
      available: form.available,
      weight: form.weight,
      isDrink: form.isDrink,
      volume: form.isDrink ? form.volume : null,
    }

    try {
      if (editingId.value !== null) {
        await productStore.updateProduct(editingId.value, request)
      } else {
        await productStore.createProduct(request)
      }

      notificationService.success({
        title: t('common.success'),
        message: editingId.value !== null
            ? t('products.messages.updated')
            : t('products.messages.created'),
      })

      dialogVisible.value = false
      resetForm()
      await productStore.loadProducts()
    } catch {
      notificationService.error({
        title: t('common.error'),
        message: editingId.value !== null
            ? t('products.errors.update')
            : t('products.errors.create'),
      })
    }
  })
}

async function removeProduct(product: ProductResponse): Promise<void> {
  try {
    await ElMessageBox.confirm(
        t('products.confirmDelete', { name: product.name }),
        t('products.deleteTitle'),
        {
          confirmButtonText: t('common.delete'),
          cancelButtonText: t('common.cancel'),
          type: 'warning',
          confirmButtonClass: 'coffee-confirm-button',
        },
    )
  } catch {
    // Пользователь отменил удаление.
    return
  }

  try {
    await productStore.deleteProduct(product.id)

    notificationService.success({
      title: t('common.success'),
      message: t('products.messages.deleted'),
    })
  } catch {
    notificationService.error({
      title: t('common.error'),
      message: t('products.errors.delete'),
    })
  }
}

async function handleCategoryChange(value: number | undefined) {
  productStore.setCategoryFilter(value)
  await refreshProducts()
}

async function handleAvailabilityChange(value: boolean | undefined) {
  productStore.setAvailabilityFilter(value)
  await refreshProducts()
}

async function handlePageChange(value: number) {
  productStore.setPage(value - 1)
  await refreshProducts()
}

async function handlePageSizeChange(value: number) {
  productStore.setPageSize(value)
  await refreshProducts()
}

async function refreshProducts(): Promise<void> {
  try {
    await productStore.loadProducts()
  } catch {
    notificationService.error({
      title: t('common.error'),
      message: t('products.errors.load'),
    })
  }
}

onMounted(async () => {
  try {
    await Promise.all([
      categoryStore.fetchCategories(),
      productStore.loadProducts(),
    ])
  } catch {
    notificationService.error({
      title: t('common.error'),
      message: t('products.errors.load'),
    })
  }
})
</script>

<template>
  <section class="products-page">
    <header class="products-header">
      <div>
        <h1 class="products-title">
          {{ t('products.title') }}
        </h1>

        <p class="products-subtitle">
          {{ t('products.subtitle') }}
        </p>
      </div>

      <UiButton
          type="primary"
          :loading="productStore.isSaving"
          @click="openCreateDialog"
      >
        <el-icon class="mr-2">
          <Plus />
        </el-icon>
        {{ t('products.add') }}
      </UiButton>
    </header>

    <div class="products-filters">
      <el-select
          :model-value="productStore.categoryId"
          clearable
          :placeholder="t('products.filters.category')"
          class="coffee-select"
          @update:model-value="handleCategoryChange"
      >
        <el-option
            v-for="category in categoryOptions"
            :key="category.id"
            :label="category.name"
            :value="category.id"
        />
      </el-select>

      <el-select
          :model-value="productStore.available"
          :placeholder="t('products.filters.availability')"
          class="coffee-select"
          @update:model-value="handleAvailabilityChange"
      >
        <el-option
            v-for="option in availabilityOptions"
            :key="String(option.value)"
            :label="option.label"
            :value="option.value"
        />
      </el-select>
    </div>

    <div v-loading="productStore.isLoading" class="products-table-wrapper">
      <el-table
          :data="productStore.products"
          row-key="id"
          class="coffee-table"
          empty-text="—"
      >
        <el-table-column
            prop="name"
            :label="t('products.fields.name')"
            min-width="190"
        >
          <template #default="{ row }">
            <div class="product-name-cell">
              <span class="product-name">{{ row.name }}</span>
              <span class="product-id">#{{ row.id }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            :label="t('products.fields.category')"
            min-width="150"
        >
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>

        <el-table-column
            :label="t('products.fields.price')"
            width="130"
            align="right"
        >
          <template #default="{ row }">
            <span class="product-price">
              {{ formatPrice(row.price) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column
            :label="t('products.fields.availability')"
            width="150"
        >
          <template #default="{ row }">
            <el-tag
                :type="row.available ? 'success' : 'info'"
                effect="plain"
                round
                class="availability-tag"
            >
              {{
                row.available
                    ? t('products.available')
                    : t('products.unavailable')
              }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
            :label="t('products.fields.details')"
            min-width="130"
        >
          <template #default="{ row }">
            <span v-if="row.isDrink && row.volume">
              {{ row.volume }} {{ t('products.units.ml') }}
            </span>
            <span v-else-if="row.weight">
              {{ row.weight }} {{ t('products.units.g') }}
            </span>
            <span v-else>—</span>
          </template>
        </el-table-column>

        <el-table-column
            :label="t('products.fields.actions')"
            width="125"
            fixed="right"
            align="center"
        >
          <template #default="{ row }">
            <div class="product-actions">
              <UiButton
                  type="default"
                  circle
                  plain
                  :aria-label="t('products.edit')"
                  :title="t('products.edit')"
                  @click="openEditDialog(row)">
                <el-icon><Edit /></el-icon>
              </UiButton>

              <UiButton
                  circle
                  plain
                  type="danger"
                  :aria-label="t('products.delete')"
                  :title="t('products.delete')"
                  @click="removeProduct(row)"
              >
                <el-icon><Delete /></el-icon>
              </UiButton>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="products-pagination">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next"
            :current-page="productStore.page + 1"
            :page-size="productStore.size"
            :page-sizes="[10, 20, 50]"
            :total="productStore.totalElements"
            @current-change="handlePageChange"
            @size-change="handlePageSizeChange"
        />
      </div>
    </div>

    <el-dialog
        v-model="dialogVisible"
        :title="editingId !== null
        ? t('products.editTitle')
        : t('products.createTitle')"
        width="620px"
        class="coffee-dialog"
        destroy-on-close
        @closed="resetForm"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="product-form"@submit.prevent
      >
        <el-form-item
            prop="categoryId"
            :label="t('products.fields.category')"
        >
          <el-select
              v-model="form.categoryId"
              :placeholder="t('products.form.selectCategory')"
              class="coffee-select full-width"
              :disabled="productStore.isSaving"
          >
            <el-option
                v-for="category in categoryOptions"
                :key="category.id"
                :label="category.name"
                :value="category.id"
            />
          </el-select>
        </el-form-item>

        <div class="product-form-grid">
          <el-form-item
              prop="nameRu"
              :label="t('products.fields.nameRu')"
          >
            <UiInput v-model="form.nameRu" :maxlength="150" show-word-limit :disabled="productStore.isSaving"/>
          </el-form-item>

          <el-form-item
              prop="nameEn"
              :label="t('products.fields.nameEn')"
          >
            <UiInput v-model="form.nameEn" :maxlength="150" show-word-limit :disabled="productStore.isSaving"/>
          </el-form-item>
        </div>

        <div class="product-form-grid">
          <el-form-item :label="t('products.fields.descriptionRu')">
            <UiTextarea v-model="form.descriptionRu" :rows="3" :maxlength="2000" showWordLimit
                        resize="vertical" :disabled="productStore.isSaving"/>
          </el-form-item>

          <el-form-item :label="t('products.fields.descriptionEn')">
            <UiTextarea v-model="form.descriptionEn" :rows="3" :maxlength="2000" showWordLimit
                        resize="vertical" :disabled="productStore.isSaving"/>
          </el-form-item>
        </div>

        <div class="product-form-grid">
          <el-form-item
              prop="price"
              :label="t('products.fields.price')"
          >
            <UiNumber v-model="form.price" :min="0.01" :precision="2" :step="0.5" :controls="false"
                      :disabled="productStore.isSaving"/>
          </el-form-item>

          <el-form-item :label="t('products.fields.available')">
            <UiSwitch v-model="form.available" :active-text="t('products.available')"
                      :inactive-text="t('products.unavailable')" :disabled="productStore.isSaving"/>
          </el-form-item>
        </div>

        <el-form-item>
          <UiCheckbox v-model="form.isDrink" :disabled="productStore.isSaving">
            {{ t('products.fields.isDrink') }}
          </UiCheckbox>
        </el-form-item>

        <div class="product-form-grid">
          <el-form-item v-if="!form.isDrink" :label="t('products.fields.weight')">
            <UiNumber v-model="form.weight"
                      :min="1"
                      :precision="0"
                      :step="10"
                      :placeholder="t('products.form.optional')"
                      :disabled="productStore.isSaving"
                      class="full-width"/>
          </el-form-item>

          <el-form-item
              v-else
              :label="t('products.fields.volume')"
          >
            <UiNumber v-model="form.volume"
                      :min="1"
                      :precision="0"
                      :step="50"
                      :placeholder="t('products.form.volume')"
                      :disabled="productStore.isSaving"
                      class="full-width"/>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <div class="product-dialog-footer">
          <UiButton type="default" plain :disabled="productStore.isSaving" @click="dialogVisible = false">
            {{ t('common.cancel') }}
          </UiButton>

          <UiButton type="primary" :loading="productStore.isSaving" @click="saveProduct">
            {{ t('common.save') }}
          </UiButton>
        </div>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.products-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 28px;
  color: #593a2b;
}

.products-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.products-title {
  margin: 0;
  color: #422719;
  font-size: 28px;
  font-weight: 700;
}

.products-subtitle {
  margin: 8px 0 0;
  color: #8a7565;
  font-size: 14px;
}

.products-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.coffee-select {
  width: 240px;
}

.full-width {
  width: 100%;
}

.products-table-wrapper {
  min-width: 0;
  padding: 16px;
  border: 1px solid #eadfd2;
  border-radius: 16px;
  background: #fffaf4;
  box-shadow: 0 5px 20px rgb(66 39 25 / 4%);
}

.product-name-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-name {
  color: #422719;
  font-weight: 600;
}

.product-id {
  color: #a18b79;
  font-size: 12px;
}

.product-price {
  color: #593a2b;
  font-weight: 600;
  white-space: nowrap;
}

.availability-tag {
  font-weight: 500;
}

.product-actions {
  display: flex;
  justify-content: center;
  gap: 6px;
}

.products-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.product-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.product-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .products-page {
    gap: 18px;
    padding: 16px;
  }

  .products-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .products-title {
    font-size: 24px;
  }

  .products-filters {
    flex-direction: column;
  }

  .coffee-select {
    width: 100%;
  }

  .products-table-wrapper {
    padding: 8px;
  }

  .products-pagination {
    justify-content: flex-start;
    overflow-x: auto;
  }

  .product-form-grid {
    grid-template-columns: 1fr;
    gap: 0;
  }
}
</style>