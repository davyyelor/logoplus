-- V12: V2 Phase 5 — electronic signatures.
-- New table `signature_records` holding the signature ledger. Additive only:
-- no existing tables are modified, so V1 behaviour is preserved. clinicId is the
-- tenant boundary and patientId scopes access for FAMILY users.

create table signature_records (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    document_type varchar(40) not null,
    document_id varchar(36),
    signer_user_id varchar(36),
    signer_name varchar(255) not null,
    provider varchar(30) not null,
    status varchar(20) not null,
    signature_hash varchar(128),
    note varchar(1000),
    signed_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_signature_clinic on signature_records (clinic_id);
create index idx_signature_patient on signature_records (patient_id);
