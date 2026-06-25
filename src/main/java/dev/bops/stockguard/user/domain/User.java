package dev.bops.stockguard.user.domain;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class User {

    private final UUID id;
    private String email;
    private String passwordHash;
    private String fullName;
    private Role role;
    private boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;

    public User(String email, String passwordHash, String fullName, Role role) {
        Preconditions.checkArgument(email != null && email.contains("@"), "Email valide obligatoire");
        Preconditions.checkArgument(passwordHash != null && !passwordHash.isBlank(), "Mot de passe obligatoire");
        Preconditions.checkArgument(fullName != null && !fullName.isBlank(), "Nom complet obligatoire");
        Preconditions.checkArgument(role != null, "Rôle obligatoire");

        this.id = UUID.randomUUID();
        this.email = email.toLowerCase().trim();
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.enabled = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Constructeur de reconstruction
    public User(UUID id, String email, String passwordHash, String fullName, Role role,
                boolean enabled, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public enum Role {
        ADMIN, COMMERCIAL
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}