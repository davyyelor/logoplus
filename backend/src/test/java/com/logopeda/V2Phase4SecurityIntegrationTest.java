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
 * Security tests for the V2 phase 4 integrations (Stripe payment gateway and
 * calendar). They run against the default disabled mode: the webhook endpoint
 * is public, staff can read integration status, checkout is rejected while
 * disabled, and family users cannot reach any integration endpoint.
 */
@SpringBootTest(classes = BillingApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class V2Phase4SecurityIntegrationTest {

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
    void staffCanReadStripeStatusWhichIsDisabledByDefault() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/billing/stripe/status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("DISABLED"))
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    void checkoutIsRejectedWhileStripeIsDisabled() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        String body = objectMapper.writeValueAsString(new CheckoutBody(null, "Sesión", 5000, "EUR",
                null, null));
        mockMvc.perform(post("/api/billing/stripe/checkout")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void familyCannotReadStripeStatus() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/billing/stripe/status")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void stripeWebhookIsPublicAndAcknowledged() throws Exception {
        mockMvc.perform(post("/api/webhooks/stripe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"checkout.session.completed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("DISABLED"))
                .andExpect(jsonPath("$.handled").value(false));
    }

    @Test
    void staffCanReadCalendarStatusWhichIsDisabledByDefault() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/calendar/status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("DISABLED"))
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    void familyCannotReachCalendarStatus() throws Exception {
        String familyToken = login(FAMILY_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/calendar/status")
                        .header("Authorization", "Bearer " + familyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void icsExportForUnknownAppointmentIsNotFound() throws Exception {
        String adminToken = login(ADMIN_EMAIL, LOGIN_PASSWORD);
        mockMvc.perform(get("/api/appointments/does-not-exist/calendar.ics")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    private record LoginBody(String email, String password) {
    }

    private record CheckoutBody(String patientId, String description, long amountCents,
                                String currency, String successUrl, String cancelUrl) {
    }
}
