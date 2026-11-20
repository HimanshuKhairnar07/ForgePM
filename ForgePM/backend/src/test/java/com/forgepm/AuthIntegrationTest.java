package com.forgepm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgepm.auth.repository.Accounts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties="forgepm.jwt-secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=")
@AutoConfigureMockMvc
class AuthIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired Accounts accounts;
    private static final String PASSWORD="test-only password";
    private String email(){return UUID.randomUUID()+"@example.com";}
    private org.springframework.test.web.servlet.ResultActions register(String email) throws Exception {
        return mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("email",email,"password",PASSWORD,"fullName","Test User"))));
    }
    private org.springframework.test.web.servlet.ResultActions login(String email,String password) throws Exception {
        return mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("email",email,"password",password))));
    }
    @Test void accountLifecycle() throws Exception {
        String email=email();
        register(email).andExpect(status().isCreated()).andExpect(jsonPath("$.passwordHash").doesNotExist());
        assertThat(accounts.findByEmail(email).orElseThrow().getPasswordHash()).startsWith("$2").isNotEqualTo(PASSWORD);
        String result=login(email,PASSWORD).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String bearer=json.readTree(result).get("accessToken").asText();
        mvc.perform(get("/api/v1/users/me").header("Authorization","Bearer "+bearer))
            .andExpect(status().isOk()).andExpect(jsonPath("$.email").value(email));
    }
    @Test void duplicateEmailIsCaseInsensitive() throws Exception {
        String email=email();register(email).andExpect(status().isCreated());
        register(email.toUpperCase(java.util.Locale.ROOT)).andExpect(status().isConflict());
    }
    @Test void invalidCredentialsAreRejected() throws Exception {
        String email=email();register(email).andExpect(status().isCreated());
        login(email,"invalid password").andExpect(status().isUnauthorized());
        login(email(),PASSWORD).andExpect(status().isUnauthorized());
    }
    @Test void protectedEndpointRejectsMissingAndInvalidTokens() throws Exception {
        mvc.perform(get("/api/v1/users/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/users/me").header("Authorization","Bearer not-a-jwt")).andExpect(status().isUnauthorized());
    }
    @Test void invalidRegistrationReturnsBadRequest() throws Exception {
        register("not-an-email").andExpect(status().isBadRequest());
    }
}
