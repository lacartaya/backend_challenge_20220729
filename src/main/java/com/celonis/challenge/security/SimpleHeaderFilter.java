package com.celonis.challenge.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class SimpleHeaderFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "Celonis-Auth";
    private static final String HEADER_VALUE = "totally_secret";

    private static final Set<String> EXACT_WHITELIST = Set.of(
            "/swagger-ui.html",
            "/openapi.yaml",
            "/h2-console",
            "/error"
    );
    private static final String[] PREFIX_WHITELIST = new String[]{
            "/swagger-ui/",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars"
    };
    public static final String OPTIONS = "OPTIONS";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (OPTIONS.equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        final var path = request.getServletPath();

        // Whitelist: Swagger/OpenAPI y otros públicos
        if (EXACT_WHITELIST.contains(path) || startsWithAny(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Auth simple por cabecera
        final var val = request.getHeader(HEADER_NAME);
        if (!HEADER_VALUE.equals(val)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().append("Not authorized");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean startsWithAny(String path) {
        for (final var p : SimpleHeaderFilter.PREFIX_WHITELIST) {
            if (path.startsWith(p)) return true;
        }
        return false;
    }
}
