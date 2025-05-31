package com.project.dadn.repositories;

import com.project.dadn.models.Image;
import com.project.dadn.models.Plant;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    // Tìm tất cả ảnh của một cây
    List<Image> findByPlantOrderByIdDesc(Plant plant);

    // Tìm ảnh theo plant ID với phân trang
    Page<Image> findByPlant_IdOrderByIdDesc(UUID plantId, Pageable pageable);

    // Tìm ảnh theo uploader
    Page<Image> findByUploader_IdOrderByIdDesc(UUID uploaderId, Pageable pageable);

    // Đếm số lượng ảnh của một cây
    Long countByPlant_Id(UUID plantId);

    // Tìm n ảnh mới nhất của một cây
    @Query("SELECT i FROM Image i WHERE i.plant.id = :plantId ORDER BY i.id DESC")
    List<Image> findLatestImagesByPlantId(@Param("plantId") UUID plantId, Pageable pageable);

    @Query("SELECT i FROM Image i " +
            "WHERE i.plant.id = :plantId " +
            "AND i.uploader.id = :uploaderId " +
            "AND i.id != :currentImageId " +
            "ORDER BY i.createdAt DESC " +
            "LIMIT 1")
    Optional<Image> findMostRecentImageByPlantAndUploader(
            @Param("plantId") UUID plantId,
            @Param("uploaderId") UUID uploaderId,
            @Param("currentImageId") UUID currentImageId
    );
}
