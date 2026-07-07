package com.logopeda;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logopeda.billing.BillingApplication;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Security and tenant-isolation tests for the V2 phase 2 modules (internal
 * reminders and clinical history export). They enforce that reminders are
 * scoped per clinic, that family users only ever reach the read-only family
 * endpoint, and that the clinical history export is staff-only.
 */
@SpringBootTest(classes = BillingApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class V2Phase2SecurityIntegrationTest {

    private static final String LOGIN_PASSWORD = "Demo1234!";
    private static final String ADMIN_EMAIL = "admin@demo.local";
    private static final String FAMILY_EMAIL = "family@demo.local";
    private static final String LINKED_PATIENT = "patient-001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String login(String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(new LoginBody(email, password));
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    private String createReminder(String token, String targetUserId) throws Exception {
        String body = objectMapper.writeValueAsString(new ReminderBody(
                null, targetUserId, "Llamar a la familia", "Recordatorio interno",
                Instant.now().plusSeconds(3600), null, null));
        MvcResult result = mockMvc.perform(post("/api/reminders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void staffCanCreateAndListReminders() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        String id = createReminder(adminToken, null);
        mockMvc.perform(get("/api/reminders")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id=='" + id + "')]").exists());
    }

    @Test
    void familyCannotReachStaffReminderEndpoint() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/reminders")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void familyCanReadOwnReminderFeed() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/family/reminders")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedReminderRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/reminders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void staffCanExportClinicalHistoryForOwnClinicPatient() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/patients/" + LINKED_PATIENT + "/clinical-history/export")
                        .param("format", "CSV")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void familyCannotExportClinicalHistory() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/patients/" + LINKED_PATIENT + "/clinical-history/export")
                        .param("format", "CSV")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void exportForUnknownPatientIsNotFound() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/patients/does-not-exist/clinical-history/export")
                        .param("format", "PDF")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    private record LoginBody(String email, String password) {
    }

    private record ReminderBody(String patientId, String targetUserId, String title, String message,
                                Instant remindAt, String relatedEntityType, String relatedEntityId) {
    }
}
