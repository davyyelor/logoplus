import type { ReactNode } from "react";
import { NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import type { Role } from "../types/api";

interface NavItem {
  to: string;
  label: string;
  end?: boolean;
  roles?: Role[];
}

const STAFF_NAV: NavItem[] = [
  { to: "/", label: "Panel", end: true },
  { to: "/patients", label: "Pacientes" },
  { to: "/appointments", label: "Agenda" },
  { to: "/sessions", label: "Sesiones", roles: ["CLINIC_ADMIN", "THERAPIST"] },
  { to: "/report-templates", label: "Plantillas de informe", roles: ["CLINIC_ADMIN", "THERAPIST"] },
  { to: "/consent-templates", label: "Plantillas de consentimiento", roles: ["CLINIC_ADMIN", "THERAPIST"] },
  { to: "/billing", label: "Facturación" },
  { to: "/payments", label: "Pagos" },
  { to: "/fees", label: "Tarifas" },
  { to: "/session-billing", label: "Cobro de sesiones" },
  { to: "/users", label: "Usuarios", roles: ["CLINIC_ADMIN"] },
  { to: "/clinic", label: "Clínica", roles: ["CLINIC_ADMIN"] },
];

const FAMILY_NAV: NavItem[] = [{ to: "/portal", label: "Portal familiar", end: true }];

export function Layout({ children }: Readonly<{ children: ReactNode }>) {
  const { user, logout, hasRole } = useAuth();
  const isFamily = user?.role === "FAMILY";
  const items = (isFamily ? FAMILY_NAV : STAFF_NAV).filter(
    (item) => !item.roles || hasRole(...item.roles),
  );

  return (
    <div className="app">
      <aside className="sidebar">
        <h1 className="sidebar__brand">LogoPlus</h1>
        <nav className="sidebar__nav">
          {items.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) =>
                `sidebar__link${isActive ? " sidebar__link--active" : ""}`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        {user && (
          <div className="sidebar__footer">
            <p className="sidebar__user">
              {user.firstName} {user.lastName}
              <span className="sidebar__role">{user.role}</span>
            </p>
            <button type="button" className="button button--ghost" onClick={logout}>
              Cerrar sesión
            </button>
          </div>
        )}
      </aside>
      <main className="content">{children}</main>
    </div>
  );
}
