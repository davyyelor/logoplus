-- V10: V2 Phase 2 — internal reminders.
-- New tables only; no changes to existing tables (backward compatible).

create table reminders (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36),
    target_user_id varchar(36),
    created_by_user_id varchar(36),
    title varchar(255) not null,
    message varchar(2000),
    remind_at timestamp(6) with time zone not null,
    status varchar(16) not null check (status in ('SCHEDULED','SENT','CANCELLED','FAILED')),
    related_entity_type varchar(40),
    related_entity_id varchar(36),
    sent_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_reminder_clinic on reminders (clinic_id);
create index idx_reminder_status_time on reminders (status, remind_at);
create index idx_reminder_target_user on reminders (target_user_id);
