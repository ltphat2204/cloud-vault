import { defineConfig, loadEnv } from 'vite'
import { devtools } from '@tanstack/devtools-vite'

import { tanstackStart } from '@tanstack/react-start/plugin/vite'

import viteReact, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const apiBaseUrl = env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'
  const targetOrigin = new URL(apiBaseUrl).origin // e.g., http://localhost:8080

  return {
    resolve: { tsconfigPaths: true },
    server: {
      proxy: {
        '/api': {
          target: targetOrigin,
          changeOrigin: true,
        },
      },
    },
    plugins: [
      devtools(),
      tailwindcss(),
      tanstackStart(),
      viteReact(),
      babel({ presets: [reactCompilerPreset()] }),
    ],
  }
})
