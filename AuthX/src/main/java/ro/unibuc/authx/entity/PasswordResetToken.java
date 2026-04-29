package ro.unibuc.authx.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String token;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @Setter
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Getter
    @Setter
    @Column(nullable = false)
    private Boolean used = false;


    public PasswordResetToken(){}
    public PasswordResetToken(String token, User user, Instant expiresAt, Boolean used){
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.used = used;
    }
}
