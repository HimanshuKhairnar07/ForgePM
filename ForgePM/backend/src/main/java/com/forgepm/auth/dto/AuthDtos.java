package com.forgepm.auth.dto;
import jakarta.validation.constraints.*;
import java.util.UUID;
public final class AuthDtos {
    private AuthDtos() {}
    public record Register(@NotBlank @Size(max=120) String fullName,
                           @NotBlank @Email @Size(max=254) String email,
                           @NotBlank @Size(min=12,max=128) String password) {}
    public record Login(@NotBlank @Email @Size(max=254) String email,
                        @NotBlank @Size(max=128) String password) {}
    public record Profile(UUID id, String fullName, String email) {}
    public record Session(String accessToken, long expiresIn) {}
}
