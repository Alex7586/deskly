package ro.unibuc.deskly.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Getter
    @Setter
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Getter
    @Setter
    @Column(nullable = false)
    private String role;

    @Getter
    @Setter
    @Column(nullable = false)
    private Boolean locked = false;

    @Getter
    @Setter
    @Column(name = "failed_attempts", nullable = false)
    private Integer failedAttempts = 0;

    @Getter
    @Setter
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();


    public User(){}
}
