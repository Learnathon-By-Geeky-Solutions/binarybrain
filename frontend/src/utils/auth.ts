const TOKEN_KEY = 'jwt_token';
const REFRESH_TOKEN_KEY = 'refresh_token';

export const storeTokens = (jwtToken: string, refreshToken: string) => {
    localStorage.setItem(TOKEN_KEY, jwtToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
};

export const getStoredToken = () => localStorage.getItem(TOKEN_KEY);

export const getStoredRefreshToken = () => localStorage.getItem(REFRESH_TOKEN_KEY);

export const clearTokens = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
};