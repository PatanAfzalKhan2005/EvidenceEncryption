package com.imageseal.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="verifications",indexes=@Index(name="idx_verification_owner_created",columnList="owner_id,created_at"))
public class VerificationRecord {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private String id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="owner_id",nullable=false) private AppUser owner;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="image_id",nullable=false) private ImageRecord image;
    @Column(nullable=false) private String checkName;
    @Column(nullable=false) private String checkStoragePath;
    @Column(nullable=false,length=50) private String checkMimeType;
    @Column(nullable=false,length=64) private String originalSha256;
    @Column(nullable=false,length=64) private String checkSha256;
    @Column(nullable=false) private boolean matched;
    @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
    protected VerificationRecord() {}
    public VerificationRecord(AppUser owner,ImageRecord image,String checkName,String checkStoragePath,String checkMimeType,String originalSha256,String checkSha256,boolean matched){this.owner=owner;this.image=image;this.checkName=checkName;this.checkStoragePath=checkStoragePath;this.checkMimeType=checkMimeType;this.originalSha256=originalSha256;this.checkSha256=checkSha256;this.matched=matched;}
    public String getId(){return id;} public AppUser getOwner(){return owner;} public ImageRecord getImage(){return image;} public String getCheckName(){return checkName;} public String getCheckStoragePath(){return checkStoragePath;} public String getCheckMimeType(){return checkMimeType;} public String getOriginalSha256(){return originalSha256;} public String getCheckSha256(){return checkSha256;} public boolean isMatched(){return matched;} public Instant getCreatedAt(){return createdAt;}
}
