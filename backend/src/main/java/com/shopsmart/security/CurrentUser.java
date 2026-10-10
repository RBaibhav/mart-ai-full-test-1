package com.shopsmart.security;

import java.util.UUID;

public class CurrentUser {
    private final UUID id;
    private final String name;
    private final String email;
    private final String membershipTier;

    public CurrentUser(UUID id, String name, String email, String membershipTier) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.membershipTier = membershipTier;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMembershipTier() {
        return membershipTier;
    }
}
