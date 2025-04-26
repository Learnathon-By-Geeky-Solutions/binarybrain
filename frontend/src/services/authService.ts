import api from './api';
import { AuthRequest, AuthResponse, UserDto, RefreshTokenRequest, User } from '../interfaces/auth';
import { storeTokens } from '../utils/auth';

export const authService = {
    login: async (credentials: AuthRequest): Promise<void> => {
        const response = await api.post<AuthResponse>('/api/user/login', credentials);
        storeTokens(response.data.jwt, response.data.refreshToken);
    },

    register: async (userData: UserDto): Promise<User> => {
        const response = await api.post<User>('/api/user/register', userData);
        return response.data;
    },

    refreshToken: async (refreshToken: string): Promise<void> => {
        const response = await api.post<AuthResponse>('/api/user/refresh', { refreshToken } as RefreshTokenRequest);
        storeTokens(response.data.jwt, response.data.refreshToken);
    },

    getCurrentUser: async (): Promise<User> => {
        const response = await api.get<User>('/api/user/profile');
        return response.data;
    },

    getUserById: async (id: number): Promise<User> => {
        const response = await api.get<User>(`/api/user/profile/${id}`);
        return response.data;
    },

    uploadPhoto: async (userId: number, file: File): Promise<string> => {
        const formData = new FormData();
        formData.append('id', userId.toString());
        formData.append('file', file);
        const response = await api.post<string>('/api/user/photo', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        return response.data;
    },
};