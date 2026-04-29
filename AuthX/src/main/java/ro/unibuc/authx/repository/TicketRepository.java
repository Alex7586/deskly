package ro.unibuc.authx.repository;

import ro.unibuc.authx.entity.Ticket;
import ro.unibuc.authx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByOwner(User owner);
    List<Ticket> findByTitleContainingIgnoreCase(String title);
    List<Ticket> findByOwnerAndTitleContainingIgnoreCase(User owner, String title);
}
