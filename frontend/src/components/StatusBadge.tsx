interface StatusBadgeProps {
  status: string;
}

/** Color-coded badge for payment / session statuses. */
export function StatusBadge({ status }: StatusBadgeProps) {
  const tone = toneFor(status);
  return <span className={`badge badge--${tone}`}>{status.replace(/_/g, " ")}</span>;
}

function toneFor(status: string): string {
  switch (status) {
    case "PAID":
    case "REGISTERED":
      return "success";
    case "PENDING":
    case "PARTIALLY_PAID":
      return "warning";
    case "REFUNDED":
      return "info";
    case "CANCELLED":
      return "danger";
    case "NO_CHARGE":
    default:
      return "neutral";
  }
}
