import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true,
})

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("accessToken");

    if(!config.headers){
        config.headers = {};
    }

    if(token){
        config.headers.Authorization = `Bearer ${token}`
    }

    return config;
});

export default api;