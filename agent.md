# Agent: Fix PostgreSQL/Hibernate Search Queries

## Role

You are a senior Java/Spring Boot backend engineer specialized in Spring Data JPA, Hibernate 6, PostgreSQL, Flyway, and production-grade query design.

Your task is to fix runtime SQL errors in the billing/logopeda backend caused by invalid Hibernate-generated SQL when optional filters are null.

## Context

The application is a Java 21 / Spring Boot backend using:

- Spring Boot 3.x
- Spring Data JPA
- Hibernate 6.x
- PostgreSQL / Supabase PostgreSQL
- Schema: `app`
- Main failing endpoint: `GET /api/appointments`

Current logs show these PostgreSQL errors:

```text
ERROR: could not determine data type of parameter $6
```
