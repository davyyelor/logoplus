-- V5: Report templates and generated reports.

create table report_templates (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    name varchar(255) not null,
    report_type varchar(16) not null check (report_type in ('INITIAL','EVOLUTION','SCHOOL','DISCHARGE','FAMILY','MEDICAL')),
    content_template text not null,
    active boolean not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_report_templates_clinic on report_templates (clinic_id);

create table generated_reports (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    template_id varchar(36),
    report_type varchar(16) not null check (report_type in ('INITIAL','EVOLUTION','SCHOOL','DISCHARGE','FAMILY','MEDICAL')),
    title varchar(255) not null,
    content text not null,
    pdf_document_id varchar(36),
    status varchar(24) not null check (status in ('DRAFT','GENERATED','SHARED_WITH_FAMILY','ARCHIVED')),
    generated_at timestamp(6) with time zone,
    created_by_user_id varchar(36),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_reports_clinic on generated_reports (clinic_id);
create index idx_reports_patient on generated_reports (patient_id);
create index idx_reports_status on generated_reports (status);
