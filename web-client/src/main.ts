import { createApp } from 'vue'
import { createPinia } from 'pinia'

import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import App from './App.vue'
import router from './router'
import './style.css'
import { i18n } from "@/i18n";

const app = createApp(App)

app.use(createPinia())
app.use(ElementPlus)
app.use(router)
app.use(i18n)

app.mount('#app')
