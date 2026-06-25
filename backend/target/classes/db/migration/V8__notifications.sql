-- V8: In-app notifications.

create table notifications (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    user_id varchar(36) not null,
    type varchar(32) not null check (type in ('APPOINTMENT_REMINDER','APPOINTMENT_CANCELLED','REPORT_SHARED','DOCUMENT_SHARED','CONSENT_REQUESTED','GENERAL')),
    title varchar(255) not null,
    message varchar(1000),
    reference varchar(128),
    read_flag boolean not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_notifications_clinic on notifications (clinic_id);
create index idx_notifications_user on notifications (user_id);
create index idx_notifications_read on notifications (read_flag);
