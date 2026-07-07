-- V11: V2 Phase 3 — multi-center support.
-- New table `centers` plus additive optional center_id columns on existing
-- tables. All columns are nullable, so existing rows and V1 behaviour are
-- preserved (backward compatible). clinicId remains the tenant boundary.

create table centers (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    name varchar(255) not null,
    address varchar(255),
    city varchar(255),
    phone varchar(255),
    email varchar(255),
    active boolean not null default true,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_center_clinic on centers (clinic_id);

alter table patients add column center_id varchar(36);
alter table users add column center_id varchar(36);
alter table appointments add column center_id varchar(36);
alter table therapy_sessions add column center_id varchar(36);
