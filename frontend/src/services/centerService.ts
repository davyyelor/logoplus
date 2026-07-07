import { apiGet, apiSend } from "./apiClient";
import type { Center, CenterRequest } from "../types/api";

export const centerService = {
  list(): Promise<Center[]> {
    return apiGet<Center[]>("/centers");
  },

  get(id: string): Promise<Center> {
    return apiGet<Center>(`/centers/${id}`);
  },

  create(request: CenterRequest): Promise<Center> {
    return apiSend<Center>("/centers", "POST", request);
  },

  update(id: string, request: CenterRequest): Promise<Center> {
    return apiSend<Center>(`/centers/${id}`, "PUT", request);
  },

  activate(id: string): Promise<Center> {
    return apiSend<Center>(`/centers/${id}/activate`, "PATCH");
  },

  deactivate(id: string): Promise<Center> {
    return apiSend<Center>(`/centers/${id}/deactivate`, "PATCH");
  },
};
