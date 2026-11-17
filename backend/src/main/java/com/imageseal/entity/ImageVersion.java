package com.imageseal.entity;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="image_versions",uniqueConstraints=@UniqueConstraint(name="uq_image_version_number",columnNames={"original_image_id","version_number"}),indexes=@Index(name="idx_image_version_original",columnList="original_image_id,version_number"))
public class ImageVersion {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private String id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="owner_id",nullable=false) private AppUser owner;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="original_image_id",nullable=false) private ImageRecord original;
 @Column(name="version_number",nullable=false) private int versionNumber;
 @Column(nullable=false) private String name;
 @Column(name="mime_type",nullable=false,length=50) private String mimeType;
 @Column(name="file_size",nullable=false) private long fileSize;
 @Column(name="storage_path",nullable=false,unique=true) private String storagePath;
 @Column(nullable=false,length=64) private String sha256;
 @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
 protected ImageVersion(){}
 public ImageVersion(AppUser owner,ImageRecord original,int number,String name,String mime,long size,String path,String hash){this.owner=owner;this.original=original;this.versionNumber=number;this.name=name;this.mimeType=mime;this.fileSize=size;this.storagePath=path;this.sha256=hash;}
 public String getId(){return id;} public AppUser getOwner(){return owner;} public ImageRecord getOriginal(){return original;} public int getVersionNumber(){return versionNumber;} public String getName(){return name;} public String getMimeType(){return mimeType;} public long getFileSize(){return fileSize;} public String getStoragePath(){return storagePath;} public String getSha256(){return sha256;} public Instant getCreatedAt(){return createdAt;}
}
