// Thin fetch wrapper for the billing API. Centralizes the base URL, the
// (placeholder) clinic header and JSON/error handling so individual services
// stay small. When real authentication is added, inject the token and the
// resolved clinic id here without touching the rest of the app.

const API_BASE = "/api";

// Placeholder tenant header. The backend falls back to its default clinic when
// this is empty, so the app works before auth exists. Replace with the value
// derived from the authenticated session later.
const CLINIC_ID = "clinic-default";

function buildHeaders(hasBody: boolean): HeadersInit {
  const headers: Record<string, string> = {
    "X-Clinic-Id": CLINIC_ID,
  };
  if (hasBody) {
    headers["Content-Type"] = "application/json";
  }
  return headers;
}

async function parseError(response: Response): Promise<string> {
  try {
    const data = await response.json();
    if (data?.message) {
      const details = Array.isArray(data.details) && data.details.length
        ? `: ${data.details.join(", ")}`
        : "";
      return `${data.message}${details}`;
    }
  } catch {
    // fall through to status text
  }
  return `${response.status} ${response.statusText}`;
}

export function buildQuery(params: Record<string, unknown> | object): string {
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      search.append(key, String(value));
    }
  });
  const query = search.toString();
  return query ? `?${query}` : "";
}

export async function apiGet<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: buildHeaders(false),
  });
  if (!response.ok) {
    throw new Error(await parseError(response));
  }
  return response.json() as Promise<T>;
}

export async function apiSend<T>(
  path: string,
  method: "POST" | "PUT" | "PATCH" | "DELETE",
  body?: unknown,
): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    method,
    headers: buildHeaders(body !== undefined),
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
  if (!response.ok) {
    throw new Error(await parseError(response));
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return response.json() as Promise<T>;
}

/** Triggers a browser download of a CSV export endpoint. */
export async function downloadCsv(path: string, fileName: string): Promise<void> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: buildHeaders(false),
  });
  if (!response.ok) {
    throw new Error(await parseError(response));
  }
  const blob = await response.blob();
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
}

export { CLINIC_ID };
