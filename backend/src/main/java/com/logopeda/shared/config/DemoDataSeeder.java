package com.logopeda.shared.config;

import com.logopeda.clinic.model.Clinic;
import com.logopeda.clinic.repository.ClinicRepository;
import com.logopeda.consent.enums.ConsentType;
import com.logopeda.consent.model.ConsentTemplate;
import com.logopeda.consent.repository.ConsentTemplateRepository;
import com.logopeda.guardian.enums.GuardianRelationship;
import com.logopeda.guardian.model.Guardian;
import com.logopeda.guardian.repository.GuardianRepository;
import com.logopeda.patient.enums.Gender;
import com.logopeda.patient.enums.PatientStatus;
import com.logopeda.patient.model.Patient;
import com.logopeda.patient.repository.PatientRepository;
import com.logopeda.report.enums.ReportType;
import com.logopeda.report.model.ReportTemplate;
import com.logopeda.report.repository.ReportTemplateRepository;
import com.logopeda.shared.enums.Role;
import com.logopeda.user.model.User;
import com.logopeda.user.repository.UserRepository;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a coherent demo dataset (one clinic, users for every role, two patients,
 * a family link and a couple of templates) so the app is usable immediately in
 * local development. Idempotent: it does nothing if the demo admin already
 * exists. Enabled only when {@code app.seed-demo-data=true} — never enable in
 * production.
 *
 * <p>The demo clinic intentionally uses the same id as the billing default
 * ({@code clinic-default}) and patient ids {@code patient-001}/{@code patient-002}
 * so the existing billing sample data lines up with real demo patients.</p>
 *
 * <p>Demo credentials (local only): admin@demo.local / therapist@demo.local /
 * reception@demo.local / family@demo.local, all with password {@code Demo1234!}.</p>
 */
@Component
@Order(20)
@ConditionalOnProperty(prefix = "app", name = "seed-demo-data", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private static final String CLINIC_ID = "clinic-default";
    private static final String PATIENT_1_ID = "patient-001";
    private static final String PATIENT_2_ID = "patient-002";
    // Demo-only credential. The seeder runs only when app.seed-demo-data=true
    // (never in production). Documented in the README so testers can log in.
    @SuppressWarnings("java:S2068")
    private static final String DEMO_LOGIN_SECRET = "Demo1234!";
    private static final String ADMIN_EMAIL = "admin@demo.local";

    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final GuardianRepository guardianRepository;
    private final ReportTemplateRepository reportTemplateRepository;
    private final ConsentTemplateRepository consentTemplateRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(ClinicRepository clinicRepository, UserRepository userRepository,
                          PatientRepository patientRepository, GuardianRepository guardianRepository,
                          ReportTemplateRepository reportTemplateRepository,
                          ConsentTemplateRepository consentTemplateRepository,
                          PasswordEncoder passwordEncoder) {
        this.clinicRepository = clinicRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.guardianRepository = guardianRepository;
        this.reportTemplateRepository = reportTemplateRepository;
        this.consentTemplateRepository = consentTemplateRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmailIgnoreCase(ADMIN_EMAIL)) {
            return;
        }
        log.info("Seeding demo data (app.seed-demo-data=true). Do not enable in production.");

        Clinic clinic = clinicRepository.findById(CLINIC_ID).orElseGet(() -> {
            Clinic c = new Clinic();
            c.setId(CLINIC_ID);
            c.setName("Centro LogoPlus Demo");
            c.setLegalName("LogoPlus Demo S.L.");
            c.setEmail("info@demo.local");
            c.setPhone("600000000");
            c.setCity("Madrid");
            c.setProvince("Madrid");
            c.setCountry("ES");
            return clinicRepository.save(c);
        });

        User therapist = createUser(clinic.getId(), "therapist@demo.local", "Tomás", "Terapeuta", Role.THERAPIST);
        createUser(clinic.getId(), ADMIN_EMAIL, "Ana", "Admin", Role.CLINIC_ADMIN);
        createUser(clinic.getId(), "reception@demo.local", "Rosa", "Recepción", Role.RECEPTION);
        User family = createUser(clinic.getId(), "family@demo.local", "Familia", "Demo", Role.FAMILY);

        Patient patient1 = createPatient(clinic.getId(), PATIENT_1_ID, "Lucía", "García",
                LocalDate.now().minusYears(6), therapist.getId(),
                "Retraso en el desarrollo del lenguaje");
        createPatient(clinic.getId(), PATIENT_2_ID, "Mateo", "Fernández",
                LocalDate.now().minusYears(8), therapist.getId(),
                "Dificultades en la articulación");

        // Link the family user to patient 1 with portal access.
        Guardian guardian = new Guardian();
        guardian.setClinicId(clinic.getId());
        guardian.setPatientId(patient1.getId());
        guardian.setUserId(family.getId());
        guardian.setFirstName("Familia");
        guardian.setLastName("Demo");
        guardian.setRelationship(GuardianRelationship.MOTHER);
        guardian.setEmail("family@demo.local");
        guardian.setCanAccessPortal(true);
        guardian.setCanReceiveReports(true);
        guardian.setCanReceiveReminders(true);
        guardianRepository.save(guardian);

        seedReportTemplate(clinic.getId());
        seedConsentTemplate(clinic.getId());

        log.info("Demo data seeded: clinic={}, users=4, patients=2", clinic.getId());
    }

    private User createUser(String clinicId, String email, String firstName, String lastName, Role role) {
        User user = new User();
        user.setClinicId(clinicId);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(DEMO_LOGIN_SECRET));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

    private Patient createPatient(String clinicId, String id, String firstName, String lastName,
                                  LocalDate birthDate, String therapistId, String reason) {
        Patient patient = patientRepository.findById(id).orElseGet(Patient::new);
        patient.setId(id);
        patient.setClinicId(clinicId);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setBirthDate(birthDate);
        patient.setGender(Gender.UNSPECIFIED);
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setMainTherapistId(therapistId);
        patient.setReasonForConsultation(reason);
        return patientRepository.save(patient);
    }

    private void seedReportTemplate(String clinicId) {
        ReportTemplate template = new ReportTemplate();
        template.setClinicId(clinicId);
        template.setName("Informe de evolución");
        template.setReportType(ReportType.EVOLUTION);
        template.setContentTemplate("""
                INFORME DE EVOLUCIÓN

                Centro: {{clinic.name}}
                Fecha: {{today}}

                Paciente: {{patient.fullName}}
                Fecha de nacimiento: {{patient.birthDate}} (edad: {{patient.age}})
                Motivo de consulta: {{patient.reasonForConsultation}}

                Resumen de la intervención:
                (Complete este apartado con la evolución observada.)

                Recomendaciones:
                {{recommendations}}
                """);
        template.setActive(true);
        reportTemplateRepository.save(template);
    }

    private void seedConsentTemplate(String clinicId) {
        ConsentTemplate template = new ConsentTemplate();
        template.setClinicId(clinicId);
        template.setName("Consentimiento de tratamiento de datos");
        template.setConsentType(ConsentType.DATA_PROCESSING);
        template.setBody("""
                Autorizo al centro al tratamiento de los datos personales del paciente
                con la finalidad de la prestación del servicio terapéutico, de acuerdo
                con la normativa de protección de datos aplicable.
                """);
        template.setActive(true);
        consentTemplateRepository.save(template);
    }
}
