import { apiGet, apiSend, downloadFile } from "./apiClient";
import type {
  GeneratedReport,
  GenerateReportRequest,
  ReportTemplate,
  ReportTemplateRequest,
  UpdateReportRequest,
} from "../types/api";

export const reportTemplateService = {
  list(): Promise<ReportTemplate[]> {
    return apiGet<ReportTemplate[]>("/report-templates");
  },

  get(id: string): Promise<ReportTemplate> {
    return apiGet<ReportTemplate>(`/report-templates/${id}`);
  },

  create(request: ReportTemplateRequest): Promise<ReportTemplate> {
    return apiSend<ReportTemplate>("/report-templates", "POST", request);
  },

  update(id: string, request: ReportTemplateRequest): Promise<ReportTemplate> {
    return apiSend<ReportTemplate>(`/report-templates/${id}`, "PUT", request);
  },

  activate(id: string): Promise<ReportTemplate> {
    return apiSend<ReportTemplate>(`/report-templates/${id}/activate`, "PATCH");
  },

  deactivate(id: string): Promise<ReportTemplate> {
    return apiSend<ReportTemplate>(`/report-templates/${id}/deactivate`, "PATCH");
  },
};

export const reportService = {
  listForPatient(patientId: string): Promise<GeneratedReport[]> {
    return apiGet<GeneratedReport[]>(`/patients/${patientId}/reports`);
  },

  get(id: string): Promise<GeneratedReport> {
    return apiGet<GeneratedReport>(`/reports/${id}`);
  },

  generate(patientId: string, request: GenerateReportRequest): Promise<GeneratedReport> {
    return apiSend<GeneratedReport>(`/patients/${patientId}/reports/generate`, "POST", request);
  },

  update(id: string, request: UpdateReportRequest): Promise<GeneratedReport> {
    return apiSend<GeneratedReport>(`/reports/${id}`, "PUT", request);
  },

  shareWithFamily(id: string): Promise<GeneratedReport> {
    return apiSend<GeneratedReport>(`/reports/${id}/share-with-family`, "PATCH");
  },

  archive(id: string): Promise<GeneratedReport> {
    return apiSend<GeneratedReport>(`/reports/${id}/archive`, "PATCH");
  },

  downloadPdf(id: string, fileName: string): Promise<void> {
    return downloadFile(`/reports/${id}/pdf`, fileName);
  },
};
