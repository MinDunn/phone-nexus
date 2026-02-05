package com.phonenexus.identities.repositories;

import com.phonenexus.identities.models.UserLoginHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, UUID> {
    List<UserLoginHistory> findByUserIdOrderByLoginTimeDesc(UUID userId, Pageable pageable);

    List<UserLoginHistory> findByUsernameOrderByLoginTimeDesc(String username, Pageable pageable);
}
