import { apiGet, apiSend } from "./apiClient";
import type {
  AssignQuestionnaireRequest,
  QuestionnaireAssignment,
  QuestionnaireResponseDetail,
  QuestionnaireTemplate,
  QuestionnaireTemplateRequest,
  SubmitResponseRequest,
} from "../types/api";

export const questionnaireService = {
  // Staff — templates
  listTemplates(): Promise<QuestionnaireTemplate[]> {
    return apiGet<QuestionnaireTemplate[]>(`/questionnaire-templates`);
  },

  getTemplate(id: string): Promise<QuestionnaireTemplate> {
    return apiGet<QuestionnaireTemplate>(`/questionnaire-templates/${id}`);
  },

  createTemplate(request: QuestionnaireTemplateRequest): Promise<QuestionnaireTemplate> {
    return apiSend<QuestionnaireTemplate>(`/questionnaire-templates`, "POST", request);
  },

  updateTemplate(id: string, request: QuestionnaireTemplateRequest): Promise<QuestionnaireTemplate> {
    return apiSend<QuestionnaireTemplate>(`/questionnaire-templates/${id}`, "PUT", request);
  },

  // Staff — assignments
  assign(patientId: string, request: AssignQuestionnaireRequest): Promise<QuestionnaireAssignment> {
    return apiSend<QuestionnaireAssignment>(
      `/patients/${patientId}/questionnaires/assign`,
      "POST",
      request,
    );
  },

  listAssignments(patientId: string): Promise<QuestionnaireAssignment[]> {
    return apiGet<QuestionnaireAssignment[]>(`/patients/${patientId}/questionnaires`);
  },

  cancelAssignment(id: string): Promise<QuestionnaireAssignment> {
    return apiSend<QuestionnaireAssignment>(`/questionnaire-assignments/${id}/cancel`, "PATCH");
  },

  listResponses(assignmentId: string): Promise<QuestionnaireResponseDetail[]> {
    return apiGet<QuestionnaireResponseDetail[]>(
      `/questionnaire-assignments/${assignmentId}/responses`,
    );
  },

  // Family portal
  myQuestionnaires(): Promise<QuestionnaireAssignment[]> {
    return apiGet<QuestionnaireAssignment[]>(`/family/questionnaires`);
  },

  familyTemplate(assignmentId: string): Promise<QuestionnaireTemplate> {
    return apiGet<QuestionnaireTemplate>(`/family/questionnaires/${assignmentId}/template`);
  },

  submitAsFamily(
    assignmentId: string,
    request: SubmitResponseRequest,
  ): Promise<QuestionnaireResponseDetail> {
    return apiSend<QuestionnaireResponseDetail>(
      `/family/questionnaires/${assignmentId}/responses`,
      "POST",
      request,
    );
  },
};
