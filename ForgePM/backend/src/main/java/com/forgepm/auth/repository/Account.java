package com.forgepm.auth.repository;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="users")
public class Account {
    @Id private UUID id;
    @Column(nullable=false, length=120) private String fullName;
    @Column(nullable=false, length=254) private String email;
    @Column(nullable=false, length=100) private String passwordHash;
    @Column(name="is_active", nullable=false) private boolean active;
    protected Account() {}
    public Account(String fullName, String email, String passwordHash) {
        this.id=UUID.randomUUID(); this.fullName=fullName; this.email=email; this.passwordHash=passwordHash; this.active=true;
    }
    public UUID getId(){return id;}
    public String getFullName(){return fullName;}
    public String getEmail(){return email;}
    public String getPasswordHash(){return passwordHash;}
    public boolean isActive(){return active;}
}
