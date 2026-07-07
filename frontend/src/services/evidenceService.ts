import { apiGet, apiSend, apiUpload } from "./apiClient";
import type {
  EvidenceReviewStatus,
  EvidenceType,
  FamilyEvidence,
  FamilyEvidenceRequest,
} from "../types/api";

export const evidenceService = {
  // Staff
  listForPatient(patientId: string): Promise<FamilyEvidence[]> {
    return apiGet<FamilyEvidence[]>(`/patients/${patientId}/family-evidence`);
  },

  listPending(): Promise<FamilyEvidence[]> {
    return apiGet<FamilyEvidence[]>(`/family-evidence/pending`);
  },

  review(id: string, reviewStatus: EvidenceReviewStatus): Promise<FamilyEvidence> {
    return apiSend<FamilyEvidence>(`/family-evidence/${id}/review`, "PATCH", { reviewStatus });
  },

  // Family portal
  createTextNote(patientId: string, request: FamilyEvidenceRequest): Promise<FamilyEvidence> {
    return apiSend<FamilyEvidence>(`/family/patients/${patientId}/evidence`, "POST", request);
  },

  upload(
    patientId: string,
    file: File,
    meta: { type?: EvidenceType; title?: string; description?: string; homeworkId?: string } = {},
  ): Promise<FamilyEvidence> {
    const form = new FormData();
    form.append("file", file);
    if (meta.type) form.append("type", meta.type);
    if (meta.title) form.append("title", meta.title);
    if (meta.description) form.append("description", meta.description);
    if (meta.homeworkId) form.append("homeworkId", meta.homeworkId);
    return apiUpload<FamilyEvidence>(`/family/patients/${patientId}/evidence/upload`, form);
  },
};
