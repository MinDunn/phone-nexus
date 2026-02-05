package com.phonenexus.identities.repositories;

import com.phonenexus.identities.models.VerificationToken;
import com.phonenexus.identities.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user);
}
