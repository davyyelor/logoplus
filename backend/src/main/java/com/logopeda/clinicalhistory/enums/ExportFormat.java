package com.logopeda.clinicalhistory.enums;

/** Output formats supported by the clinical history export. */
public enum ExportFormat {
    /** ZIP bundle: PDF summary plus CSV datasets. */
    ZIP,
    /** Single PDF summary document. */
    PDF,
    /** Single CSV with the session listing. */
    CSV
}
