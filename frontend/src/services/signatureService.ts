import { apiGet, apiSend } from "./apiClient";
import type {
  SignatureRecord,
  SignatureRequest,
  SignatureStatusResponse,
} from "../types/api";

/** Staff-facing electronic signature API. Patient-scoped on the backend. */
export const signatureService = {
  status(): Promise<SignatureStatusResponse> {
    return apiGet<SignatureStatusResponse>("/signatures/status");
  },

  listForPatient(patientId: string): Promise<SignatureRecord[]> {
    return apiGet<SignatureRecord[]>(`/patients/${patientId}/signatures`);
  },

  get(id: string): Promise<SignatureRecord> {
    return apiGet<SignatureRecord>(`/signatures/${id}`);
  },

  sign(patientId: string, request: SignatureRequest): Promise<SignatureRecord> {
    return apiSend<SignatureRecord>(`/patients/${patientId}/signatures`, "POST", request);
  },
};

/** Family-facing electronic signature API. Scoped to linked patients. */
export const familySignatureService = {
  status(): Promise<SignatureStatusResponse> {
    return apiGet<SignatureStatusResponse>("/family/signatures/status");
  },

  listForPatient(patientId: string): Promise<SignatureRecord[]> {
    return apiGet<SignatureRecord[]>(`/family/patients/${patientId}/signatures`);
  },

  sign(patientId: string, request: SignatureRequest): Promise<SignatureRecord> {
    return apiSend<SignatureRecord>(`/family/patients/${patientId}/signatures`, "POST", request);
  },
};
