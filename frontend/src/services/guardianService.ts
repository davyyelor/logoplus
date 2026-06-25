import { apiGet, apiSend } from "./apiClient";
import type {
  CreateFamilyAccessRequest,
  FamilyAccess,
  Guardian,
  GuardianRequest,
} from "../types/api";

export const guardianService = {
  listForPatient(patientId: string): Promise<Guardian[]> {
    return apiGet<Guardian[]>(`/patients/${patientId}/guardians`);
  },

  create(patientId: string, request: GuardianRequest): Promise<Guardian> {
    return apiSend<Guardian>(`/patients/${patientId}/guardians`, "POST", request);
  },

  update(id: string, request: GuardianRequest): Promise<Guardian> {
    return apiSend<Guardian>(`/guardians/${id}`, "PUT", request);
  },

  remove(id: string): Promise<void> {
    return apiSend<void>(`/guardians/${id}`, "DELETE");
  },

  createFamilyAccess(id: string, request: CreateFamilyAccessRequest): Promise<FamilyAccess> {
    return apiSend<FamilyAccess>(`/guardians/${id}/create-family-access`, "POST", request);
  },
};
