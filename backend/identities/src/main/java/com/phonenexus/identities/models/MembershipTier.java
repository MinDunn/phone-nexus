package com.phonenexus.identities.models;

public enum MembershipTier {
    NORMAL(1.0),
    SILVER(1.1),
    GOLD(1.2),
    DIAMOND(1.5);

    private final double multiplier;

    MembershipTier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
