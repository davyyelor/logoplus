import { apiGet, apiSend, apiUpload, downloadFile } from "./apiClient";
import type {
  Appointment,
  DocumentMeta,
  DocumentType,
  GeneratedReport,
  Patient,
  PatientConsent,
  SignConsentRequest,
  TherapySession,
} from "../types/api";

/** Family-portal API. Every endpoint is scoped to patients the family is linked to. */
export const familyService = {
  myPatients(): Promise<Patient[]> {
    return apiGet<Patient[]>("/family/me/patients");
  },

  appointments(patientId: string): Promise<Appointment[]> {
    return apiGet<Appointment[]>(`/family/patients/${patientId}/appointments`);
  },

  sessions(patientId: string): Promise<TherapySession[]> {
    return apiGet<TherapySession[]>(`/family/patients/${patientId}/sessions`);
  },

  documents(patientId: string): Promise<DocumentMeta[]> {
    return apiGet<DocumentMeta[]>(`/family/patients/${patientId}/documents`);
  },

  reports(patientId: string): Promise<GeneratedReport[]> {
    return apiGet<GeneratedReport[]>(`/family/patients/${patientId}/reports`);
  },

  consents(patientId: string): Promise<PatientConsent[]> {
    return apiGet<PatientConsent[]>(`/family/patients/${patientId}/consents`);
  },

  uploadDocument(patientId: string, file: File, documentType: DocumentType): Promise<DocumentMeta> {
    const form = new FormData();
    form.append("file", file);
    form.append("documentType", documentType);
    return apiUpload<DocumentMeta>(`/family/patients/${patientId}/documents`, form);
  },

  signConsent(id: string, request: SignConsentRequest): Promise<PatientConsent> {
    return apiSend<PatientConsent>(`/family/consents/${id}/sign`, "PATCH", request);
  },

  downloadDocument(id: string, fileName: string): Promise<void> {
    return downloadFile(`/documents/${id}/download`, fileName);
  },

  downloadReportPdf(id: string, fileName: string): Promise<void> {
    return downloadFile(`/reports/${id}/pdf`, fileName);
  },

  downloadConsentPdf(id: string, fileName: string): Promise<void> {
    return downloadFile(`/consents/${id}/pdf`, fileName);
  },
};
