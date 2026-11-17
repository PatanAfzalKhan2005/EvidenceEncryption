package com.imageseal.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="users", indexes=@Index(name="idx_users_email", columnList="email", unique=true))
public class AppUser {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private String id;
    @Column(nullable=false, unique=true, length=254) private String email;
    @Column(nullable=false) private String passwordHash;
    @Column(nullable=false, updatable=false) private Instant createdAt=Instant.now();
    protected AppUser() {}
    public AppUser(String email, String passwordHash) { this.email=email; this.passwordHash=passwordHash; }
    public String getId(){return id;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public Instant getCreatedAt(){return createdAt;}
}
