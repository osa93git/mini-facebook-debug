import axios, {AxiosHeaders} from 'axios';
import type {AxiosRequestConfig, AxiosResponse} from 'axios';

interface FailedRequest {
    resolve: (token: string) => void;
    reject: (error: unknown) => void;
}

interface RefreshResponse {
    accessToken: string;
}

let isRefreshing = false;
let failedQueue: FailedRequest[] = [];


const processQueue = (error: unknown, token: string | null = null): void => {
    failedQueue.forEach(({resolve, reject}) => {
        if (error || !token) {
            reject(error);
        } else {
            resolve(token!);
        }
    });

    failedQueue = [];
}

const api = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true,
})

api.interceptors.request.use((config) => {

    const url = (config.url ?? '').toString()

    if (url.includes('/auth/')) {
        return config;
    }

    const token = localStorage.getItem("accessToken");

    if (!config.headers) {
        config.headers = new AxiosHeaders();
    }
    if (token && config.headers) {
        (config.headers as AxiosHeaders).set("Authorization", `Bearer ${token}`);
    }

    return config;
});

// for 401. access token expired
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };

        if ((error.response?.status === 401 || error.response?.status === 403) && !originalRequest._retry) {
            if (isRefreshing) {
                return new Promise(function (resolve, reject) {
                    failedQueue.push({resolve, reject});
                })
                    .then((token) => {
                        originalRequest.headers = originalRequest.headers || new AxiosHeaders();
                        (originalRequest.headers as AxiosHeaders).set("Authorization", 'Bearer ' + token);
                        return api(originalRequest);
                    })
                    .catch((error) => Promise.reject(error));
            }

            originalRequest._retry = true;
            isRefreshing = true;

            try {
                console.log("[REFRESH] Access token expired → requesting new one...");
                const res: AxiosResponse<RefreshResponse> = await api.post('api/auth/refresh',
                    {},
                    {withCredentials: true}
                );

                const newAccessToken = res.data.accessToken;

                if (!newAccessToken) {
                    throw new Error('No access token after refresh');
                }

                localStorage.setItem("accessToken", newAccessToken);
                api.defaults.headers.common['Authorization'] = 'Bearer ' + newAccessToken;

                processQueue(null, newAccessToken);
                return api(originalRequest);
            } catch (error) {
                processQueue(error, null);
                localStorage.removeItem('accessToken');
                window.location.href = '/login';

                return Promise.reject(error);
            } finally {
                isRefreshing = false;
            }
        }
        return Promise.reject(error);
    }
);

export default api;