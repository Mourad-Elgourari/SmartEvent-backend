package com.congress.event.repository;

import com.congress.event.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,String> {
    VerificationToken findByToken(String token);
}
