package ro.unibuc.authx.service;

import org.springframework.stereotype.Service;
import ro.unibuc.authx.entity.AuditLog;
import ro.unibuc.authx.repository.AuditLogRepository;

import java.time.Instant;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository){
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Long userId, String action, String resource, Long resourceId, String ipAddress){
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        auditLog.setResourceId(resourceId);
        auditLog.setIpAddress(ipAddress);
        auditLog.setTimestamp(Instant.now());

        System.out.println("Audit Debug");
        System.out.println("userId = " + userId);
        System.out.println("action = " + action);
        System.out.println("resource = " + resource);
        System.out.println("resourceId = " + resourceId);
        System.out.println("ipAddress = " + ipAddress);

        auditLogRepository.save(auditLog);
    }
}
