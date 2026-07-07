-- DEPRECATED / LEGACY — only used by docker-compose.legacy.yml (local PostgreSQL).
-- With Supabase, Flyway owns schema `app` (create-schemas: true) and the pgcrypto
-- extension / timezone are managed in Supabase itself. Kept for self-hosted setups.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS app;

ALTER DATABASE clinic_saas SET timezone TO 'Europe/Madrid';