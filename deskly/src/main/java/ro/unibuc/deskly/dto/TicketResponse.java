package ro.unibuc.deskly.dto;

import lombok.Getter;
import lombok.Setter;
import ro.unibuc.deskly.entity.Ticket;

public class TicketResponse {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private String severity;
    @Getter
    @Setter
    private Long ownerId;
    @Getter
    @Setter
    private String ownerEmail;
    @Getter
    @Setter
    private String createdAt;
    @Getter
    @Setter
    private String updatedAt;

    public TicketResponse(){}
    public TicketResponse(Long id, String title, String description, String status, String severity, Long ownerId,
                          String createdAt, String ownerEmail, String updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.severity = severity;
        this.ownerId = ownerId;
        this.ownerEmail = ownerEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
