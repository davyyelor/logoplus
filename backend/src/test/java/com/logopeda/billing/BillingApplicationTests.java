package com.logopeda.billing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** Verifies the full Spring context wires up and the application can start. */
@SpringBootTest
@ActiveProfiles("test")
class BillingApplicationTests {

    @Test
    void contextLoads() {
        // Intentionally empty: failure to load the context fails the test.
    }
}
