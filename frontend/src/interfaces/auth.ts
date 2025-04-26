export interface User {
    id: number;
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    roles: string[];
    profilePicture?: string;
    createdAt: string;
    updatedAt: string;
}

export interface LoginDto {
    username: string;
    password: string;
}

export interface RegisterDto {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    password: string;
    roles: string[];
}

export interface UpdateProfileDto {
    firstName: string;
    lastName: string;
    email: string;
    currentPassword?: string;
    newPassword?: string;
    profilePicture?: File;
}

export interface AuthState {
    user: User | null;
    token: string | null;
    loading: boolean;
    error: string | null;
}