-- V3: Patients, guardians and audit logs.

create table patients (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    birth_date date,
    gender varchar(16) check (gender in ('MALE','FEMALE','OTHER','UNSPECIFIED')),
    status varchar(16) not null check (status in ('ACTIVE','PAUSED','DISCHARGED','INACTIVE')),
    main_therapist_id varchar(36),
    school_name varchar(255),
    referral_source varchar(255),
    reason_for_consultation varchar(2000),
    relevant_notes varchar(4000),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_patients_clinic on patients (clinic_id);
create index idx_patients_status on patients (status);
create index idx_patients_therapist on patients (main_therapist_id);

create table guardians (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    user_id varchar(36),
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    relationship varchar(16) not null check (relationship in ('MOTHER','FATHER','LEGAL_GUARDIAN','OTHER')),
    email varchar(255),
    phone varchar(32),
    can_access_portal boolean not null,
    can_receive_reports boolean not null,
    can_receive_reminders boolean not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_guardians_clinic on guardians (clinic_id);
create index idx_guardians_patient on guardians (patient_id);
create index idx_guardians_user on guardians (user_id);

create table audit_logs (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    user_id varchar(36),
    action varchar(64) not null,
    entity_type varchar(64),
    entity_id varchar(36),
    metadata varchar(2000),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_audit_clinic on audit_logs (clinic_id);
create index idx_audit_entity on audit_logs (entity_type, entity_id);
create index idx_audit_user on audit_logs (user_id);
