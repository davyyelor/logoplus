import { useState } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { StatusBadge } from "../components/StatusBadge";
import { useAsync } from "../hooks/useAsync";
import { centerService } from "../services/centerService";
import type { Center, CenterRequest } from "../types/api";

interface FormState {
  name: string;
  address: string;
  city: string;
  phone: string;
  email: string;
  active: boolean;
}

const EMPTY_FORM: FormState = {
  name: "",
  address: "",
  city: "",
  phone: "",
  email: "",
  active: true,
};

function toForm(center: Center): FormState {
  return {
    name: center.name,
    address: center.address ?? "",
    city: center.city ?? "",
    phone: center.phone ?? "",
    email: center.email ?? "",
    active: center.active,
  };
}

export function CentersPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Center | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(() => centerService.list(), []);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(center: Center) {
    setEditing(center);
    setForm(toForm(center));
    setFormError(null);
    setModalOpen(true);
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setFormError(null);
    if (!form.name.trim()) {
      setFormError("El nombre es obligatorio");
      return;
    }
    const request: CenterRequest = {
      name: form.name.trim(),
      address: form.address.trim() || null,
      city: form.city.trim() || null,
      phone: form.phone.trim() || null,
      email: form.email.trim() || null,
      active: form.active,
    };
    setSaving(true);
    try {
      if (editing) {
        await centerService.update(editing.id, request);
        setToast("Centro actualizado");
      } else {
        await centerService.create(request);
        setToast("Centro creado");
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function toggleActive(center: Center) {
    try {
      if (center.active) {
        await centerService.deactivate(center.id);
        setToast("Centro desactivado");
      } else {
        await centerService.activate(center.id);
        setToast("Centro activado");
      }
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "La acción falló");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Centros</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          Nuevo centro
        </button>
      </div>

      {toast && <div className="toast">{toast}</div>}

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="No hay centros configurados."
      >
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Ciudad</th>
              <th>Teléfono</th>
              <th>Email</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {data?.map((center) => (
              <tr key={center.id}>
                <td>{center.name}</td>
                <td>{center.city}</td>
                <td>{center.phone}</td>
                <td>{center.email}</td>
                <td>
                  <StatusBadge status={center.active ? "Activo" : "Inactivo"} />
                </td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => openEdit(center)}>
                      Editar
                    </button>
                    <button className="btn btn--sm" onClick={() => toggleActive(center)}>
                      {center.active ? "Desactivar" : "Activar"}
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </StateView>

      <Modal
        open={modalOpen}
        title={editing ? "Editar centro" : "Nuevo centro"}
        onClose={() => setModalOpen(false)}
      >
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <label className="field">
              Nombre *
              <input
                value={form.name}
                onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              Ciudad
              <input
                value={form.city}
                onChange={(e) => setForm((f) => ({ ...f, city: e.target.value }))}
              />
            </label>
            <label className="field field--full">
              Dirección
              <input
                value={form.address}
                onChange={(e) => setForm((f) => ({ ...f, address: e.target.value }))}
              />
            </label>
            <label className="field">
              Teléfono
              <input
                value={form.phone}
                onChange={(e) => setForm((f) => ({ ...f, phone: e.target.value }))}
              />
            </label>
            <label className="field">
              Email
              <input
                type="email"
                value={form.email}
                onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
              />
            </label>
            <label className="field field--checkbox">
              <input
                type="checkbox"
                checked={form.active}
                onChange={(e) => setForm((f) => ({ ...f, active: e.target.checked }))}
              />
              Activo
            </label>
          </div>
          {formError && <p className="form-error">{formError}</p>}
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
