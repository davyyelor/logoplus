package com.logopeda.report.service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Resolves {@code {{variable}}} placeholders in a template against a flat map of
 * values. Unknown placeholders are replaced with an empty string so reports
 * never leak raw {@code {{...}}} markers. Intentionally simple: this is text
 * substitution, not a scripting engine.
 */
@Component
public class TemplateEngine {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([\\w.]+)\\s*}}");

    public String render(String template, Map<String, String> values) {
        if (template == null) {
            return "";
        }
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = values.getOrDefault(key, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
