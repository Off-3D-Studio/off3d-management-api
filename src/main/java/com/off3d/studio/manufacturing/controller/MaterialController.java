package com.off3d.studio.manufacturing.controller;

import com.off3d.studio.manufacturing.domain.Material;
import com.off3d.studio.manufacturing.dto.MaterialRequestDTO;
import com.off3d.studio.manufacturing.dto.MaterialResponseDTO;
import com.off3d.studio.manufacturing.service.MaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping
    public ResponseEntity<MaterialResponseDTO> create(@RequestBody MaterialRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materialService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<MaterialResponseDTO>> getAll() {
        return ResponseEntity.ok(materialService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(materialService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> update(@PathVariable UUID id, @RequestBody MaterialRequestDTO dto) {
        return ResponseEntity.ok(materialService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MaterialResponseDTO> delete(@PathVariable UUID id) {
        materialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}