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

    // Rutas sin auth (Swagger/OpenAPI + error + estáticos)
    private static final Set<String> EXACT_WHITELIST = Set.of(
            "/swagger-ui.html",
            "/openapi.yaml",
            "/h2-console",
            "/error"
    );
    private static final String[] PREFIX_WHITELIST = new String[]{
            "/swagger-ui/",       // assets de la UI
            "/v3/api-docs",       // JSON del spec
            "/swagger-resources", // compat
            "/webjars"            // assets estáticos
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // OPTIONS should always work
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getServletPath();

        // Whitelist: Swagger/OpenAPI y otros públicos
        if (EXACT_WHITELIST.contains(path) || startsWithAny(path, PREFIX_WHITELIST)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Auth simple por cabecera
        String val = request.getHeader(HEADER_NAME);
        if (val == null || !HEADER_VALUE.equals(val)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().append("Not authorized");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean startsWithAny(String path, String[] prefixes) {
        for (String p : prefixes) {
            if (path.startsWith(p)) return true;
        }
        return false;
    }
}
