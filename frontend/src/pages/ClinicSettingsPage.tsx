import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { StateView } from "../components/StateView";
import { useAsync } from "../hooks/useAsync";
import { clinicService } from "../services/clinicService";
import type { ClinicRequest } from "../types/api";

const EMPTY: ClinicRequest = {
  name: "",
  legalName: "",
  taxId: "",
  email: "",
  phone: "",
  address: "",
  city: "",
  province: "",
  postalCode: "",
  country: "",
};

export function ClinicSettingsPage() {
  const { data, loading, error, reload } = useAsync(() => clinicService.current(), []);
  const [form, setForm] = useState<ClinicRequest>(EMPTY);
  const [saving, setSaving] = useState(false);
  const [toast, setToast] = useState<string | null>(null);
  const [formError, setFormError] = useState<string | null>(null);

  useEffect(() => {
    if (data) {
      setForm({
        name: data.name,
        legalName: data.legalName ?? "",
        taxId: data.taxId ?? "",
        email: data.email ?? "",
        phone: data.phone ?? "",
        address: data.address ?? "",
        city: data.city ?? "",
        province: data.province ?? "",
        postalCode: data.postalCode ?? "",
        country: data.country ?? "",
      });
    }
  }, [data]);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.name?.trim()) {
      setFormError("El nombre es obligatorio");
      return;
    }
    setSaving(true);
    setFormError(null);
    try {
      await clinicService.update(form);
      setToast("Datos de la clínica actualizados");
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  function update<K extends keyof ClinicRequest>(key: K, value: string) {
    setForm((f) => ({ ...f, [key]: value }));
  }

  return (
    <div>
      <div className="page-header">
        <h1>Datos de la clínica</h1>
      </div>

      {toast && <div className="toast">{toast}</div>}

      <StateView loading={loading} error={error}>
        <form className="card" onSubmit={handleSubmit}>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="form-grid">
            <label className="field">
              Nombre *
              <input value={form.name ?? ""} onChange={(e) => update("name", e.target.value)} required />
            </label>
            <label className="field">
              Razón social
              <input value={form.legalName ?? ""} onChange={(e) => update("legalName", e.target.value)} />
            </label>
            <label className="field">
              NIF/CIF
              <input value={form.taxId ?? ""} onChange={(e) => update("taxId", e.target.value)} />
            </label>
            <label className="field">
              Email
              <input type="email" value={form.email ?? ""} onChange={(e) => update("email", e.target.value)} />
            </label>
            <label className="field">
              Teléfono
              <input value={form.phone ?? ""} onChange={(e) => update("phone", e.target.value)} />
            </label>
            <label className="field">
              Dirección
              <input value={form.address ?? ""} onChange={(e) => update("address", e.target.value)} />
            </label>
            <label className="field">
              Ciudad
              <input value={form.city ?? ""} onChange={(e) => update("city", e.target.value)} />
            </label>
            <label className="field">
              Provincia
              <input value={form.province ?? ""} onChange={(e) => update("province", e.target.value)} />
            </label>
            <label className="field">
              Código postal
              <input value={form.postalCode ?? ""} onChange={(e) => update("postalCode", e.target.value)} />
            </label>
            <label className="field">
              País
              <input value={form.country ?? ""} onChange={(e) => update("country", e.target.value)} />
            </label>
          </div>
          <div className="modal__actions">
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? "Guardando…" : "Guardar"}
            </button>
          </div>
        </form>
      </StateView>
    </div>
  );
}
