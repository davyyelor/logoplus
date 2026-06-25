import { useState } from "react";
import { StatCard } from "../components/StatCard";
import { StateView } from "../components/StateView";
import { useAsync } from "../hooks/useAsync";
import { billingService } from "../services/billingService";
import { formatCurrency } from "../utils/format";

export function BillingDashboard() {
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [exportError, setExportError] = useState<string | null>(null);

  const { data, loading, error } = useAsync(
    () => billingService.summary({ fromDate, toDate }),
    [fromDate, toDate],
  );

  async function runExport(action: () => Promise<void>) {
    setExportError(null);
    try {
      await action();
    } catch (err) {
      setExportError(err instanceof Error ? err.message : "Export failed");
    }
  }

  const currency = data?.currency ?? "EUR";

  return (
    <div>
      <div className="page-header">
        <h1>Billing dashboard</h1>
      </div>

      <div className="card">
        <div className="filters">
          <label className="field">
            From date
            <input type="date" value={fromDate} onChange={(e) => setFromDate(e.target.value)} />
          </label>
          <label className="field">
            To date
            <input type="date" value={toDate} onChange={(e) => setToDate(e.target.value)} />
          </label>
        </div>
      </div>

      {exportError && <div className="state state--error">Error: {exportError}</div>}

      <StateView loading={loading} error={error}>
        {data && (
          <>
            <div className="stat-grid">
              <StatCard label="Total paid" value={formatCurrency(data.totalPaid, currency)} accent="paid" />
              <StatCard
                label="Total pending"
                value={formatCurrency(data.totalPending, currency)}
                accent="pending"
              />
              <StatCard
                label="Total refunded"
                value={formatCurrency(data.totalRefunded, currency)}
                accent="refunded"
              />
              <StatCard label="Paid sessions" value={String(data.numberOfPaidSessions)} />
              <StatCard label="Pending sessions" value={String(data.numberOfPendingSessions)} />
              <StatCard label="Active fees" value={String(data.activeFees)} />
            </div>

            <div className="card">
              <h2 style={{ marginTop: 0, fontSize: "1.05rem" }}>Export CSV</h2>
              <div className="export-row">
                <button
                  className="btn"
                  onClick={() => runExport(() => billingService.exportPayments({ fromDate, toDate }))}
                >
                  Payments
                </button>
                <button
                  className="btn"
                  onClick={() =>
                    runExport(() => billingService.exportPendingSessions({ fromDate, toDate }))
                  }
                >
                  Pending sessions
                </button>
                <button className="btn" onClick={() => runExport(() => billingService.exportFees({}))}>
                  Fees
                </button>
                <button
                  className="btn"
                  onClick={() => runExport(() => billingService.exportSummary({ fromDate, toDate }))}
                >
                  Summary
                </button>
              </div>
            </div>
          </>
        )}
      </StateView>
    </div>
  );
}
