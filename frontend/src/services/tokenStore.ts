// Centralized auth-token storage. The token (a JWT) is persisted in
// localStorage so a page refresh keeps the user signed in. Kept tiny and
// framework-agnostic so both the API client and the AuthContext can use it.

const TOKEN_KEY = "logoplus.token";

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}
