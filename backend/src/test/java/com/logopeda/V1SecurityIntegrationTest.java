package com.logopeda;

import static org.hamcrest.Matchers.hasSize;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * End-to-end security and tenant-isolation tests for the V1 platform.
 *
 * <p>These drive real HTTP through the Spring Security filter chain against the
 * demo data seeded by {@code DemoDataSeeder} (enabled via {@code app.seed-demo-data}
 * in the test {@code application.yml}). They are the authoritative guard for the
 * core selling point: families may only ever see patients they are linked to.
 */
@SpringBootTest(classes = BillingApplication.class)
@AutoConfigureMockMvc
class V1SecurityIntegrationTest {

    private static final String LOGIN_PASSWORD = "Demo1234!";
    private static final String ADMIN_EMAIL = "admin@demo.local";
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

    @Test
    void adminCanLoginAndSeesAllPatients() throws Exception {
        String token = login(ADMIN_EMAIL, LOGIN_PASSWORD);

        mockMvc.perform(get("/api/patients").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void familySeesOnlyLinkedPatient() throws Exception {
        String token = login(FAMILY_EMAIL, LOGIN_PASSWORD);

        mockMvc.perform(get("/api/family/me/patients").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("patient-001"));
    }

    @Test
    void familyIsForbiddenFromStaffPatientEndpoint() throws Exception {
        String token = login(FAMILY_EMAIL, LOGIN_PASSWORD);

        mockMvc.perform(get("/api/patients").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginWithWrongPasswordIsUnauthorized() throws Exception {
        String body = objectMapper.writeValueAsString(new LoginBody(ADMIN_EMAIL, "wrong-password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isUnauthorized());
    }

    /** Minimal login payload mirroring {@code LoginRequest}. */
    private record LoginBody(String email, String password) {
    }
}
