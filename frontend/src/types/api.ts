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
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: Role;
}

export interface UpdateUserRequest {
  firstName: string;
  lastName: string;
  role: Role;
  password?: string | null;
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
