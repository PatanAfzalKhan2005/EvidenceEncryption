package com.imageseal.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="images", indexes={@Index(name="idx_image_owner_created",columnList="owner_id,created_at"),@Index(name="idx_image_sha256",columnList="sha256")})
public class ImageRecord {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private String id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="owner_id",nullable=false) private AppUser owner;
    @Column(nullable=false) private String name;
    @Column(name="mime_type",nullable=false,length=50) private String mimeType;
    @Column(name="file_size",nullable=false) private long fileSize;
    @Column(name="storage_path",nullable=false,unique=true) private String storagePath;
    @Column(nullable=false,length=64) private String sha256;
    @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
    protected ImageRecord() {}
    public ImageRecord(AppUser owner,String name,String mimeType,long fileSize,String storagePath,String sha256){this.owner=owner;this.name=name;this.mimeType=mimeType;this.fileSize=fileSize;this.storagePath=storagePath;this.sha256=sha256;}
    public String getId(){return id;} public AppUser getOwner(){return owner;} public String getName(){return name;} public String getMimeType(){return mimeType;} public long getFileSize(){return fileSize;} public String getStoragePath(){return storagePath;} public String getSha256(){return sha256;} public Instant getCreatedAt(){return createdAt;}
}
