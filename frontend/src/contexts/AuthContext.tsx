import React, { createContext, useContext, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../store';
import { getCurrentUser } from '../store/slices/authSlice';
import { AppDispatch } from '../store';
import { getStoredToken } from '../utils/auth';

interface AuthContextType {
    isAuthenticated: boolean;
    isLoading: boolean;
}

const AuthContext = createContext<AuthContextType>({
    isAuthenticated: false,
    isLoading: true,
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const dispatch = useDispatch<AppDispatch>();
    const { user, loading } = useSelector((state: RootState) => state.auth);
    
    useEffect(() => {
        const token = getStoredToken();
        if (token && !user) {
            dispatch(getCurrentUser());
        }
    }, [dispatch, user]);

    const value = {
        isAuthenticated: !!user,
        isLoading: loading,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);