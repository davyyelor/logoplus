-- V7: Document metadata (file bytes live in the storage backend).

create table documents (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36),
    document_type varchar(16) not null check (document_type in ('REPORT','CONSENT','REFERRAL','EXERCISE','AUDIO','VIDEO','IMAGE','OTHER')),
    original_file_name varchar(255) not null,
    content_type varchar(128) not null,
    size_bytes bigint not null,
    storage_key varchar(255) not null,
    visible_to_family boolean not null,
    uploaded_by_user_id varchar(36),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_documents_clinic on documents (clinic_id);
create index idx_documents_patient on documents (patient_id);
