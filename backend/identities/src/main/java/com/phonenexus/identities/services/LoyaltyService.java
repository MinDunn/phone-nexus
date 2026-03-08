package com.phonenexus.identities.services;

import com.phonenexus.identities.models.User;
import com.phonenexus.identities.models.MembershipTier;
import com.phonenexus.identities.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class LoyaltyService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void addPoints(UUID userId, Integer points, BigDecimal amountSpent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.setLoyaltyPoints(user.getLoyaltyPoints() + points);
        user.setTotalSpent(user.getTotalSpent().add(amountSpent));
        user.setTotalOrders(user.getTotalOrders() + 1);

        updateMembershipTier(user);
        userRepository.save(user);
    }

    private void updateMembershipTier(User user) {
        BigDecimal totalSpent = user.getTotalSpent();
        MembershipTier currentTier = user.getMembershipTier();
        MembershipTier newTier = currentTier;

        // Hardcoded thresholds for now
        // Normal: < 10,000,000
        // Silver: 10,000,000 - 50,000,000
        // Gold: 50,000,000 - 200,000,000
        // Diamond: > 200,000,000

        BigDecimal silverThreshold = new BigDecimal("10000000");
        BigDecimal goldThreshold = new BigDecimal("50000000");
        BigDecimal diamondThreshold = new BigDecimal("200000000");

        if (totalSpent.compareTo(diamondThreshold) >= 0) {
            newTier = MembershipTier.DIAMOND;
        } else if (totalSpent.compareTo(goldThreshold) >= 0) {
            newTier = MembershipTier.GOLD;
        } else if (totalSpent.compareTo(silverThreshold) >= 0) {
            newTier = MembershipTier.SILVER;
        }

        if (newTier != currentTier) {
            user.setMembershipTier(newTier);
        }
    }
}
