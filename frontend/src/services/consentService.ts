import { apiGet, apiSend, downloadFile } from "./apiClient";
import type {
  ConsentTemplate,
  ConsentTemplateRequest,
  IssueConsentRequest,
  PatientConsent,
  SignConsentRequest,
} from "../types/api";

export const consentTemplateService = {
  list(): Promise<ConsentTemplate[]> {
    return apiGet<ConsentTemplate[]>("/consent-templates");
  },

  get(id: string): Promise<ConsentTemplate> {
    return apiGet<ConsentTemplate>(`/consent-templates/${id}`);
  },

  create(request: ConsentTemplateRequest): Promise<ConsentTemplate> {
    return apiSend<ConsentTemplate>("/consent-templates", "POST", request);
  },

  update(id: string, request: ConsentTemplateRequest): Promise<ConsentTemplate> {
    return apiSend<ConsentTemplate>(`/consent-templates/${id}`, "PUT", request);
  },

  activate(id: string): Promise<ConsentTemplate> {
    return apiSend<ConsentTemplate>(`/consent-templates/${id}/activate`, "PATCH");
  },

  deactivate(id: string): Promise<ConsentTemplate> {
    return apiSend<ConsentTemplate>(`/consent-templates/${id}/deactivate`, "PATCH");
  },
};

export const consentService = {
  listForPatient(patientId: string): Promise<PatientConsent[]> {
    return apiGet<PatientConsent[]>(`/patients/${patientId}/consents`);
  },

  get(id: string): Promise<PatientConsent> {
    return apiGet<PatientConsent>(`/consents/${id}`);
  },

  issue(patientId: string, request: IssueConsentRequest): Promise<PatientConsent> {
    return apiSend<PatientConsent>(`/patients/${patientId}/consents`, "POST", request);
  },

  sign(id: string, request: SignConsentRequest): Promise<PatientConsent> {
    return apiSend<PatientConsent>(`/consents/${id}/sign`, "PATCH", request);
  },

  revoke(id: string): Promise<PatientConsent> {
    return apiSend<PatientConsent>(`/consents/${id}/revoke`, "PATCH");
  },

  downloadPdf(id: string, fileName: string): Promise<void> {
    return downloadFile(`/consents/${id}/pdf`, fileName);
  },
};
