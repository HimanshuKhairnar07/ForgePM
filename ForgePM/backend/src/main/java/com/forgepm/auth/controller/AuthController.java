package com.forgepm.auth.controller;
import com.forgepm.auth.dto.AuthDtos.*;
import com.forgepm.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth){this.auth=auth;}
    @PostMapping("/auth/register") @ResponseStatus(HttpStatus.CREATED)
    public Profile register(@Valid @RequestBody Register input){return auth.register(input);}
    @PostMapping("/auth/login")
    public Session login(@Valid @RequestBody Login input){return auth.login(input);}
    @GetMapping("/users/me")
    public Profile current(@AuthenticationPrincipal Jwt jwt){return auth.current(jwt.getSubject());}
}
