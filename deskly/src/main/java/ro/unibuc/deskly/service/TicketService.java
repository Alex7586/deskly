package ro.unibuc.deskly.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import ro.unibuc.deskly.dto.CreateTicketRequest;
import ro.unibuc.deskly.dto.TicketResponse;
import ro.unibuc.deskly.entity.AuditLog;
import ro.unibuc.deskly.entity.Ticket;
import ro.unibuc.deskly.entity.User;
import ro.unibuc.deskly.repository.TicketRepository;
import ro.unibuc.deskly.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         AuditService auditService){
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    private User getAuthenticatedUser(HttpSession session){
        Object userIdObj = session.getAttribute("userId");
        if(userIdObj == null) throw new RuntimeException("User is not authenticated.");

        Long userId = (Long) userIdObj;
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private TicketResponse mapToResponse(Ticket ticket){
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getSeverity(),
                ticket.getOwner().getId(),
                ticket.getOwner().getEmail(),
                ticket.getCreatedAt().toString(),
                ticket.getUpdatedAt().toString()
        );
    }

    private Ticket getOwnedTicket(Long ticketId, HttpSession session){
        User currentUser = getAuthenticatedUser(session);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        if(!ticket.getOwner().getId().equals(currentUser.getId()))
            throw new RuntimeException("Access denied");
        return ticket;
    }

    public TicketResponse createTicket(CreateTicketRequest request,
                                       HttpSession session,
                                       String ipAddress){
        if(request.getTitle() == null || request.getTitle().isBlank())
            throw new RuntimeException("Title is required");

        User currentUser = getAuthenticatedUser(session);
        Ticket ticket = new Ticket(
                request.getTitle(),
                request.getDescription(),
                request.getStatus() == null || request.getStatus().isBlank() ? "OPEN" : request.getStatus(),
                request.getSeverity() == null || request.getSeverity().isBlank() ? "LOW" : request.getSeverity(),
                currentUser,
                Instant.now(),
                Instant.now()
        );
        Ticket savedTicket = ticketRepository.save(ticket);
        auditService.log(currentUser.getId(), "CREATE", "TICKET", savedTicket.getId(), ipAddress);
        return mapToResponse(savedTicket);
    }

    public List<TicketResponse> getMyTickets(HttpSession session){
        User currentUser = getAuthenticatedUser(session);
        return ticketRepository.findByOwner(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // cu ownership check
    public TicketResponse getTicketById(Long ticketId, HttpSession session){
        Ticket ticket = getOwnedTicket(ticketId, session);
        return mapToResponse(ticket);
    }

    // cu ownership check
    public TicketResponse updateTicket(Long ticketId,
                                       CreateTicketRequest request,
                                       HttpSession session,
                                       String ipAddress){
        User currentUser = getAuthenticatedUser(session);

        Ticket ticket = getOwnedTicket(ticketId, session);

        if(request.getTitle() != null && !request.getTitle().isBlank())
            ticket.setTitle(request.getTitle());
        if(request.getDescription() != null && !request.getDescription().isBlank())
            ticket.setDescription(request.getDescription());
        if(request.getStatus() != null && !request.getStatus().isBlank())
            ticket.setStatus(request.getStatus());
        if(request.getSeverity() != null && !request.getSeverity().isBlank())
            ticket.setSeverity(request.getSeverity());

        ticket.setUpdatedAt(Instant.now());
        Ticket updatedTicket = ticketRepository.save(ticket);
        auditService.log(currentUser.getId(), "UPDATE", "TICKET", updatedTicket.getId(), ipAddress);
        return mapToResponse(updatedTicket);
    }

    // cu ownership check
    public void deleteTicket(Long ticketId,
                             HttpSession session,
                             String ipAddress){
        User currentUser = getAuthenticatedUser(session);
        Ticket ticket = getOwnedTicket(ticketId, session);
        auditService.log(currentUser.getId(), "DELETE", "TICKET", ticket.getId(), ipAddress);
        ticketRepository.delete(ticket);
    }

    public List<TicketResponse> searchMyTickets(String query, HttpSession session){
        User currentUser = getAuthenticatedUser(session);

        if(query == null || query.isBlank()) throw new RuntimeException("Search query is required");

        return ticketRepository.findByOwnerAndTitleContainingIgnoreCase(currentUser,query)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}
