-- V6: Consent templates and patient consents.

create table consent_templates (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    name varchar(255) not null,
    consent_type varchar(24) not null check (consent_type in ('DATA_PROCESSING','IMAGE_AUDIO_VIDEO','FAMILY_PORTAL_ACCESS','TELETHERAPY','OTHER')),
    body text not null,
    active boolean not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_consent_templates_clinic on consent_templates (clinic_id);

create table patient_consents (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    template_id varchar(36),
    guardian_id varchar(36),
    consent_type varchar(24) not null check (consent_type in ('DATA_PROCESSING','IMAGE_AUDIO_VIDEO','FAMILY_PORTAL_ACCESS','TELETHERAPY','OTHER')),
    title varchar(255) not null,
    body text not null,
    status varchar(16) not null check (status in ('PENDING','SIGNED','REVOKED','EXPIRED')),
    signature_text varchar(255),
    signed_by_user_id varchar(255),
    signed_at timestamp(6) with time zone,
    revoked_at timestamp(6) with time zone,
    expires_on date,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_patient_consents_clinic on patient_consents (clinic_id);
create index idx_patient_consents_patient on patient_consents (patient_id);
create index idx_patient_consents_status on patient_consents (status);
