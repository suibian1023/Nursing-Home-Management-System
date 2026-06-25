import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': '/src'
    }
  },
  server: {
    allowedHosts: ['xn--xhq8a53p4rumh0dwpl.cc.cd'],
    port: 3000,
    proxy: {
      '/yyzx': {
        target: 'http://localhost:3030',
        changeOrigin: true
      }
    }
  }
})
