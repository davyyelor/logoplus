package com.logopeda.shared.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

/**
 * Chooses the active database at startup, before the Spring context (and its
 * DataSource) is created.
 *
 * <p>Decision matrix:
 * <ol>
 *   <li>Supabase configured ({@code SUPABASE_DB_URL/USERNAME/PASSWORD}) and
 *       reachable &rarr; activate the {@code supabase} profile (Flyway +
 *       {@code ddl-auto=validate} on schema {@code app}).</li>
 *   <li>Supabase not configured &rarr; keep the default H2 database.</li>
 *   <li>Supabase configured but unreachable:
 *     <ul>
 *       <li>{@code APP_DB_FALLBACK_TO_H2=true} (default) &rarr; fall back to H2
 *           (intended for local development only).</li>
 *       <li>{@code APP_DB_FALLBACK_TO_H2=false} &rarr; abort startup with a
 *           clear error instead of silently hiding the problem.</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p>The {@code test}, {@code h2}, {@code schemagen} and {@code docker} profiles
 * are left untouched: they pin their own datasource explicitly.
 */
public class DatabaseFallbackEnvironmentPostProcessor
        implements EnvironmentPostProcessor, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(DatabaseFallbackEnvironmentPostProcessor.class);

    private static final String SUPABASE_PROFILE = "supabase";
    private static final String H2_PROFILE = "h2";
    private static final List<String> PINNED_PROFILES =
            List.of("test", "h2", "schemagen", "docker");
    private static final int CONNECT_TIMEOUT_SECONDS = 8;

    @Override
    public int getOrder() {
        // Run before ConfigData loads profile-specific YAML, so that any profile
        // we activate here is picked up when its application-<profile>.yml loads.
        return ConfigDataEnvironmentPostProcessor.ORDER - 1;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
            SpringApplication application) {
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());

        // Explicitly pinned datasources: do not interfere.
        if (PINNED_PROFILES.stream().anyMatch(activeProfiles::contains)) {
            return;
        }

        boolean supabaseExplicit = activeProfiles.contains(SUPABASE_PROFILE);
        String url = environment.getProperty("SUPABASE_DB_URL");
        String username = environment.getProperty("SUPABASE_DB_USERNAME", "postgres");
        String password = environment.getProperty("SUPABASE_DB_PASSWORD");
        boolean fallbackToH2 =
                environment.getProperty("APP_DB_FALLBACK_TO_H2", Boolean.class, Boolean.TRUE);

        boolean supabaseConfigured = StringUtils.hasText(url) && StringUtils.hasText(password);

        // Case 2: no Supabase configuration and none requested -> default H2.
        if (!supabaseExplicit && !supabaseConfigured) {
            log.info("Supabase not configured; using the default H2 in-memory database.");
            return;
        }

        // Supabase requested (explicit profile) but credentials are incomplete.
        if (!supabaseConfigured) {
            if (fallbackToH2) {
                log.warn("Profile 'supabase' is active but SUPABASE_DB_URL/PASSWORD are missing; "
                        + "falling back to H2 (APP_DB_FALLBACK_TO_H2=true). Do NOT use this in production.");
                environment.addActiveProfile(H2_PROFILE);
                return;
            }
            throw new IllegalStateException(
                    "Profile 'supabase' is active but SUPABASE_DB_URL/SUPABASE_DB_PASSWORD are not set, "
                            + "and APP_DB_FALLBACK_TO_H2=false. Provide the Supabase credentials or enable the fallback.");
        }

        // Supabase configured: verify it is reachable before committing to it.
        if (isReachable(url, username, password)) {
            log.info("Supabase PostgreSQL reachable; activating the 'supabase' profile.");
            if (!supabaseExplicit) {
                environment.addActiveProfile(SUPABASE_PROFILE);
            }
            return;
        }

        // Configured but unreachable.
        if (fallbackToH2) {
            log.warn("Supabase configured but unreachable; falling back to H2 "
                    + "(APP_DB_FALLBACK_TO_H2=true). Do NOT use this fallback in production.");
            // If 'supabase' was pinned, add 'h2' so its YAML overrides the PG datasource.
            // Otherwise the default (H2) already applies and nothing else is needed.
            if (supabaseExplicit) {
                environment.addActiveProfile(H2_PROFILE);
            }
            return;
        }

        throw new IllegalStateException(
                "Cannot connect to Supabase PostgreSQL at " + url
                        + " and APP_DB_FALLBACK_TO_H2=false. Aborting startup.");
    }

    private boolean isReachable(String url, String username, String password) {
        DriverManager.setLoginTimeout(CONNECT_TIMEOUT_SECONDS);
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            return connection.isValid(CONNECT_TIMEOUT_SECONDS);
        } catch (Exception ex) {
            log.warn("Supabase connectivity check failed: {}", ex.getMessage());
            return false;
        }
    }
}
