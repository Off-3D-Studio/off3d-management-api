package com.off3d.studio.manufacturing.repository;

import com.off3d.studio.manufacturing.domain.Model3D;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface Model3DRepository extends JpaRepository<Model3D, UUID> {
}
