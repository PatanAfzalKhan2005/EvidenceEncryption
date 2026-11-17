package com.imageseal.entity;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="audit_logs",indexes=@Index(name="idx_audit_owner_created",columnList="owner_id,created_at"))
public class AuditLog {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private String id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="owner_id",nullable=false) private AppUser owner;
 @Column(nullable=false,length=40) private String action;@Column(name="resource_id",length=36) private String resourceId;@Column(nullable=false,updatable=false)private Instant createdAt=Instant.now();
 protected AuditLog(){}public AuditLog(AppUser owner,String action,String resourceId){this.owner=owner;this.action=action;this.resourceId=resourceId;}
 public String getId(){return id;}public String getAction(){return action;}public String getResourceId(){return resourceId;}public Instant getCreatedAt(){return createdAt;}
}
