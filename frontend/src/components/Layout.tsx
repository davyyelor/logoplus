import type { ReactNode } from "react";
import { NavLink } from "react-router-dom";

const NAV_ITEMS = [
  { to: "/", label: "Dashboard", end: true },
  { to: "/payments", label: "Payments", end: false },
  { to: "/fees", label: "Fees", end: false },
  { to: "/session-billing", label: "Session billing", end: false },
];

export function Layout({ children }: { children: ReactNode }) {
  return (
    <div className="app">
      <aside className="sidebar">
        <h1 className="sidebar__brand">Clinic Billing</h1>
        <nav className="sidebar__nav">
          {NAV_ITEMS.map((item) => (
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
        <p className="sidebar__hint">MVP · internal payment control</p>
      </aside>
      <main className="content">{children}</main>
    </div>
  );
}
