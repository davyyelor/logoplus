import { useState } from "react";
import type { FormEvent } from "react";
import { Modal } from "../components/Modal";
import { StateView } from "../components/StateView";
import { useAsync } from "../hooks/useAsync";
import { userService } from "../services/clinicService";
import { type CreateUserRequest, type Role, type UpdateUserRequest, type User } from "../types/api";
import { humanizeEnum } from "../utils/format";

const STAFF_ROLES: Role[] = ["CLINIC_ADMIN", "THERAPIST", "RECEPTION"];

interface FormState {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: Role;
}

const EMPTY_FORM: FormState = {
  email: "",
  password: "",
  firstName: "",
  lastName: "",
  role: "THERAPIST",
};

export function UsersPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<User | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useAsync(() => userService.list(), []);

  function openCreate() {
    setEditing(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setModalOpen(true);
  }

  function openEdit(user: User) {
    setEditing(user);
    setForm({
      email: user.email,
      password: "",
      firstName: user.firstName,
      lastName: user.lastName,
      role: user.role,
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
    setSaving(true);
    setFormError(null);
    try {
      if (editing) {
        const request: UpdateUserRequest = {
          firstName: form.firstName.trim(),
          lastName: form.lastName.trim(),
          role: form.role,
          password: form.password.trim() || null,
        };
        await userService.update(editing.id, request);
      } else {
        if (!form.email.trim() || !form.password.trim()) {
          setFormError("Email y contraseña son obligatorios");
          setSaving(false);
          return;
        }
        const request: CreateUserRequest = {
          email: form.email.trim(),
          password: form.password,
          firstName: form.firstName.trim(),
          lastName: form.lastName.trim(),
          role: form.role,
        };
        await userService.create(request);
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo guardar");
    } finally {
      setSaving(false);
    }
  }

  async function toggleActive(user: User) {
    try {
      if (user.active) {
        await userService.deactivate(user.id);
      } else {
        await userService.activate(user.id);
      }
      reload();
    } catch (err) {
      globalThis.alert(err instanceof Error ? err.message : "Acción fallida");
    }
  }

  return (
    <div>
      <div className="page-header">
        <h1>Usuarios</h1>
        <button className="btn btn--primary" onClick={openCreate}>
          Nuevo usuario
        </button>
      </div>

      <StateView
        loading={loading}
        error={error}
        isEmpty={!data || data.length === 0}
        emptyMessage="No hay usuarios."
      >
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Email</th>
              <th>Rol</th>
              <th>Activo</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data?.map((user) => (
              <tr key={user.id}>
                <td>
                  {user.firstName} {user.lastName}
                </td>
                <td>{user.email}</td>
                <td>{humanizeEnum(user.role)}</td>
                <td>{user.active ? "Sí" : "No"}</td>
                <td>
                  <div className="btn-row">
                    <button className="btn btn--sm" onClick={() => openEdit(user)}>
                      Editar
                    </button>
                    <button className="btn btn--sm" onClick={() => toggleActive(user)}>
                      {user.active ? "Desactivar" : "Activar"}
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
        title={editing ? "Editar usuario" : "Nuevo usuario"}
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
              Email {editing ? "" : "*"}
              <input
                type="email"
                value={form.email}
                onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
                disabled={Boolean(editing)}
              />
            </label>
            <label className="field">
              Rol
              <select
                value={form.role}
                onChange={(e) => setForm((f) => ({ ...f, role: e.target.value as Role }))}
              >
                {STAFF_ROLES.map((r) => (
                  <option key={r} value={r}>
                    {humanizeEnum(r)}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              {editing ? "Nueva contraseña (opcional)" : "Contraseña *"}
              <input
                type="password"
                value={form.password}
                onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
                autoComplete="new-password"
              />
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
