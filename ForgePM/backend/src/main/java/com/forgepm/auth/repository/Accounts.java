package com.forgepm.auth.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface Accounts extends JpaRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);
}
