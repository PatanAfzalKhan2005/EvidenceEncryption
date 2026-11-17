package com.imageseal.entity;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="duplicate_uploads",indexes=@Index(name="idx_duplicate_owner_created",columnList="owner_id,created_at"))
public class DuplicateUpload {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private String id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="owner_id",nullable=false) private AppUser owner;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="existing_image_id",nullable=false) private ImageRecord existingImage;
 @Column(nullable=false) private String uploadName;@Column(nullable=false,length=64) private String sha256;@Column(nullable=false,updatable=false)private Instant createdAt=Instant.now();
 protected DuplicateUpload(){}public DuplicateUpload(AppUser owner,ImageRecord image,String name,String sha){this.owner=owner;existingImage=image;uploadName=name;sha256=sha;}
 public String getId(){return id;}public String getUploadName(){return uploadName;}public String getSha256(){return sha256;}public Instant getCreatedAt(){return createdAt;}
}
