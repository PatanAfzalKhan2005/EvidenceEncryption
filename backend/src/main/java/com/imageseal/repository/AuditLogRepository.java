package com.imageseal.repository;
import com.imageseal.entity.AuditLog;import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface AuditLogRepository extends JpaRepository<AuditLog,String>{List<AuditLog> findAllByOwnerIdOrderByCreatedAtDesc(String ownerId);}
