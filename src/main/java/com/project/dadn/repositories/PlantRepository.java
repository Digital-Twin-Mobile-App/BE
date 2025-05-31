package com.project.dadn.repositories;

import com.project.dadn.models.Image;
import com.project.dadn.models.Plant;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PlantRepository extends JpaRepository<Plant, UUID> {
    Page<Plant> findByOwner_Id(UUID ownerId, Pageable pageable);
}
