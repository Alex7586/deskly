package ro.unibuc.deskly.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.deskly.dto.CreateTicketRequest;
import ro.unibuc.deskly.dto.TicketResponse;
import ro.unibuc.deskly.service.TicketService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody CreateTicketRequest request,
                                                       HttpSession session,
                                                       HttpServletRequest httpRequest){
        TicketResponse response = ticketService.createTicket(request, session, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getMyTickets(HttpSession session){
        List<TicketResponse> response = ticketService.getMyTickets(session);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id, HttpSession session){
        TicketResponse response = ticketService.getTicketById(id, session);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable Long id,
                                                       @RequestBody CreateTicketRequest request,
                                                       HttpSession session,
                                                       HttpServletRequest httpRequest){
        TicketResponse response = ticketService.updateTicket(id, request, session, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTicket(@PathVariable Long id,
                                                            HttpSession session,
                                                            HttpServletRequest httpRequest){
        ticketService.deleteTicket(id, session, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Ticket deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TicketResponse>> searchMyTickets(@RequestParam("q") String query,
                                                                HttpSession session){
        List<TicketResponse> response = ticketService.searchMyTickets(query, session);
        return ResponseEntity.ok(response);
    }
}
