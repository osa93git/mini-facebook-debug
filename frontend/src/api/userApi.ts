import api from "./axios"
import type {UserPublic} from "../types/UserPublic.ts";

export const getCurrentUser = async (): Promise<UserPublic> => {
    const response = await api.get<UserPublic>("/users/me")
    return response.data;
}