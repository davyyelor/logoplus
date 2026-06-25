import type { ReactNode } from "react";

interface ModalProps {
  open: boolean;
  title: string;
  onClose: () => void;
  children: ReactNode;
}

/** Minimal accessible modal used to host create/edit forms. */
export function Modal({ open, title, onClose, children }: ModalProps) {
  if (!open) {
    return null;
  }
  return (
    <div className="modal-overlay" role="dialog" aria-modal="true" onClick={onClose}>
      <div className="modal" onClick={(event) => event.stopPropagation()}>
        <div className="modal__header">
          <h2>{title}</h2>
          <button type="button" className="btn btn--ghost" onClick={onClose} aria-label="Close">
            ×
          </button>
        </div>
        <div className="modal__body">{children}</div>
      </div>
    </div>
  );
}
