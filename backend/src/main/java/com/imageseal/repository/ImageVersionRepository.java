package com.imageseal.repository;
import com.imageseal.entity.ImageVersion;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface ImageVersionRepository extends JpaRepository<ImageVersion,String>{List<ImageVersion> findAllByOriginalIdAndOwnerIdOrderByVersionNumberAsc(String originalId,String ownerId);Optional<ImageVersion> findByIdAndOwnerId(String id,String ownerId);}
