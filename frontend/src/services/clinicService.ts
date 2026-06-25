import { apiGet, apiSend } from "./apiClient";
import type { Clinic, ClinicRequest, CreateUserRequest, User, UpdateUserRequest } from "../types/api";

export const clinicService = {
  current(): Promise<Clinic> {
    return apiGet<Clinic>("/clinics/current");
  },

  update(request: ClinicRequest): Promise<Clinic> {
    return apiSend<Clinic>("/clinics/current", "PUT", request);
  },
};

export const userService = {
  list(): Promise<User[]> {
    return apiGet<User[]>("/users");
  },

  get(id: string): Promise<User> {
    return apiGet<User>(`/users/${id}`);
  },

  create(request: CreateUserRequest): Promise<User> {
    return apiSend<User>("/users", "POST", request);
  },

  update(id: string, request: UpdateUserRequest): Promise<User> {
    return apiSend<User>(`/users/${id}`, "PUT", request);
  },

  activate(id: string): Promise<User> {
    return apiSend<User>(`/users/${id}/activate`, "PATCH");
  },

  deactivate(id: string): Promise<User> {
    return apiSend<User>(`/users/${id}/deactivate`, "PATCH");
  },
};
