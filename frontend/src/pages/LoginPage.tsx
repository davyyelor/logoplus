import { useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const user = await login({ email: email.trim(), password });
      navigate(user.role === "FAMILY" ? "/portal" : "/", { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo iniciar sesión");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="login">
      <form className="login__card" onSubmit={handleSubmit}>
        <h1 className="login__brand">LogoPlus</h1>
        <p className="login__subtitle">Gestión clínica para logopedia y terapia infantil</p>

        {error && <div className="state state--error">{error}</div>}

        <label className="field">
          <span className="field__label">Correo electrónico</span>
          <input
            type="email"
            className="field__input"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="username"
            required
          />
        </label>

        <label className="field">
          <span className="field__label">Contraseña</span>
          <input
            type="password"
            className="field__input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </label>

        <button type="submit" className="button button--primary" disabled={submitting}>
          {submitting ? "Entrando…" : "Iniciar sesión"}
        </button>

        <p className="login__hint">
          Demo local: admin@demo.local / Demo1234!
        </p>
      </form>
    </div>
  );
}
