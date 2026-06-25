import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import { authService } from "../services/authService";
import { clearToken, getToken, setToken } from "../services/tokenStore";
import type { CurrentUser, LoginRequest, Role } from "../types/api";

interface AuthContextValue {
  user: CurrentUser | null;
  loading: boolean;
  login: (request: LoginRequest) => Promise<CurrentUser>;
  logout: () => void;
  hasRole: (...roles: Role[]) => boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [loading, setLoading] = useState(true);

  // On mount, if a token is stored, resolve the current user. An invalid or
  // expired token simply clears the session.
  useEffect(() => {
    if (!getToken()) {
      setLoading(false);
      return;
    }
    authService
      .me()
      .then((current) => setUser(current))
      .catch(() => {
        clearToken();
        setUser(null);
      })
      .finally(() => setLoading(false));
  }, []);

  const login = useCallback(async (request: LoginRequest) => {
    const response = await authService.login(request);
    setToken(response.token);
    const current: CurrentUser = {
      userId: response.userId,
      clinicId: response.clinicId,
      email: response.email,
      firstName: response.firstName,
      lastName: response.lastName,
      role: response.role,
    };
    setUser(current);
    return current;
  }, []);

  const logout = useCallback(() => {
    clearToken();
    setUser(null);
  }, []);

  const hasRole = useCallback(
    (...roles: Role[]) => (user ? roles.includes(user.role) : false),
    [user],
  );

  const value = useMemo<AuthContextValue>(
    () => ({ user, loading, login, logout, hasRole }),
    [user, loading, login, logout, hasRole],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
