-- V9: V2 Phase 1 — home tasks, family evidence, visual evolution and questionnaires.
-- New tables only; no changes to existing V1 tables (backward compatible).

create table homework (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    session_id varchar(36),
    created_by_user_id varchar(36),
    title varchar(255) not null,
    description varchar(2000),
    instructions varchar(4000),
    due_date date,
    status varchar(16) not null check (status in ('ASSIGNED','IN_PROGRESS','COMPLETED','REVIEWED','CANCELLED')),
    visible_to_family boolean not null default true,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_homework_clinic on homework (clinic_id);
create index idx_homework_patient on homework (patient_id);
create index idx_homework_status on homework (status);

create table family_evidence (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    homework_id varchar(36),
    uploaded_by_user_id varchar(36),
    type varchar(16) not null check (type in ('DOCUMENT','IMAGE','AUDIO','VIDEO','TEXT_NOTE')),
    title varchar(255),
    description varchar(2000),
    storage_document_id varchar(36),
    text_content varchar(4000),
    review_status varchar(16) not null check (review_status in ('PENDING_REVIEW','REVIEWED','REJECTED')),
    reviewed_by_user_id varchar(36),
    reviewed_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_evidence_clinic on family_evidence (clinic_id);
create index idx_evidence_patient on family_evidence (patient_id);
create index idx_evidence_review on family_evidence (review_status);

create table patient_metrics (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    name varchar(255) not null,
    description varchar(1000),
    unit varchar(255),
    active boolean not null default true,
    visible_to_family boolean not null default false,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_metric_clinic on patient_metrics (clinic_id);
create index idx_metric_patient on patient_metrics (patient_id);

create table patient_metric_entries (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    metric_id varchar(36) not null,
    session_id varchar(36),
    value double precision not null,
    entry_date date not null,
    notes varchar(1000),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_metric_entry_clinic on patient_metric_entries (clinic_id);
create index idx_metric_entry_patient on patient_metric_entries (patient_id);
create index idx_metric_entry_metric on patient_metric_entries (metric_id);

create table questionnaire_templates (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    name varchar(255) not null,
    description varchar(1000),
    target_role varchar(16) not null check (target_role in ('STAFF','FAMILY','BOTH')),
    active boolean not null default true,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_qtemplate_clinic on questionnaire_templates (clinic_id);

create table questionnaire_questions (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    template_id varchar(36) not null,
    text varchar(1000) not null,
    type varchar(24) not null check (type in ('TEXT','NUMBER','SCALE_1_5','YES_NO','MULTIPLE_CHOICE')),
    options_json varchar(2000),
    required boolean not null default false,
    position integer not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_qquestion_template on questionnaire_questions (template_id);

create table questionnaire_assignments (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    template_id varchar(36) not null,
    assigned_by_user_id varchar(36),
    assigned_to_user_id varchar(36),
    due_date date,
    status varchar(16) not null check (status in ('PENDING','COMPLETED','REVIEWED','CANCELLED')),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_qassign_clinic on questionnaire_assignments (clinic_id);
create index idx_qassign_patient on questionnaire_assignments (patient_id);
create index idx_qassign_template on questionnaire_assignments (template_id);

create table questionnaire_responses (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    assignment_id varchar(36) not null,
    responded_by_user_id varchar(36),
    submitted_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_qresponse_clinic on questionnaire_responses (clinic_id);
create index idx_qresponse_assignment on questionnaire_responses (assignment_id);

create table questionnaire_answers (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    response_id varchar(36) not null,
    question_id varchar(36) not null,
    answer_text varchar(4000),
    answer_number double precision,
    answer_json varchar(2000),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_qanswer_response on questionnaire_answers (response_id);
create index idx_qanswer_question on questionnaire_answers (question_id);
