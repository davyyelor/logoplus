// Display helpers for currency and dates. All amounts are formatted in the
// record's own currency (defaulting to EUR) using the Spanish locale.

const dateFormatter = new Intl.DateTimeFormat("es-ES", {
  year: "numeric",
  month: "2-digit",
  day: "2-digit",
});

export function formatCurrency(amount: number, currency = "EUR"): string {
  return new Intl.NumberFormat("es-ES", {
    style: "currency",
    currency,
  }).format(amount);
}

/** Formats an ISO date (yyyy-MM-dd) as a readable local date. */
export function formatDate(isoDate: string | null | undefined): string {
  if (!isoDate) {
    return "—";
  }
  const date = new Date(`${isoDate}T00:00:00`);
  if (Number.isNaN(date.getTime())) {
    return isoDate;
  }
  return dateFormatter.format(date);
}

/** Today's date as yyyy-MM-dd, useful as a default form value. */
export function todayIso(): string {
  return new Date().toISOString().slice(0, 10);
}

export function humanizeEnum(value: string): string {
  return value
    .toLowerCase()
    .split("_")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}
