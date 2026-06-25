import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../../components/Modal";
import { StateView } from "../../components/StateView";
import { useAsync } from "../../hooks/useAsync";
import { guardianService } from "../../services/guardianService";
import {
  GUARDIAN_RELATIONSHIPS,
  type Guardian,
  type GuardianRelationship,
  type GuardianRequest,
} from "../../types/api";
import { humanizeEnum } from "../../utils/format";

interface FormState {
  firstName: string;
  lastName: string;
  relationship: GuardianRelationship;
  email: string;
  phone: string;
  canAccessPortal: boolean;
  canReceiveReports: boolean;
  canReceiveReminders: boolean;
}

const EMPTY_FORM: FormState = {
  firstName: "",
  lastName: "",
  relationship: "MOTHER",
  email: "",
  phone: "",
  canAccessPortal: false,
  canReceiveReports: false,
  canReceiveReminders: false,
};

export function GuardiansTab({ patientId }: Readonly<{ patientId: string }>) {
  const { data, loading, error, reload } = useAsync(
    () => guardianService.listForPatient(patientId),
    [patientId],
  );
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Guardian | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [toast, setToast] = useState<string | null>(null);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(guardian: Guardian) {
    setEditing(guardian);
    setForm({
      firstName: guardian.firstName,
      lastName: guardian.lastName,
      relationship: guardian.relationship,
      email: guardian.email ?? "",
      phone: guardian.phone ?? "",
      canAccessPortal: guardian.canAccessPortal,
      canReceiveReports: guardian.canReceiveReports,
      canReceiveReminders: guardian.canReceiveReminders,
    });
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!form.firstName.trim() || !form.lastName.trim()) {
      setFormError("Nombre y apellidos son obligatorios");
      return;
    }
    const request: GuardianRequest = {
      firstName: form.firstName.trim(),
      lastName: form.lastName.trim(),
      relationship: form.relationship,
      email: form.email.trim() || null,
      phone: form.phone.trim() || null,
      canAccessPortal: form.canAccessPortal,
      canReceiveReports: form.canReceiveReports,
      canReceiveReminders: form.canReceiveReminders,
    };
    setSaving(true);
    setFormError(null);
    try {
      if (editing) {
        await guardianService.update(editing.id, request);
      } else {
        await guardianService.create(patientId, request);
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function grantPortalAccess(guardian: Guardian) {
    const tempPassword = globalThis.prompt(
      `Contraseña temporal para ${guardian.email ?? "el tutor"} (mínimo 8 caracteres):`,
    );
    if (!tempPassword) {
      return;
    }
    try {
      const access = await guardianService.createFamilyAccess(guardian.id, {
        temporaryPassword: tempPassword,
      });
      setToast(`Acceso creado para ${access.email}`);
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "No se pudo crear el acceso");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>Tutores / familias</h2>
        <button className="btn btn--primary" onClick={openCreate}>
          Añadir tutor
        </button>
      </div>

      {toast && <div className="toast">{toast}</div>}

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="Sin tutores registrados."
      >
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Relación</th>
              <th>Contacto</th>
              <th>Portal</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((guardian) => (
              <tr key={guardian.id}>
                <td>{guardian.fullName}</td>
                <td>{humanizeEnum(guardian.relationship)}</td>
                <td className="text-muted">{guardian.email ?? guardian.phone ?? "—"}</td>
                <td>{guardian.hasPortalAccount ? "Activo" : guardian.canAccessPortal ? "Permitido" : "No"}</td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => openEdit(guardian)}>
                      Editar
                    </button>
                    {guardian.canAccessPortal && !guardian.hasPortalAccount && (
                      <button className="btn btn--sm" onClick={() => grantPortalAccess(guardian)}>
                        Crear acceso
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>

      <Modal
        open={modalOpen}
        title={editing ? "Editar tutor" : "Nuevo tutor"}
        onClose={() => setModalOpen(false)}
      >
        <form onSubmit={handleSubmit}>
          {formError && <div className="state state--error">{formError}</div>}
          <div className="form-grid">
            <label className="field">
              Nombre *
              <input
                value={form.firstName}
                onChange={(e) => setForm((f) => ({ ...f, firstName: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Apellidos *
              <input
                value={form.lastName}
                onChange={(e) => setForm((f) => ({ ...f, lastName: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Relación
              <select
                value={form.relationship}
                onChange={(e) =>
                  setForm((f) => ({ ...f, relationship: e.target.value as GuardianRelationship }))
                }
              >
                {GUARDIAN_RELATIONSHIPS.map((r) => (
                  <option key={r} value={r}>
                    {humanizeEnum(r)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              Email
              <input
                type="email"
                value={form.email}
                onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
              />
            </label>
            <label className="field">
              Teléfono
              <input
                value={form.phone}
                onChange={(e) => setForm((f) => ({ ...f, phone: e.target.value }))}
              />
            </label>
            <label className="field field--check">
              <input
                type="checkbox"
                checked={form.canAccessPortal}
                onChange={(e) => setForm((f) => ({ ...f, canAccessPortal: e.target.checked }))}
              />
              Puede acceder al portal familiar
            </label>
            <label className="field field--check">
              <input
                type="checkbox"
                checked={form.canReceiveReports}
                onChange={(e) => setForm((f) => ({ ...f, canReceiveReports: e.target.checked }))}
              />
              Puede recibir informes
            </label>
            <label className="field field--check">
              <input
                type="checkbox"
                checked={form.canReceiveReminders}
                onChange={(e) => setForm((f) => ({ ...f, canReceiveReminders: e.target.checked }))}
              />
              Puede recibir recordatorios
            </label>
          </div>
          <div className="modal__actions">
            <button type="button" className="btn btn--ghost" onClick={() => setModalOpen(false)}>
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? "Guardando…" : "Guardar"}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
