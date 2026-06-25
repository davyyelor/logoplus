-- V1: Billing module tables (V0 foundation).
-- Column definitions mirror the JPA entities so Hibernate ddl-auto=validate
-- passes on PostgreSQL. Billing ids are varchar(255) (legacy mapping).

create table payments (
    id varchar(255) not null,
    clinic_id varchar(255) not null,
    patient_id varchar(255) not null,
    session_id varchar(255),
    fee_id varchar(255),
    amount numeric(12,2) not null,
    currency varchar(3) not null,
    payment_date date not null,
    method varchar(255) not null check (method in ('CASH','CARD','BANK_TRANSFER','BIZUM','STRIPE','OTHER')),
    status varchar(255) not null check (status in ('REGISTERED','REFUNDED','CANCELLED')),
    notes varchar(1000),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create table fees (
    id varchar(255) not null,
    clinic_id varchar(255) not null,
    patient_id varchar(255) not null,
    name varchar(255) not null,
    amount numeric(12,2) not null,
    currency varchar(3) not null,
    recurrence_type varchar(255) not null check (recurrence_type in ('MONTHLY','WEEKLY','CUSTOM')),
    start_date date not null,
    end_date date,
    active boolean not null,
    notes varchar(1000),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create table session_billing (
    id varchar(255) not null,
    clinic_id varchar(255) not null,
    patient_id varchar(255) not null,
    session_id varchar(255) not null,
    payment_id varchar(255),
    amount numeric(12,2) not null,
    paid_amount numeric(12,2) not null,
    currency varchar(3) not null,
    session_date date not null,
    status varchar(255) not null check (status in ('PENDING','PAID','PARTIALLY_PAID','CANCELLED','NO_CHARGE')),
    notes varchar(1000),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create table export_logs (
    id varchar(255) not null,
    clinic_id varchar(255) not null,
    export_type varchar(255) not null check (export_type in ('PAYMENTS','PENDING_SESSIONS','FEES','SUMMARY')),
    file_name varchar(255) not null,
    created_at timestamp(6) with time zone not null,
    primary key (id)
);
