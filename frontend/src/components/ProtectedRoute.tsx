import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import type { Role } from "../types/api";

interface ProtectedRouteProps {
  children: ReactNode;
  /** When set, only these roles may view the route; others are redirected. */
  roles?: Role[];
}

/**
 * Guards a route. Unauthenticated users go to /login. Authenticated users
 * lacking the required role are sent to their home (family portal for FAMILY,
 * dashboard otherwise).
 */
export function ProtectedRoute({ children, roles }: Readonly<ProtectedRouteProps>) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="state state--loading">Cargando…</div>;
  }
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (roles && !roles.includes(user.role)) {
    return <Navigate to={user.role === "FAMILY" ? "/portal" : "/"} replace />;
  }
  return <>{children}</>;
}
