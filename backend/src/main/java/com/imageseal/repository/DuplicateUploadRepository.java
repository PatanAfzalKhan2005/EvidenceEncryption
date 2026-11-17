package com.imageseal.repository;
import com.imageseal.entity.DuplicateUpload;import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface DuplicateUploadRepository extends JpaRepository<DuplicateUpload,String>{List<DuplicateUpload> findAllByOwnerIdOrderByCreatedAtDesc(String ownerId);void deleteAllByExistingImageId(String imageId);}
