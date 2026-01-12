package com.off3d.studio.manufacturing.controller;

import com.off3d.studio.manufacturing.dto.Model3DRequestDTO;
import com.off3d.studio.manufacturing.dto.Model3DResponseDTO;
import com.off3d.studio.manufacturing.service.Model3DService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/models-3d")
public class Model3DController {

    private final Model3DService modelService;

    public Model3DController(Model3DService modelService) {
        this.modelService = modelService;
    }

    @PostMapping
    public ResponseEntity<Model3DResponseDTO> createModel3D(@RequestBody Model3DRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(modelService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<Model3DResponseDTO>> getAll() {
        return ResponseEntity.ok(modelService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Model3DResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(modelService.findByIdDetailed(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Model3DResponseDTO> update(@PathVariable UUID id, @RequestBody Model3DRequestDTO dto) {
        return ResponseEntity.ok(modelService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        modelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}