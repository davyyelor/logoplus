import type { ReactNode } from "react";

interface StateViewProps {
  loading: boolean;
  error: string | null;
  isEmpty?: boolean;
  emptyMessage?: string;
  children: ReactNode;
}

/** Renders loading / error / empty states consistently across pages. */
export function StateView({
  loading,
  error,
  isEmpty = false,
  emptyMessage = "No records to display.",
  children,
}: StateViewProps) {
  if (loading) {
    return <div className="state state--loading">Loading…</div>;
  }
  if (error) {
    return <div className="state state--error">Error: {error}</div>;
  }
  if (isEmpty) {
    return <div className="state state--empty">{emptyMessage}</div>;
  }
  return <>{children}</>;
}
