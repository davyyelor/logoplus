import { apiGet, apiSend } from "./apiClient";
import type { AuthResponse, CurrentUser, LoginRequest } from "../types/api";

export const authService = {
  login(request: LoginRequest): Promise<AuthResponse> {
    return apiSend<AuthResponse>("/auth/login", "POST", request);
  },

  me(): Promise<CurrentUser> {
    return apiGet<CurrentUser>("/auth/me");
  },
};
