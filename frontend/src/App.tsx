import { Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout";
import { BillingDashboard } from "./pages/BillingDashboard";
import { PaymentsPage } from "./pages/PaymentsPage";
import { FeesPage } from "./pages/FeesPage";
import { SessionBillingPage } from "./pages/SessionBillingPage";

export function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<BillingDashboard />} />
        <Route path="/payments" element={<PaymentsPage />} />
        <Route path="/fees" element={<FeesPage />} />
        <Route path="/session-billing" element={<SessionBillingPage />} />
      </Routes>
    </Layout>
  );
}
