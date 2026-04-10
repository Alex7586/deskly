package ro.unibuc.deskly.repository;

import ro.unibuc.deskly.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
