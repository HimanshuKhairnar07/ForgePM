package com.forgepm.config;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.util.Base64;
@Configuration
public class SecurityConfig {
    public static final String ISSUER = "forgepm";
    @Bean SecretKey signingKey(@Value("${forgepm.jwt-secret}") String configured) {
        byte[] bytes=Base64.getDecoder().decode(configured);
        if(bytes.length<32) throw new IllegalArgumentException("JWT_SECRET must encode at least 32 bytes");
        return new SecretKeySpec(bytes,"HmacSHA256");
    }
    @Bean JwtEncoder encoder(SecretKey key){return new NimbusJwtEncoder(new ImmutableSecret<>(key));}
    @Bean JwtDecoder decoder(SecretKey key){
        var decoder=NimbusJwtDecoder.withSecretKey(key).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(ISSUER));
        return decoder;
    }
    @Bean PasswordEncoder passwords(){return new BCryptPasswordEncoder(12);}
    @Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {
        return http.csrf(csrf->csrf.disable()) // Authorization header only; no authentication cookies.
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a.requestMatchers(HttpMethod.POST,"/api/v1/auth/register","/api/v1/auth/login").permitAll().anyRequest().authenticated())
            .oauth2ResourceServer(o->o.jwt(j->{}))
            .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->{
                res.setStatus(401); res.setHeader("WWW-Authenticate","Bearer"); res.setContentType("application/problem+json");
                res.getWriter().write("{\"status\":401,\"title\":\"Authentication required\"}");
            })).build();
    }
}
