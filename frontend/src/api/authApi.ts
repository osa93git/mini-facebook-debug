import api from "./axios"

export interface LoginRequest {
    email: string;
    password: string;
}

export interface LoginResponse {
    accessToken: string;
}

export const login = async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>("api/auth/login", credentials);
    return response.data;
};