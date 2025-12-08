import { defineConfig } from "vite"
import react from "@vitejs/plugin-react"
import tailwindcss from "@tailwindcss/vite"

export default defineConfig({
  build: {
    rollupOptions: {
      input: {
        admin: "admin.html",

        // other pages
        home: "pages/home.html",
        notFound: "pages/404.html",
        password: "pages/password.html",
      },
    },
  },
  plugins: [react(), tailwindcss()],
})
