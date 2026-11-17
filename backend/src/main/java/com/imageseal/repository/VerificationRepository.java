package com.imageseal.repository;
import com.imageseal.entity.VerificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface VerificationRepository extends JpaRepository<VerificationRecord,String> { List<VerificationRecord> findAllByOwnerIdOrderByCreatedAtDesc(String ownerId); void deleteAllByImageId(String imageId); }
