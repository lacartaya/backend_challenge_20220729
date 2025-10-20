package com.celonis.challenge.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleHeaderFilterTest {

    private final SimpleHeaderFilter filter = new SimpleHeaderFilter();

    private static MockHttpServletRequest req(String method, String path) {
        final var r = new MockHttpServletRequest();
        r.setMethod(method);
        r.setServletPath(path);
        return r;
    }

    private static MockHttpServletResponse resp() {
        return new MockHttpServletResponse();
    }

    // ----------------- Tests -----------------

    @Test
    @DisplayName("OPTIONS: siempre deja pasar sin auth")
    void optionsBypassesAuth() throws ServletException, IOException {
        //Given
        var request = req("OPTIONS", "/api/tasks");
        var response = resp();
        var chain = new MockFilterChain();

        //When
        filter.doFilter(request, response, chain);

        //Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(chain.getRequest()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/swagger-ui.html", "/openapi.yaml", "/h2-console", "/error"})
    @DisplayName("Whitelist EXACTA: paths públicos pasan sin header")
    void exactWhitelistAllows(String path) throws ServletException, IOException {
        //Given
        var request = req("GET", path);
        var response = resp();
        var chain = new MockFilterChain();

        //When
        filter.doFilter(request, response, chain);

        //Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(chain.getRequest()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/swagger-ui/index.html", "/v3/api-docs", "/swagger-resources", "/webjars/springfox-swagger-ui"})
    @DisplayName("Whitelist por prefijo: paths públicos pasan sin header")
    void prefixWhitelistAllows(String path) throws ServletException, IOException {
        //Given
        var request = req("GET", path);
        var response = resp();
        var chain = new MockFilterChain();

        //When
        filter.doFilter(request, response, chain);

        //Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    @DisplayName("Falta header -> 401 y no pasa al chain")
    void missingHeader_isUnauthorized() throws ServletException, IOException {
        //Given
        var request = req("GET", "/api/tasks");
        var response = resp();
        var chain = new MockFilterChain();

        //When
        filter.doFilter(request, response, chain);

        //Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Not authorized");
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    @DisplayName("Header incorrecto -> 401")
    void wrongHeader_isUnauthorized() throws ServletException, IOException {
        //Given
        var request = req("GET", "/api/tasks");
        request.addHeader("Celonis-Auth", "wrong");
        var response = resp();
        var chain = new MockFilterChain();

        //When
        filter.doFilter(request, response, chain);

        //Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Not authorized");
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    @DisplayName("Header correcto -> 200 y pasa al chain")
    void correctHeader_allows() throws ServletException, IOException {
        //Given
        var request = req("GET", "/api/tasks");
        request.addHeader("Celonis-Auth", "totally_secret");
        var response = resp();
        var chain = new MockFilterChain();

        //When
        filter.doFilter(request, response, chain);

        //Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    @DisplayName("Ruta casi-whitelist (/swagger-uiX) sin header -> 401")
    void nearWhitelist_shouldNotPass() throws Exception {
        //Given
        var request = req("GET", "/swagger-uiX");
        var response = resp();
        var chain = new MockFilterChain();

        //When
        filter.doFilter(request, response, chain);

        //Then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    @DisplayName("Prefijo extendido de docs (/v3/api-docs/swagger-config) sin header -> 200")
    void v3ApiDocsSwaggerConfig_shouldPass() throws Exception {
        //Given
        var request = req("GET", "/v3/api-docs/swagger-config");
        var response = resp();
        var chain = new MockFilterChain();

        //When
        filter.doFilter(request, response, chain);

        //Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    @DisplayName("POST no-whitelist con header correcto -> 200")
    void postWithHeader_shouldPass() throws Exception {
        //Given
        var request = req("POST", "/api/tasks");
        request.addHeader("Celonis-Auth", "totally_secret");
        var response = resp();
        var chain = new MockFilterChain();

        //When
        filter.doFilter(request, response, chain);

        //Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(chain.getRequest()).isNotNull();
    }
}
