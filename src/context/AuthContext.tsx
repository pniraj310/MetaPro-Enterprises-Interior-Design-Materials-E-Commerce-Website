import React, { createContext, useContext, useState, useEffect } from 'react';

const AUTH_TOKEN_KEY = 'metapro_admin_token';
const AUTH_USER_KEY = 'metapro_admin_user';

interface AuthContextType {
  isAuthenticated: boolean;
  adminUser: string | null;
  token: string | null;
  login: (username: string, password: string) => Promise<{ success: boolean; error?: string }>;
  loginWithToken: (username: string, token: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function getStoredAdminToken(): string | null {
  try {
    return localStorage.getItem(AUTH_TOKEN_KEY);
  } catch {
    return null;
  }
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(() => {
    try {
      return localStorage.getItem(AUTH_TOKEN_KEY);
    } catch {
      return null;
    }
  });

  const [adminUser, setAdminUser] = useState<string | null>(() => {
    try {
      return localStorage.getItem(AUTH_USER_KEY);
    } catch {
      return null;
    }
  });

  const isAuthenticated = Boolean(token);

  useEffect(() => {
    if (!token) return;
    fetch('/api/auth/verify', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) {
          logout();
        }
      })
      .catch(() => {
        // Keep session if offline/transient
      });
  }, [token]);

  const login = async (
    username: string,
    password: string
  ): Promise<{ success: boolean; error?: string }> => {
    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });
      const data = await res.json();
      if (!res.ok || !data.token) {
        return {
          success: false,
          error: data.error || 'Invalid username/email or password.',
        };
      }
      localStorage.setItem(AUTH_TOKEN_KEY, data.token);
      localStorage.setItem(AUTH_USER_KEY, data.username || username);
      setToken(data.token);
      setAdminUser(data.username || username);
      return { success: true };
    } catch (error: any) {
      return {
        success: false,
        error: error.message || 'Unable to connect to authentication server.',
      };
    }
  };

  const loginWithToken = (username: string, newToken: string) => {
    try {
      localStorage.setItem(AUTH_TOKEN_KEY, newToken);
      localStorage.setItem(AUTH_USER_KEY, username);
    } catch {
      // ignore
    }
    setToken(newToken);
    setAdminUser(username);
  };

  const logout = () => {
    try {
      localStorage.removeItem(AUTH_TOKEN_KEY);
      localStorage.removeItem(AUTH_USER_KEY);
    } catch {
      // ignore
    }
    setToken(null);
    setAdminUser(null);
  };

  return (
    <AuthContext.Provider
      value={{ isAuthenticated, adminUser, token, login, loginWithToken, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
