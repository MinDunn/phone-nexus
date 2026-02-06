package com.phonenexus.notifications.events;

import java.util.UUID;

public record UserEvent(
        UUID userId,
        String email,
        String fullName,
        String verificationUrl) {
}
