package com.imageseal.repository;
import com.imageseal.entity.ImageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ImageRepository extends JpaRepository<ImageRecord,String> { List<ImageRecord> findAllByOwnerIdOrderByCreatedAtDesc(String ownerId); Optional<ImageRecord> findByIdAndOwnerId(String id,String ownerId); Optional<ImageRecord> findFirstByOwnerIdAndSha256(String ownerId,String sha256); }
