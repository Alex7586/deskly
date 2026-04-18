package ro.unibuc.deskly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String title;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Getter
    @Setter
    @Column(nullable = false)
    private String status;

    @Getter
    @Setter
    @Column(nullable = false)
    private String severity;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Getter
    @Setter
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Getter
    @Setter
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();


    public Ticket(){}

    public Ticket(String title, String description, String status,
                  String severity, User owner, Instant createdAt, Instant updatedAt) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.severity = severity;
        this.owner = owner;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
