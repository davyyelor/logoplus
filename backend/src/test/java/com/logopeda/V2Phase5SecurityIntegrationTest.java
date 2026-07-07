package com.logopeda;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
 * Security and multi-tenant tests for the V2 phase 5 electronic signatures. They
 * verify the default internal-basic provider is enabled, staff and linked
 * families can sign, families cannot act on unlinked patients or reach staff
 * endpoints, and unauthenticated access is rejected.
 */
@SpringBootTest(classes = BillingApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class V2Phase5SecurityIntegrationTest {

    private static final String LOGIN_PASSWORD = "Demo1234!";
    private static final String ADMIN_EMAIL = "admin@demo.local";
    private static final String FAMILY_EMAIL = "family@demo.local";
    private static final String LINKED_PATIENT_ID = "patient-001";
    private static final String UNLINKED_PATIENT_ID = "patient-002";

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

    @Test
    void staffCanReadSignatureStatusWhichIsInternalBasicByDefault() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/signatures/status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("INTERNAL_BASIC"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void staffCanSignForAccessiblePatient() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        String body = objectMapper.writeValueAsString(
                new SignatureBody("CONSENT", null, "Ana Admin", "Firma de prueba"));
        mockMvc.perform(post("/api/patients/" + LINKED_PATIENT_ID + "/signatures")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.provider").value("INTERNAL_BASIC"))
                .andExpect(jsonPath("$.status").value("SIGNED"))
                .andExpect(jsonPath("$.signatureHash").isNotEmpty());
    }

    @Test
    void familyCanSignForLinkedPatient() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        String body = objectMapper.writeValueAsString(
                new SignatureBody("CONSENT", null, "Familia Demo", null));
        mockMvc.perform(post("/api/family/patients/" + LINKED_PATIENT_ID + "/signatures")
                        .header("Authorization", "Bearer " + familyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SIGNED"));
    }

    @Test
    void familyCannotSignForUnlinkedPatient() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        String body = objectMapper.writeValueAsString(
                new SignatureBody("CONSENT", null, "Familia Demo", null));
        mockMvc.perform(post("/api/family/patients/" + UNLINKED_PATIENT_ID + "/signatures")
                        .header("Authorization", "Bearer " + familyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void familyCannotReachStaffSignatureEndpoint() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/patients/" + LINKED_PATIENT_ID + "/signatures")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedSignatureStatusIsRejected() throws Exception {
        mockMvc.perform(get("/api/signatures/status"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signatureForUnknownPatientIsNotFound() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        String body = objectMapper.writeValueAsString(
                new SignatureBody("CONSENT", null, "Ana Admin", null));
        mockMvc.perform(post("/api/patients/does-not-exist/signatures")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    private record LoginBody(String email, String password) {
    }

    private record SignatureBody(String documentType, String documentId, String signerName, String note) {
    }
}
