interface StatCardProps {
  label: string;
  value: string;
  accent?: "paid" | "pending" | "refunded" | "neutral";
}

export function StatCard({ label, value, accent = "neutral" }: StatCardProps) {
  return (
    <div className={`stat-card stat-card--${accent}`}>
      <span className="stat-card__label">{label}</span>
      <span className="stat-card__value">{value}</span>
    </div>
  );
}
