// V1 domain types mirroring the backend DTOs (see com.logopeda.*.dto). Dates are
// ISO strings: LocalDate -> "yyyy-MM-dd", Instant/LocalDateTime -> ISO datetime.

// ---------------------------------------------------------------------------
// Enums (string-literal unions + value arrays for dropdowns)
// ---------------------------------------------------------------------------

export type Role = "CLINIC_ADMIN" | "THERAPIST" | "RECEPTION" | "FAMILY";

export type Gender = "MALE" | "FEMALE" | "OTHER" | "UNSPECIFIED";
export const GENDERS: Gender[] = ["MALE", "FEMALE", "OTHER", "UNSPECIFIED"];

export type PatientStatus = "ACTIVE" | "PAUSED" | "DISCHARGED" | "INACTIVE";
export const PATIENT_STATUSES: PatientStatus[] = ["ACTIVE", "PAUSED", "DISCHARGED", "INACTIVE"];

export type GuardianRelationship = "MOTHER" | "FATHER" | "LEGAL_GUARDIAN" | "OTHER";
export const GUARDIAN_RELATIONSHIPS: GuardianRelationship[] = ["MOTHER", "FATHER", "LEGAL_GUARDIAN", "OTHER"];

export type AppointmentStatus = "SCHEDULED" | "COMPLETED" | "CANCELLED" | "NO_SHOW";
export const APPOINTMENT_STATUSES: AppointmentStatus[] = ["SCHEDULED", "COMPLETED", "CANCELLED", "NO_SHOW"];

export type LocationType = "IN_PERSON" | "ONLINE" | "HOME" | "OTHER";
export const LOCATION_TYPES: LocationType[] = ["IN_PERSON", "ONLINE", "HOME", "OTHER"];

export type SessionType = "ASSESSMENT" | "THERAPY" | "FOLLOW_UP" | "FAMILY_GUIDANCE" | "OTHER";
export const SESSION_TYPES: SessionType[] = ["ASSESSMENT", "THERAPY", "FOLLOW_UP", "FAMILY_GUIDANCE", "OTHER"];

export type GoalStatus = "NOT_STARTED" | "IN_PROGRESS" | "ACHIEVED" | "PAUSED" | "CANCELLED";
export const GOAL_STATUSES: GoalStatus[] = ["NOT_STARTED", "IN_PROGRESS", "ACHIEVED", "PAUSED", "CANCELLED"];

export type GoalPriority = "LOW" | "MEDIUM" | "HIGH";
export const GOAL_PRIORITIES: GoalPriority[] = ["LOW", "MEDIUM", "HIGH"];

export type GoalProgressStatus =
  | "NOT_WORKED"
  | "WORKED"
  | "IMPROVED"
  | "ACHIEVED"
  | "DIFFICULTY_OBSERVED";
export const GOAL_PROGRESS_STATUSES: GoalProgressStatus[] = [
  "NOT_WORKED",
  "WORKED",
  "IMPROVED",
  "ACHIEVED",
  "DIFFICULTY_OBSERVED",
];

export type ReportType = "INITIAL" | "EVOLUTION" | "SCHOOL" | "DISCHARGE" | "FAMILY" | "MEDICAL";
export const REPORT_TYPES: ReportType[] = ["INITIAL", "EVOLUTION", "SCHOOL", "DISCHARGE", "FAMILY", "MEDICAL"];

export type ReportStatus = "DRAFT" | "GENERATED" | "SHARED_WITH_FAMILY" | "ARCHIVED";

export type ConsentType =
  | "DATA_PROCESSING"
  | "IMAGE_AUDIO_VIDEO"
  | "FAMILY_PORTAL_ACCESS"
  | "TELETHERAPY"
  | "OTHER";
export const CONSENT_TYPES: ConsentType[] = [
  "DATA_PROCESSING",
  "IMAGE_AUDIO_VIDEO",
  "FAMILY_PORTAL_ACCESS",
  "TELETHERAPY",
  "OTHER",
];

export type ConsentStatus = "PENDING" | "SIGNED" | "REVOKED" | "EXPIRED";

export type DocumentType =
  | "REPORT"
  | "CONSENT"
  | "REFERRAL"
  | "EXERCISE"
  | "AUDIO"
  | "VIDEO"
  | "IMAGE"
  | "OTHER";
export const DOCUMENT_TYPES: DocumentType[] = [
  "REPORT",
  "CONSENT",
  "REFERRAL",
  "EXERCISE",
  "AUDIO",
  "VIDEO",
  "IMAGE",
  "OTHER",
];

