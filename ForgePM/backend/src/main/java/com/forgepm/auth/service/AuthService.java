package com.forgepm.auth.service;
import com.forgepm.auth.dto.AuthDtos.*;
import com.forgepm.auth.repository.*;
import com.forgepm.config.SecurityConfig;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
public class AuthService {
    private final Accounts accounts;
    private final PasswordEncoder passwords;
    private final JwtEncoder jwt;
    private final String dummyHash;
    public AuthService(Accounts accounts, PasswordEncoder passwords, JwtEncoder jwt) {
        this.accounts=accounts; this.passwords=passwords; this.jwt=jwt;
        this.dummyHash=passwords.encode(UUID.randomUUID().toString());
    }
    public Profile register(Register input) {
        checkLength(input.password());
        var account=new Account(input.fullName().strip(),normalize(input.email()),passwords.encode(input.password()));
        try { return profile(accounts.saveAndFlush(account)); }
        catch(DataIntegrityViolationException ex) { throw new ResponseStatusException(HttpStatus.CONFLICT,"Account could not be created"); }
    }
    public Session login(Login input) {
        checkLength(input.password());
        var account=accounts.findByEmail(normalize(input.email())).orElse(null);
        boolean matches=passwords.matches(input.password(),account==null?dummyHash:account.getPasswordHash());
        if(!matches || account==null || !account.isActive()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials");
        var now=Instant.now();
        var claims=JwtClaimsSet.builder().issuer(SecurityConfig.ISSUER).subject(account.getId().toString()).issuedAt(now).expiresAt(now.plusSeconds(900)).build();
        return new Session(jwt.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue(),900);
    }
    public Profile current(String subject){
        return accounts.findById(UUID.fromString(subject)).filter(Account::isActive).map(AuthService::profile)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account unavailable"));
    }
    private static String normalize(String email){return email.strip().toLowerCase(Locale.ROOT);}
    private static Profile profile(Account a){return new Profile(a.getId(),a.getFullName(),a.getEmail());}
    private static void checkLength(String password){
        if(password.getBytes(StandardCharsets.UTF_8).length>72) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password must be at most 72 UTF-8 bytes");
    }
}
