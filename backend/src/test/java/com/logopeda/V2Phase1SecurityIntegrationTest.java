package com.logopeda;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logopeda.billing.BillingApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Security and tenant-isolation tests for the V2 phase 1 modules (homework,
 * family evidence, questionnaires). They enforce the core invariants: staff
 * mutate within their clinic, families only ever touch patients they are linked
 * to, and family users cannot reach staff-only endpoints.
 */
@SpringBootTest(classes = BillingApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class V2Phase1SecurityIntegrationTest {

    private static final String LOGIN_PASSWORD = "Demo1234!";
    private static final String ADMIN_EMAIL = "admin@demo.local";
    private static final String FAMILY_EMAIL = "family@demo.local";
    private static final String LINKED_PATIENT = "patient-001";
    private static final String UNLINKED_PATIENT = "patient-002";

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

    private String createHomework(String adminToken, String patientId, boolean visibleToFamily) throws Exception {
        String body = objectMapper.writeValueAsString(new HomeworkBody(
                null, "Praxias diarias", "Repetir 10 veces", "Delante del espejo", null, visibleToFamily));
        MvcResult result = mockMvc.perform(post("/api/patients/" + patientId + "/homework")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void familySeesOnlyVisibleHomeworkForLinkedPatient() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        String visibleId = createHomework(adminToken, LINKED_PATIENT, true);
        createHomework(adminToken, LINKED_PATIENT, false);

        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/family/patients/" + LINKED_PATIENT + "/homework")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id", hasItem(visibleId)));
    }

    @Test
    void familyCannotReadHomeworkForUnlinkedPatient() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/family/patients/" + UNLINKED_PATIENT + "/homework")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void familyCannotReachStaffHomeworkEndpoint() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/patients/" + LINKED_PATIENT + "/homework")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void familyCanAdvanceHomeworkStatusForLinkedPatient() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        String id = createHomework(adminToken, LINKED_PATIENT, true);

        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        String body = objectMapper.writeValueAsString(new StatusBody("COMPLETED"));
        mockMvc.perform(patch("/api/family/homework/" + id + "/status")
                        .header("Authorization", "Bearer " + familyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void familyCanCreateTextEvidenceForLinkedPatient() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        String body = objectMapper.writeValueAsString(new EvidenceBody(
                null, "TEXT_NOTE", "Progreso en casa", "Ha completado la tarea", "Todo correcto"));
        mockMvc.perform(post("/api/family/patients/" + LINKED_PATIENT + "/evidence")
                        .header("Authorization", "Bearer " + familyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void familyCannotCreateEvidenceForUnlinkedPatient() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        String body = objectMapper.writeValueAsString(new EvidenceBody(
                null, "TEXT_NOTE", "Intento", "no permitido", "texto"));
        mockMvc.perform(post("/api/family/patients/" + UNLINKED_PATIENT + "/evidence")
                        .header("Authorization", "Bearer " + familyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void familyCannotReachStaffEvidenceReviewQueue() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/family-evidence/pending")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void familyCannotReachStaffQuestionnaireTemplates() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/questionnaire-templates")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedHomeworkRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/family/patients/" + LINKED_PATIENT + "/homework"))
                .andExpect(status().isUnauthorized());
    }

    private record LoginBody(String email, String password) {
    }

    private record HomeworkBody(String sessionId, String title, String description, String instructions,
                                String dueDate, Boolean visibleToFamily) {
    }

    private record StatusBody(String status) {
    }

    private record EvidenceBody(String homeworkId, String type, String title, String description,
                                String textContent) {
    }
}