export type NotificationType =
  | "APPOINTMENT_REMINDER"
  | "APPOINTMENT_CANCELLED"
  | "REPORT_SHARED"
  | "DOCUMENT_SHARED"
  | "CONSENT_REQUESTED"
  | "GENERAL";

// ---------------------------------------------------------------------------
// Auth
// ---------------------------------------------------------------------------

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresInMinutes: number;
  userId: string;
  clinicId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
}

export interface CurrentUser {
  userId: string;
  clinicId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
}

// ---------------------------------------------------------------------------
// Clinic
// ---------------------------------------------------------------------------

export interface Clinic {
  id: string;
  name: string;
  legalName: string | null;
  taxId: string | null;
  email: string | null;
  phone: string | null;
  address: string | null;
  city: string | null;
  province: string | null;
  postalCode: string | null;
  country: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ClinicRequest {
  name: string;
  legalName?: string | null;
  taxId?: string | null;
  email?: string | null;
  phone?: string | null;
  address?: string | null;
  city?: string | null;
  province?: string | null;
  postalCode?: string | null;
  country?: string | null;
}

// ---------------------------------------------------------------------------
// User
// ---------------------------------------------------------------------------

export interface User {
  id: string;
  clinicId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  active: boolean;
  centerId: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: Role;
  centerId?: string | null;
}

export interface UpdateUserRequest {
  firstName: string;
  lastName: string;
  role: Role;
  password?: string | null;
  centerId?: string | null;
}

// ---------------------------------------------------------------------------
// Patient
// ---------------------------------------------------------------------------

export interface Patient {
  id: string;
  clinicId: string;
  firstName: string;
  lastName: string;
  fullName: string;
  birthDate: string | null;
  gender: Gender;
  status: PatientStatus;
  mainTherapistId: string | null;
  schoolName: string | null;
  referralSource: string | null;
  reasonForConsultation: string | null;
  relevantNotes: string | null;
  centerId: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PatientRequest {
  firstName: string;
  lastName: string;
  birthDate?: string | null;
  gender: Gender;
  mainTherapistId?: string | null;
  schoolName?: string | null;
  referralSource?: string | null;
  reasonForConsultation?: string | null;
  relevantNotes?: string | null;
  centerId?: string | null;
}

// ---------------------------------------------------------------------------
// Guardian
// ---------------------------------------------------------------------------

export interface Guardian {
  id: string;
  clinicId: string;
  patientId: string;
  userId: string | null;
  firstName: string;
  lastName: string;
  fullName: string;
  relationship: GuardianRelationship;
  email: string | null;
  phone: string | null;
  canAccessPortal: boolean;
  canReceiveReports: boolean;
  canReceiveReminders: boolean;
  hasPortalAccount: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface GuardianRequest {
  firstName: string;
  lastName: string;
  relationship: GuardianRelationship;
  email?: string | null;
  phone?: string | null;
  canAccessPortal: boolean;
  canReceiveReports: boolean;
  canReceiveReminders: boolean;
}

export interface CreateFamilyAccessRequest {
  temporaryPassword: string;
}

export interface FamilyAccess {
  guardianId: string;
  userId: string;
  email: string;
}

// ---------------------------------------------------------------------------
// Appointment
// ---------------------------------------------------------------------------

export interface Appointment {
  id: string;
  clinicId: string;
  patientId: string;
  therapistId: string | null;
  title: string | null;
  startDateTime: string;
  endDateTime: string;
  status: AppointmentStatus;
  locationType: LocationType;
  notes: string | null;
  centerId: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface AppointmentRequest {
  patientId: string;
  therapistId?: string | null;
  title?: string | null;
  startDateTime: string;
  endDateTime: string;
  locationType: LocationType;
  notes?: string | null;
  centerId?: string | null;
}

// ---------------------------------------------------------------------------
// Therapy session
// ---------------------------------------------------------------------------

export interface TherapySession {
  id: string;
  clinicId: string;
  patientId: string;
  therapistId: string | null;
  appointmentId: string | null;
  sessionDate: string;
  durationMinutes: number | null;
  sessionType: SessionType;
  summary: string | null;
  activitiesPerformed: string | null;
  patientResponse: string | null;
  observations: string | null;
  homework: string | null;
  nextSteps: string | null;
  centerId: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface TherapySessionRequest {
  patientId: string;
  therapistId?: string | null;
  appointmentId?: string | null;
  sessionDate: string;
  durationMinutes?: number | null;
  sessionType: SessionType;
  summary?: string | null;
  activitiesPerformed?: string | null;
  patientResponse?: string | null;
  observations?: string | null;
  homework?: string | null;
  nextSteps?: string | null;
  centerId?: string | null;
}

// ---------------------------------------------------------------------------
// Therapy goal + progress
// ---------------------------------------------------------------------------

export interface TherapyGoal {
  id: string;
  clinicId: string;
  patientId: string;
  area: string | null;
  title: string;
  description: string | null;
  status: GoalStatus;
  priority: GoalPriority;
  startDate: string | null;
  targetDate: string | null;
  achievedDate: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface TherapyGoalRequest {
  area?: string | null;
  title: string;
  description?: string | null;
  status: GoalStatus;
  priority: GoalPriority;
  startDate?: string | null;
  targetDate?: string | null;
  achievedDate?: string | null;
}

export interface SessionGoalProgress {
  id: string;
  clinicId: string;
  sessionId: string;
  goalId: string;
  goalTitle: string | null;
  progressStatus: GoalProgressStatus;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface SessionGoalProgressRequest {
  goalId: string;
  progressStatus: GoalProgressStatus;
  notes?: string | null;
}

// ---------------------------------------------------------------------------
// Report
// ---------------------------------------------------------------------------

export interface ReportTemplate {
  id: string;
  clinicId: string;
  name: string;
  reportType: ReportType;
  contentTemplate: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ReportTemplateRequest {
  name: string;
  reportType: ReportType;
  contentTemplate: string;
}

export interface GeneratedReport {
  id: string;
  clinicId: string;
  patientId: string;
  templateId: string | null;
  reportType: ReportType;
  title: string;
  content: string;
  pdfDocumentId: string | null;
  status: ReportStatus;
  generatedAt: string | null;
  createdByUserId: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface GenerateReportRequest {
  templateId: string;
  title: string;
  includeSessions: boolean;
  sessionsFrom?: string | null;
  sessionsTo?: string | null;
  includeGoals: boolean;
  goalIds?: string[];
  recommendations?: string | null;
}

export interface UpdateReportRequest {
  title: string;
  content: string;
}

// ---------------------------------------------------------------------------
// Consent
// ---------------------------------------------------------------------------

export interface ConsentTemplate {
  id: string;
  clinicId: string;
  name: string;
  consentType: ConsentType;
  body: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ConsentTemplateRequest {
  name: string;
  consentType: ConsentType;
  body: string;
}

export interface PatientConsent {
  id: string;
  clinicId: string;
  patientId: string;
  templateId: string | null;
  guardianId: string | null;
  consentType: ConsentType;
  title: string;
  body: string;
  status: ConsentStatus;
  signatureText: string | null;
  signedByUserId: string | null;
  signedAt: string | null;
  revokedAt: string | null;
  expiresOn: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface IssueConsentRequest {
  templateId: string;
  guardianId?: string | null;
  expiresOn?: string | null;
}

export interface SignConsentRequest {
  signatureText: string;
}

// ---------------------------------------------------------------------------
// Document
// ---------------------------------------------------------------------------

export interface DocumentMeta {
  id: string;
  clinicId: string;
  patientId: string | null;
  documentType: DocumentType;
  originalFileName: string;
  contentType: string;
  sizeBytes: number;
  visibleToFamily: boolean;
  uploadedByUserId: string | null;
  createdAt: string;
  updatedAt: string;
}

// ---------------------------------------------------------------------------
// Notification
// ---------------------------------------------------------------------------

export interface AppNotification {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  reference: string | null;
  read: boolean;
  createdAt: string;
}

// ===========================================================================
// V2 — Phase 1 (homework, family evidence, evolution metrics, questionnaires)
// ===========================================================================

// --- Homework ---
export type HomeworkStatus = "ASSIGNED" | "IN_PROGRESS" | "COMPLETED" | "REVIEWED" | "CANCELLED";
export const HOMEWORK_STATUSES: HomeworkStatus[] = [
  "ASSIGNED",
  "IN_PROGRESS",
  "COMPLETED",
  "REVIEWED",
  "CANCELLED",
];
// Statuses a family user is allowed to set.
export const FAMILY_HOMEWORK_STATUSES: HomeworkStatus[] = ["IN_PROGRESS", "COMPLETED"];

export interface Homework {
  id: string;
  clinicId: string;
  patientId: string;
  sessionId: string | null;
  createdByUserId: string | null;
  title: string;
  description: string | null;
  instructions: string | null;
  dueDate: string | null;
  status: HomeworkStatus;
  visibleToFamily: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface HomeworkRequest {
  sessionId?: string | null;
  title: string;
  description?: string | null;
  instructions?: string | null;
  dueDate?: string | null;
  visibleToFamily?: boolean | null;
}

// --- Family evidence ---
export type EvidenceType = "DOCUMENT" | "IMAGE" | "AUDIO" | "VIDEO" | "TEXT_NOTE";
export const EVIDENCE_TYPES: EvidenceType[] = ["DOCUMENT", "IMAGE", "AUDIO", "VIDEO", "TEXT_NOTE"];

export type EvidenceReviewStatus = "PENDING_REVIEW" | "REVIEWED" | "REJECTED";
export const EVIDENCE_REVIEW_STATUSES: EvidenceReviewStatus[] = [
  "PENDING_REVIEW",
  "REVIEWED",
  "REJECTED",
];

export interface FamilyEvidence {
  id: string;
  clinicId: string;
  patientId: string;
  homeworkId: string | null;
  uploadedByUserId: string | null;
  type: EvidenceType;
  title: string | null;
  description: string | null;
  storageDocumentId: string | null;
  textContent: string | null;
  reviewStatus: EvidenceReviewStatus;
  reviewedByUserId: string | null;
  reviewedAt: string | null;
  createdAt: string;
}

export interface FamilyEvidenceRequest {
  homeworkId?: string | null;
  type?: EvidenceType | null;
  title?: string | null;
  description?: string | null;
  textContent?: string | null;
}

// --- Evolution metrics ---
export interface PatientMetric {
  id: string;
  clinicId: string;
  patientId: string;
  name: string;
  description: string | null;
  unit: string | null;
  active: boolean;
  visibleToFamily: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PatientMetricRequest {
  name: string;
  description?: string | null;
  unit?: string | null;
  active?: boolean | null;
  visibleToFamily?: boolean | null;
}

export interface PatientMetricEntry {
  id: string;
  clinicId: string;
  patientId: string;
  metricId: string;
  sessionId: string | null;
  value: number;
  entryDate: string;
  notes: string | null;
  createdAt: string;
}

export interface PatientMetricEntryRequest {
  metricId?: string | null;
  sessionId?: string | null;
  value: number;
  entryDate: string;
  notes?: string | null;
}

export interface MetricEvolution {
  metric: PatientMetric;
  entries: PatientMetricEntry[];
}

// --- Questionnaires ---
export type QuestionnaireTargetRole = "STAFF" | "FAMILY" | "BOTH";
export const QUESTIONNAIRE_TARGET_ROLES: QuestionnaireTargetRole[] = ["STAFF", "FAMILY", "BOTH"];

export type QuestionType = "TEXT" | "NUMBER" | "SCALE_1_5" | "YES_NO" | "MULTIPLE_CHOICE";
export const QUESTION_TYPES: QuestionType[] = [
  "TEXT",
  "NUMBER",
  "SCALE_1_5",
  "YES_NO",
  "MULTIPLE_CHOICE",
];

export type AssignmentStatus = "PENDING" | "COMPLETED" | "REVIEWED" | "CANCELLED";
export const ASSIGNMENT_STATUSES: AssignmentStatus[] = [
  "PENDING",
  "COMPLETED",
  "REVIEWED",
  "CANCELLED",
];

export interface QuestionnaireQuestion {
  id: string;
  text: string;
  type: QuestionType;
  optionsJson: string | null;
  required: boolean;
  position: number;
}

export interface QuestionnaireTemplate {
  id: string;
  clinicId: string;
  name: string;
  description: string | null;
  targetRole: QuestionnaireTargetRole;
  active: boolean;
  questions: QuestionnaireQuestion[];
  createdAt: string;
  updatedAt: string;
}

export interface QuestionRequest {
  text: string;
  type?: QuestionType | null;
  optionsJson?: string | null;
  required?: boolean | null;
  position?: number | null;
}

export interface QuestionnaireTemplateRequest {
  name: string;
  description?: string | null;
  targetRole?: QuestionnaireTargetRole | null;
  active?: boolean | null;
  questions?: QuestionRequest[];
}

export interface QuestionnaireAssignment {
  id: string;
  clinicId: string;
  patientId: string;
  templateId: string;
  templateName: string | null;
  assignedByUserId: string | null;
  assignedToUserId: string | null;
  dueDate: string | null;
  status: AssignmentStatus;
  createdAt: string;
}

export interface AssignQuestionnaireRequest {
  templateId: string;
  assignedToUserId?: string | null;
  dueDate?: string | null;
}

export interface AnswerRequest {
  questionId: string;
  answerText?: string | null;
  answerNumber?: number | null;
  answerJson?: string | null;
}

export interface SubmitResponseRequest {
  answers: AnswerRequest[];
}

export interface QuestionnaireAnswer {
  id: string;
  questionId: string;
  answerText: string | null;
  answerNumber: number | null;
  answerJson: string | null;
}

export interface QuestionnaireResponseDetail {
  id: string;
  assignmentId: string;
  respondedByUserId: string | null;
  submittedAt: string;
  answers: QuestionnaireAnswer[];
}

// ---------------------------------------------------------------------------
// V2 — Phase 2 (internal reminders, clinical history export)
// ---------------------------------------------------------------------------

export type ReminderStatus = "SCHEDULED" | "SENT" | "CANCELLED" | "FAILED";
export const REMINDER_STATUSES: ReminderStatus[] = ["SCHEDULED", "SENT", "CANCELLED", "FAILED"];

export interface Reminder {
  id: string;
  clinicId: string;
  patientId: string | null;
  targetUserId: string | null;
  createdByUserId: string | null;
  title: string;
  message: string | null;
  remindAt: string;
  status: ReminderStatus;
  relatedEntityType: string | null;
  relatedEntityId: string | null;
  sentAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ReminderRequest {
  patientId?: string | null;
  targetUserId?: string | null;
  title: string;
  message?: string | null;
  remindAt: string;
  relatedEntityType?: string | null;
  relatedEntityId?: string | null;
}

export type ClinicalHistoryExportFormat = "ZIP" | "PDF" | "CSV";
export const CLINICAL_HISTORY_EXPORT_FORMATS: ClinicalHistoryExportFormat[] = ["ZIP", "PDF", "CSV"];

// ---------------------------------------------------------------------------
// V2 — Phase 3 (multi-center)
// ---------------------------------------------------------------------------

export interface Center {
  id: string;
  clinicId: string;
  name: string;
  address: string | null;
  city: string | null;
  phone: string | null;
  email: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CenterRequest {
  name: string;
  address?: string | null;
  city?: string | null;
  phone?: string | null;
  email?: string | null;
  active?: boolean | null;
}

// ---------------------------------------------------------------------------
// V2 — Phase 4 (Stripe payments, calendar integration)
// ---------------------------------------------------------------------------

export type StripeMode = "DISABLED" | "MOCK";

export interface StripeStatus {
  mode: StripeMode;
  enabled: boolean;
  platformFeePercent: number;
}

export interface CheckoutRequest {
  patientId?: string | null;
  description?: string | null;
  amountCents: number;
  currency?: string | null;
  successUrl?: string | null;
  cancelUrl?: string | null;
}

export interface CheckoutSessionResult {
  mode: StripeMode;
  enabled: boolean;
  sessionId: string | null;
  checkoutUrl: string | null;
}

export interface ConnectAccountResult {
  mode: StripeMode;
  enabled: boolean;
  accountId: string | null;
  onboardingUrl: string | null;
}

export type CalendarProviderType = "DISABLED" | "GOOGLE" | "OUTLOOK";

export interface CalendarStatus {
  provider: CalendarProviderType;
  enabled: boolean;
}

export interface CalendarSyncResult {
  provider: CalendarProviderType;
  synced: boolean;
  externalEventId: string | null;
  message: string | null;
}

// ---------------------------------------------------------------------------
// V2 — Phase 5 (electronic signatures)
// ---------------------------------------------------------------------------

export type SignatureProviderType = "INTERNAL_BASIC" | "ADVANCED_DISABLED";

export type SignatureStatus = "SIGNED" | "FAILED";

export interface SignatureStatusResponse {
  provider: SignatureProviderType;
  enabled: boolean;
}

export interface SignatureRequest {
  documentType: string;
  documentId?: string | null;
  signerName: string;
  note?: string | null;
}

export interface SignatureRecord {
  id: string;
  clinicId: string;
  patientId: string;
  documentType: string;
  documentId: string | null;
  signerUserId: string | null;
  signerName: string;
  provider: SignatureProviderType;
  status: SignatureStatus;
  signatureHash: string | null;
  note: string | null;
  signedAt: string | null;
  createdAt: string;
  updatedAt: string;
}
