-- V2: Clinics and users (multi-tenant + auth foundation).

create table clinics (
    id varchar(36) not null,
    name varchar(255) not null,
    legal_name varchar(255),
    tax_id varchar(32),
    email varchar(255),
    phone varchar(32),
    address varchar(255),
    city varchar(255),
    province varchar(255),
    postal_code varchar(16),
    country varchar(2),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create table users (
    id varchar(36) not null,
    clinic_id varchar(36),
    email varchar(255) not null,
    password_hash varchar(255) not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    role varchar(32) not null check (role in ('CLINIC_ADMIN','THERAPIST','RECEPTION','FAMILY')),
    active boolean not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

-- Email must be globally unique (login identifier).
create unique index ux_users_email on users (lower(email));
