import { apiGet, apiSend, apiUpload, downloadFile } from "./apiClient";
import type { DocumentMeta, DocumentType } from "../types/api";

export const documentService = {
  listForPatient(patientId: string): Promise<DocumentMeta[]> {
    return apiGet<DocumentMeta[]>(`/patients/${patientId}/documents`);
  },

  upload(
    patientId: string,
    file: File,
    documentType: DocumentType,
    visibleToFamily: boolean,
  ): Promise<DocumentMeta> {
    const form = new FormData();
    form.append("file", file);
    form.append("documentType", documentType);
    form.append("visibleToFamily", String(visibleToFamily));
    return apiUpload<DocumentMeta>(`/patients/${patientId}/documents`, form);
  },

  download(id: string, fileName: string): Promise<void> {
    return downloadFile(`/documents/${id}/download`, fileName);
  },

  shareWithFamily(id: string): Promise<DocumentMeta> {
    return apiSend<DocumentMeta>(`/documents/${id}/share-with-family`, "PATCH");
  },

  hideFromFamily(id: string): Promise<DocumentMeta> {
    return apiSend<DocumentMeta>(`/documents/${id}/hide-from-family`, "PATCH");
  },

  remove(id: string): Promise<void> {
    return apiSend<void>(`/documents/${id}`, "DELETE");
  },
};
