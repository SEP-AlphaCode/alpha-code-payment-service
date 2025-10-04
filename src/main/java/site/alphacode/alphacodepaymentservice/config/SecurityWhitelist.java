package site.alphacode.alphacodepaymentservice.config;

import org.springframework.stereotype.Component;

@Component
public class SecurityWhitelist {

    // Permit all methods
    public static final String[] GENERAL_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger",
            "/docs",
            "/",
            "/api/v1/auth/**",
            "/api/v1/payments/payos/verify-payment-webhook-data",
            "/api/v1/payments/github"
    };

    // Permit GET only
    public static final String[] GET_WHITELIST = {
            "/api/v1/**"
    };
}
