// Thin fetch wrapper for the API. Centralizes the base URL, the bearer token,
// the (fallback) clinic header and JSON/error handling so individual services
// stay small.

import { clearToken, getToken } from "./tokenStore";

const API_BASE = "/api";

// Fallback tenant header. When the user is authenticated the backend derives
// the clinic from the JWT and ignores this; it only matters for the legacy
// billing endpoints when no token is present (e.g. local exploration).
const CLINIC_ID = "clinic-default";

function buildHeaders(hasBody: boolean): HeadersInit {
  const headers: Record<string, string> = {
    "X-Clinic-Id": CLINIC_ID,
  };
  const token = getToken();
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  if (hasBody) {
    headers["Content-Type"] = "application/json";
  }
  return headers;
}

/**
 * Called when the server reports the session is no longer valid. Clears the
 * stored token and sends the user back to the login screen.
 */
function handleUnauthorized(): void {
  clearToken();
  if (globalThis.location.pathname !== "/login") {
    globalThis.location.assign("/login");
  }
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

/** Builds an Error from a failed response and triggers logout on 401. */
async function failFromResponse(response: Response): Promise<Error> {
  if (response.status === 401) {
    handleUnauthorized();
  }
  return new Error(await parseError(response));
}

export async function apiGet<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: buildHeaders(false),
  });
  if (!response.ok) {
    throw await failFromResponse(response);
  }
  return response.json() as Promise<T>;
}

export async function apiSend<T>(
  path: string,
  method: "POST" | "PUT" | "PATCH" | "DELETE",
  body?: unknown,
): Promise<T> {
  const hasBody = body !== undefined;
  const response = await fetch(`${API_BASE}${path}`, {
    method,
    headers: buildHeaders(hasBody),
    body: hasBody ? JSON.stringify(body) : undefined,
  });
  if (!response.ok) {
    throw await failFromResponse(response);
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return response.json() as Promise<T>;
}

/** Fetches a binary/file endpoint (CSV, PDF, stored document) and saves it. */
export async function downloadFile(path: string, fileName: string): Promise<void> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: buildHeaders(false),
  });
  if (!response.ok) {
    throw await failFromResponse(response);
  }
  const blob = await response.blob();
  triggerBlobDownload(blob, fileName);
}

/** Backwards-compatible alias used by the billing CSV export services. */
export const downloadCsv = downloadFile;

/**
 * POSTs a multipart/form-data body (file upload). The browser sets the
 * Content-Type boundary automatically, so we must NOT set it ourselves; we only
 * attach the auth + clinic headers.
 */
export async function apiUpload<T>(path: string, formData: FormData): Promise<T> {
  const headers: Record<string, string> = {
    "X-Clinic-Id": CLINIC_ID,
  };
  const token = getToken();
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  const response = await fetch(`${API_BASE}${path}`, {
    method: "POST",
    headers,
    body: formData,
  });
  if (!response.ok) {
    throw await failFromResponse(response);
  }
  return response.json() as Promise<T>;
}

function triggerBlobDownload(blob: Blob, fileName: string): void {
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
