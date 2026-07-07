package com.logopeda.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application entry point. The V0 billing module lives under
 * {@code com.logopeda.billing}; V1 adds sibling modules under
 * {@code com.logopeda.*} (auth, clinic, user, patient, ...). The scan/entity/
 * repository base packages are widened to {@code com.logopeda} so every module
 * is picked up while keeping the existing billing module untouched.
 */
@SpringBootApplication(scanBasePackages = "com.logopeda")
@EntityScan("com.logopeda")
@EnableJpaRepositories("com.logopeda")
@EnableScheduling
public class BillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingApplication.class, args);
    }
}
