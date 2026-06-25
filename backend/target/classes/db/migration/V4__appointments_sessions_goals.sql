-- V4: Appointments, therapy sessions, goals and session-goal progress.

create table appointments (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    therapist_id varchar(36) not null,
    title varchar(255),
    start_date_time timestamp(6) with time zone not null,
    end_date_time timestamp(6) with time zone not null,
    status varchar(16) not null check (status in ('SCHEDULED','COMPLETED','CANCELLED','NO_SHOW')),
    location_type varchar(16) not null check (location_type in ('IN_PERSON','ONLINE','HOME','OTHER')),
    notes varchar(2000),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_appointments_clinic on appointments (clinic_id);
create index idx_appointments_patient on appointments (patient_id);
create index idx_appointments_therapist on appointments (therapist_id);
create index idx_appointments_start on appointments (start_date_time);
create index idx_appointments_status on appointments (status);

create table therapy_sessions (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    therapist_id varchar(36) not null,
    appointment_id varchar(36),
    session_date date not null,
    duration_minutes integer,
    session_type varchar(16) not null check (session_type in ('ASSESSMENT','THERAPY','FOLLOW_UP','FAMILY_GUIDANCE','OTHER')),
    summary varchar(4000),
    activities_performed varchar(4000),
    patient_response varchar(4000),
    observations varchar(4000),
    homework varchar(4000),
    next_steps varchar(4000),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_sessions_clinic on therapy_sessions (clinic_id);
create index idx_sessions_patient on therapy_sessions (patient_id);
create index idx_sessions_therapist on therapy_sessions (therapist_id);
create index idx_sessions_date on therapy_sessions (session_date);

create table therapy_goals (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    patient_id varchar(36) not null,
    area varchar(255),
    title varchar(255) not null,
    description varchar(2000),
    status varchar(16) not null check (status in ('NOT_STARTED','IN_PROGRESS','ACHIEVED','PAUSED','CANCELLED')),
    priority varchar(16) not null check (priority in ('LOW','MEDIUM','HIGH')),
    start_date date,
    target_date date,
    achieved_date date,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_goals_clinic on therapy_goals (clinic_id);
create index idx_goals_patient on therapy_goals (patient_id);
create index idx_goals_status on therapy_goals (status);

create table session_goal_progress (
    id varchar(36) not null,
    clinic_id varchar(36) not null,
    session_id varchar(36) not null,
    goal_id varchar(36) not null,
    progress_status varchar(24) not null check (progress_status in ('NOT_WORKED','WORKED','IMPROVED','ACHIEVED','DIFFICULTY_OBSERVED')),
    notes varchar(2000),
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

create index idx_sgp_clinic on session_goal_progress (clinic_id);
create index idx_sgp_session on session_goal_progress (session_id);
create index idx_sgp_goal on session_goal_progress (goal_id);
