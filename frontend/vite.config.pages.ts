import { defineConfig } from "vite"
import react from "@vitejs/plugin-react"
import tailwindcss from "@tailwindcss/vite"
import path from "node:path"

export default defineConfig({
  build: {
    outDir: "dist-pages",
    rollupOptions: {
      input: {
        notFound: "pages/404.html",
        serverError: "pages/500.html",
        password: "pages/password.html",
      },
    },
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  plugins: [react(), tailwindcss()],
})
