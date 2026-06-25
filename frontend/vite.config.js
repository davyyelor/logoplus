import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
// The frontend talks to the backend through the relative "/api" prefix.
// In development Vite proxies it to the Spring Boot server on port 8080 so the
// browser never needs to know the backend URL.
export default defineConfig({
    plugins: [react()],
    server: {
        port: 5173,
        proxy: {
            "/api": {
                target: "http://localhost:8080",
                changeOrigin: true,
            },
        },
    },
});
