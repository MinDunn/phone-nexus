package com.phonenexus.identities.repositories;

import com.phonenexus.identities.models.Address;
import com.phonenexus.identities.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUser(User user);
    List<Address> findByUserId(UUID userId);
}
