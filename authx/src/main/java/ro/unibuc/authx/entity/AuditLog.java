package ro.unibuc.authx.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "user_id")
    private Long userId;

    @Getter
    @Setter
    @Column(nullable = false)
    private String action;

    @Getter
    @Setter
    @Column(nullable = false)
    private String resource;

    @Getter
    @Setter
    @Column(name = "resource_id")
    private Long resourceId;

    @Getter
    @Setter
    @Column(name = "ip_address")
    private String ipAddress;

    @Getter
    @Setter
    @Column(nullable = false)
    private Instant timestamp = Instant.now();


    public AuditLog(){}
}
