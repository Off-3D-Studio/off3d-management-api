package com.off3d.studio.manufacturing.service;

import com.off3d.studio.auth.service.AuthService;
import com.off3d.studio.manufacturing.domain.Material;
import com.off3d.studio.manufacturing.dto.MaterialRequestDTO;
import com.off3d.studio.manufacturing.dto.MaterialResponseDTO;
import com.off3d.studio.manufacturing.repository.MaterialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final AuthService authService;

    public MaterialService(MaterialRepository materialRepository, AuthService authService) {
        this.materialRepository = materialRepository;
        this.authService = authService;
    }

    @Transactional
    public MaterialResponseDTO save(MaterialRequestDTO dto) {
        log.info("Cadastrando novo material: {} - {}", dto.name(), dto.brand());
        Material material = new Material();
        updateMaterialFromDto(material, dto);

        material.setCreatedBy(authService.getCurrentUser());

        Material savedMaterial = materialRepository.save(material);

        return mapToResponseDTO(savedMaterial);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findAll() {
        return materialRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MaterialResponseDTO findById(UUID id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado com o ID: " + id));
        return mapToResponseDTO(material);
    }

    @Transactional
    public MaterialResponseDTO update(UUID id, MaterialRequestDTO dto) {
        log.info("Atualizando material ID: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        updateMaterialFromDto(material, dto);
        return  mapToResponseDTO(materialRepository.save(material));
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Tentando excluir material ID: {}", id);
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Material não encontrado");
        }
        try {
            materialRepository.deleteById(id);
            log.info("Material {} excluido com sucesso", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Bloqueio: Material {} está vinculado a impressões e não pode ser removido", id);
            throw e;
        }
    }

    private void updateMaterialFromDto(Material material, MaterialRequestDTO dto) {
        material.setName(dto.name());
        material.setColor(dto.color());
        material.setBrand(dto.brand());
        material.setWeightGrams(dto.weightGrams());
        material.setType(dto.type());
        material.setDescription(dto.description());
    }

    private MaterialResponseDTO mapToResponseDTO(Material material) {
        return new MaterialResponseDTO(
                material.getId(),
                material.getName(),
                material.getColor(),
                material.getBrand(),
                material.getWeightGrams(),
                material.getType(),
                material.getDescription()
        );
    }
}