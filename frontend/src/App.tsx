import { Navigate, Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { LoginPage } from "./pages/LoginPage";
import { Dashboard } from "./pages/Dashboard";
import { PatientsPage } from "./pages/PatientsPage";
import { PatientDetailPage } from "./pages/PatientDetailPage";
import { AppointmentsPage } from "./pages/AppointmentsPage";
import { SessionsPage } from "./pages/SessionsPage";
import { ReportTemplatesPage } from "./pages/ReportTemplatesPage";
import { ConsentTemplatesPage } from "./pages/ConsentTemplatesPage";
import { QuestionnaireTemplatesPage } from "./pages/QuestionnaireTemplatesPage";
import { RemindersPage } from "./pages/RemindersPage";
import { CentersPage } from "./pages/CentersPage";
import { IntegrationsPage } from "./pages/IntegrationsPage";
import { StripeBillingPage } from "./pages/StripeBillingPage";
import { UsersPage } from "./pages/UsersPage";
import { ClinicSettingsPage } from "./pages/ClinicSettingsPage";
import { FamilyPortalPage } from "./pages/FamilyPortalPage";
import { SignaturesPage } from "./pages/SignaturesPage";
import { LandingPage } from "./pages/landing/LandingPage";
import { BillingDashboard } from "./pages/BillingDashboard";
import { PaymentsPage } from "./pages/PaymentsPage";
import { FeesPage } from "./pages/FeesPage";
import { SessionBillingPage } from "./pages/SessionBillingPage";
import type { Role } from "./types/api";

const STAFF: Role[] = ["CLINIC_ADMIN", "THERAPIST", "RECEPTION"];
const CLINICAL: Role[] = ["CLINIC_ADMIN", "THERAPIST"];
const ADMIN: Role[] = ["CLINIC_ADMIN"];

export function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/*"
        element={
          <Layout>
            <Routes>
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute roles={STAFF}>
                    <Dashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/patients"
                element={
                  <ProtectedRoute roles={STAFF}>
                    <PatientsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/patients/:patientId"
                element={
                  <ProtectedRoute roles={STAFF}>
                    <PatientDetailPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/appointments"
                element={
                  <ProtectedRoute roles={STAFF}>
                    <AppointmentsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/sessions"
                element={
                  <ProtectedRoute roles={CLINICAL}>
                    <SessionsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/report-templates"
                element={
                  <ProtectedRoute roles={CLINICAL}>
                    <ReportTemplatesPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/consent-templates"
                element={
                  <ProtectedRoute roles={CLINICAL}>
                    <ConsentTemplatesPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/questionnaires"
                element={
                  <ProtectedRoute roles={CLINICAL}>
                    <QuestionnaireTemplatesPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/users"
                element={
                  <ProtectedRoute roles={ADMIN}>
                    <UsersPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/clinic"
                element={
                  <ProtectedRoute roles={ADMIN}>
                    <ClinicSettingsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/billing"
                element={
                  <ProtectedRoute roles={STAFF}>
                    <BillingDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/payments"
                element={
                  <ProtectedRoute roles={STAFF}>
                    <PaymentsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/fees"
                element={
                  <ProtectedRoute roles={STAFF}>
                    <FeesPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/session-billing"
                element={
                  <ProtectedRoute roles={STAFF}>
                    <SessionBillingPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/reminders"
                element={
                  <ProtectedRoute roles={STAFF}>
                    <RemindersPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/settings/centers"
                element={
                  <ProtectedRoute roles={ADMIN}>
                    <CentersPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/settings/integrations"
                element={
                  <ProtectedRoute roles={ADMIN}>
                    <IntegrationsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/billing/stripe"
                element={
                  <ProtectedRoute roles={STAFF}>
                    <StripeBillingPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/portal"
                element={
                  <ProtectedRoute roles={["FAMILY"]}>
                    <FamilyPortalPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/portal/signatures"
                element={
                  <ProtectedRoute roles={["FAMILY"]}>
                    <SignaturesPage />
                  </ProtectedRoute>
                }
              />
              <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
          </Layout>
        }
      />
    </Routes>
  );
}
