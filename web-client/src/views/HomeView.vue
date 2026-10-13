<script setup lang="ts">
import {useI18n} from "vue-i18n";
import CategorySection from "@/components/category/CategorySection.vue";

type Product = {
  id: number
  name: string
  description: string
  price: number
  image: string
  category: string
}

const {t} = useI18n()

const products: Product[] = [
  {
    id: 1,
    name: 'Капучино',
    description: 'Нежная молочная пена и насыщенный эспрессо',
    price: 220,
    image:
        '/images/cappuccino.jpg',
    category: t('products.coffee'),
  },
  {
    id: 2,
    name: 'Латте',
    description: 'Мягкий кофейный вкус с бархатистой текстурой',
    price: 250,
    image:
        '/images/latte.jpg',
    category: t('products.coffee'),
  },
  {
    id: 3,
    name: 'Эспрессо',
    description: 'Классический крепкий кофе с насыщенным ароматом',
    price: 150,
    image:
        '/images/espresso.jpg',
    category: t('products.coffee'),
  },
]

const formatPrice = (price: number): string =>
    `${new Intl.NumberFormat('ru-RU').format(price)} ₽`
</script>

<template>
  <div>
    <section
        class="mx-auto grid max-w-7xl items-center gap-10 px-4 py-12 sm:px-6 sm:py-16 lg:grid-cols-2 lg:gap-16 lg:px-8 lg:py-24">
      <div class="max-w-xl">
        <span class="inline-flex rounded-full bg-[#efe4d7] px-4 py-2 text-sm font-semibold text-[#805337]">
          {{ t('home.badge') }}
        </span>

        <h1 class="mt-6 text-4xl leading-tight font-bold tracking-tight sm:text-5xl lg:text-6xl">
          {{ t('home.titleStart') }}
          <span class="text-[#a66b45]">{{ t('home.titleAccent') }}</span>
          {{ t('home.titleEnd') }}
        </h1>

        <p class="mt-6 max-w-lg text-base leading-7 text-[#78675c] sm:text-lg">
          {{ t('home.description') }}
        </p>

        <div class="mt-8 flex flex-wrap gap-3">
          <a
              href="#menu"
              class="inline-flex min-h-12 items-center justify-center rounded-full bg-[#593a2b] px-7 py-3 font-semibold text-white transition hover:bg-[#754c37]"
          >
            {{ t('home.menuButton') }}
          </a>

          <RouterLink
              to="/register"
              class="inline-flex min-h-12 items-center justify-center rounded-full border border-[#d8c8b8] px-7 py-3 font-semibold transition hover:bg-[#efe5d9]"
          >
            {{ t('home.joinButton') }}
          </RouterLink>
        </div>

        <div class="mt-10 flex flex-wrap gap-x-8 gap-y-4 border-t border-[#e9dfd3] pt-6">
          <div>
            <p class="text-xl font-bold">100%</p>
            <p class="mt-1 text-sm text-[#78675c]">{{ t('home.coffeeLove') }}</p>
          </div>

          <div>
            <p class="text-xl font-bold">Fresh</p>
            <p class="mt-1 text-sm text-[#78675c]">{{ t('home.freshIngr') }}</p>
          </div>

          <div>
            <p class="text-xl font-bold">Daily</p>
            <p class="mt-1 text-sm text-[#78675c]">{{ t('home.forYou') }}</p>
          </div>
        </div>
      </div>

      <div class="relative mx-auto w-full max-w-xl">
        <div class="absolute -inset-3 rounded-[2rem] bg-[#e9d9c7] opacity-60 blur-2xl"/>

        <img
            src="/images/hero-coffee.jpg"
            alt="Свежеприготовленный кофе в уютной кофейне"
            class="relative aspect-[4/3] w-full rounded-[2rem] object-cover shadow-xl sm:aspect-[5/4]"
            fetchpriority="high"
        />

        <div
            class="absolute right-3 bottom-3 left-3 rounded-2xl border border-white/50 bg-white/90 p-4 shadow-lg backdrop-blur sm:right-5 sm:bottom-5 sm:left-auto sm:max-w-xs sm:p-5">
          <p class="text-sm text-[#8b7565]">{{ t('home.calmMoment') }}</p>
          <p class="mt-1 text-lg font-bold">{{ t('home.coffeeInspires') }}</p>
        </div>
      </div>
    </section>

    <CategorySection/>

    <section id="menu" class="bg-white py-14 sm:py-20">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p class="text-sm font-semibold tracking-[0.2em] text-[#a66b45] uppercase">
              {{ t('home.ourMenu') }}
            </p>

            <h2 class="mt-3 text-3xl font-bold tracking-tight sm:text-4xl">
              {{ t('home.popularDrinks') }}
            </h2>

            <p class="mt-3 max-w-xl text-[#78675c]">
              {{ t('home.menuDescription') }}
            </p>
          </div>

          <span class="text-sm text-[#8b7565]">
            {{ t('home.ourChoice') }}
          </span>
        </div>

        <div class="mt-8 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          <article
              v-for="product in products"
              :key="product.id"
              class="group overflow-hidden rounded-2xl border border-[#eee5dc] bg-[#faf7f2] transition duration-300 hover:-translate-y-1 hover:shadow-xl"
          >
            <div class="overflow-hidden">
              <img
                  :src="product.image"
                  :alt="product.name"
                  loading="lazy"
                  class="aspect-[5/4] w-full object-cover transition duration-500 group-hover:scale-105"
              />
            </div>

            <div class="p-5 sm:p-6">
              <span class="text-xs font-semibold tracking-wider text-[#a66b45] uppercase">
                {{ product.category }}
              </span>

              <h3 class="mt-2 text-xl font-bold">
                {{ product.name }}
              </h3>

              <p class="mt-2 min-h-12 text-sm leading-6 text-[#78675c]">
                {{ product.description }}
              </p>

              <div class="mt-5 flex items-center justify-between gap-3">
                <span class="text-xl font-bold">
                  {{ formatPrice(product.price) }}
                </span>

                <RouterLink
                    to="/login"
                    class="rounded-full bg-[#593a2b] px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-[#754c37]"
                    :aria-label="`Перейти к заказу: ${product.name}`"
                >
                  {{ t('home.order') }}
                </RouterLink>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section class="mx-auto max-w-7xl px-4 py-14 sm:px-6 sm:py-20 lg:px-8">
      <div
          class="overflow-hidden rounded-3xl bg-[#593a2b] px-6 py-10 text-white sm:px-10 sm:py-14 lg:flex lg:items-center lg:justify-between lg:gap-8">
        <div class="max-w-2xl">
          <p class="text-sm font-semibold tracking-[0.2em] text-[#e6c5a7] uppercase">
            {{ t('home.joinUs') }}
          </p>

          <h2 class="mt-4 text-3xl leading-tight font-bold sm:text-4xl">
            {{ t('home.joinTitle') }}
          </h2>

          <p class="mt-4 leading-7 text-[#e2d1c4]">
            {{ t('home.joinDescription') }}
          </p>
        </div>

        <RouterLink
            to="/register"
            class="mt-7 inline-flex min-h-12 shrink-0 items-center justify-center rounded-full bg-[#faf7f2] px-7 py-3 font-semibold text-[#593a2b] transition hover:bg-white lg:mt-0"
        >
          {{ t('home.createAccount') }}
        </RouterLink>
      </div>
    </section>
  </div>
</template>