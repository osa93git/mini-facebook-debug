# 🧩 Mini-Facebook – Frontend

This is the frontend of the Mini-Facebook recruitment project, built with **React + Vite**.  
It includes a login panel, integration with a JWT-based backend (access token + refresh cookie), and a modular project structure.

---

## ⚙️ Tech stack

- React (Vite)
- TypeScript
- React Router DOM
- Axios
- LocalStorage + HttpOnly Cookies
- Minimal CSS

---

## 📁 Project structure

```plaintext
src/
├── api/           # axios config and HTTP logic
├── features/      # login form and other domain features
├── pages/         # login and other route-level views
├── router/        # AppRouter with routing config
├── layouts/       # MainLayout (Navbar + Outlet)
├── assets/        # styles, images
├── App.tsx
├── main.tsx
```

---

## 🚀 Running the frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:  
👉 http://localhost:5173

---

## 🔐 Authentication

- `accessToken` is stored in `localStorage`
- `refreshToken` is received as an **HttpOnly cookie** from the backend (`http://localhost:8080`)

---

## 📌 Requirements

To function correctly, the backend must be running and accessible at `http://localhost:8080`.
