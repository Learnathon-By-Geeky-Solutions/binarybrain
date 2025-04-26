export interface User {
    id: number;
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    roles: string[];
    profilePicture?: string;
    currentInstitute?: string;
    country?: string;
    createdAt: string;
    updatedAt: string;
}

export interface AuthRequest {
    username: string;
    password: string;
}

export interface AuthResponse {
    jwt: string;
    refreshToken: string;
}

export interface UserDto extends RegisterDto {
    currentInstitute?: string;
    country?: string;
}

export interface RegisterDto {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    password: string;
    roles: string[];
}

export interface RefreshTokenRequest {
    refreshToken: string;
}

export interface UpdateProfileDto {
    firstName: string;
    lastName: string;
    email: string;
    currentPassword?: string;
    newPassword?: string;
    currentInstitute?: string;
    country?: string;
    profilePicture?: File;
}

export interface AuthState {
    user: User | null;
    token: string | null;
    loading: boolean;
    error: string | null;
}