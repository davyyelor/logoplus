package com.logopeda;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logopeda.billing.BillingApplication;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Security and tenant-isolation tests for the V2 phase 3 module (multi-center).
 * They enforce that centers are readable by staff, that only clinic admins can
 * mutate them, that family users cannot reach the center endpoints, and that a
 * patient cannot be attached to a center id that does not belong to the clinic.
 */
@SpringBootTest(classes = BillingApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class V2Phase3SecurityIntegrationTest {

    private static final String LOGIN_PASSWORD = "Demo1234!";
    private static final String ADMIN_EMAIL = "admin@demo.local";
    private static final String THERAPIST_EMAIL = "therapist@demo.local";
    private static final String RECEPTION_EMAIL = "reception@demo.local";
    private static final String FAMILY_EMAIL = "family@demo.local";

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

    private String createCenter(String token, String name) throws Exception {
        String body = objectMapper.writeValueAsString(
                new CenterBody(name, "Calle Mayor 1", "Bilbao", null, null, true));
        MvcResult result = mockMvc.perform(post("/api/centers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void clinicAdminCanCreateAndListCenters() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        String id = createCenter(adminToken, "Centro Norte");
        mockMvc.perform(get("/api/centers")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id=='" + id + "')]").exists());
    }

    @Test
    void receptionCanListCentersButCannotCreate() throws Exception {
        String receptionToken = login(RECEPTION_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/centers")
                        .header("Authorization", "Bearer " + receptionToken))
                .andExpect(status().isOk());
        String body = objectMapper.writeValueAsString(
                new CenterBody("No permitido", null, null, null, null, true));
        mockMvc.perform(post("/api/centers")
                        .header("Authorization", "Bearer " + receptionToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void therapistCannotCreateCenter() throws Exception {
        String therapistToken = login(THERAPIST_EMAIL, LOGIN_PASSWORD);
        String body = objectMapper.writeValueAsString(
                new CenterBody("No permitido", null, null, null, null, true));
        mockMvc.perform(post("/api/centers")
                        .header("Authorization", "Bearer " + therapistToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void familyCannotReachCenterEndpoint() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/centers")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedCenterRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/centers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patientCannotBeAttachedToUnknownCenter() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        String body = objectMapper.writeValueAsString(new PatientBody(
                "Nuevo", "Paciente", LocalDate.of(2015, 1, 1), "center-does-not-exist"));
        mockMvc.perform(post("/api/patients")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    private record LoginBody(String email, String password) {
    }

    private record CenterBody(String name, String address, String city, String phone,
                              String email, Boolean active) {
    }

    private record PatientBody(String firstName, String lastName, LocalDate birthDate,
                               String centerId) {
    }
}
