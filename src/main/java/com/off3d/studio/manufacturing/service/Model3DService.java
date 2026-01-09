package com.off3d.studio.manufacturing.service;

import com.off3d.studio.manufacturing.domain.Model3D;
import com.off3d.studio.manufacturing.dto.Model3DRequestDTO;
import com.off3d.studio.manufacturing.dto.Model3DResponseDTO;
import com.off3d.studio.manufacturing.repository.Model3DRepository;
import com.off3d.studio.sales.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class Model3DService {

    private final Model3DRepository modelRepository;
    private final CustomerRepository customerRepository;

    public Model3DService(Model3DRepository modelRepository, CustomerRepository customerRepository) {
        this.modelRepository = modelRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Model3DResponseDTO save(Model3DRequestDTO dto) {
        log.info("Cadastrando novo modelo 3D: {}", dto.fileName());

        var customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Model3D model = new Model3D();
        model.setFileName(dto.fileName());
        model.setFilePath(dto.filePath());
        model.setVolumeCm3(dto.volumeCm3());
        model.setCustomer(customer);

        Model3D savedModel = modelRepository.save(model);
        return mapToResponseDTO(savedModel);
    }

    @Transactional(readOnly = true)
    public List<Model3DResponseDTO> findAll() {
        return modelRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public Model3D findEntityById(UUID id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modelo 3D não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Model3DResponseDTO findByIdDetailed(UUID id) {
        return mapToResponseDTO(findEntityById(id));
    }

    @Transactional
    public Model3DResponseDTO update(UUID id, Model3DRequestDTO dto) {
        log.info("Atualizando modelo 3D ID: {}", id);

        Model3D model = modelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modelo 3D não encontrado"));

        model.setFileName(dto.fileName());
        model.setFilePath(dto.filePath());
        model.setVolumeCm3(dto.volumeCm3());

        var customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        model.setCustomer(customer);

        Model3D updatedModel = modelRepository.save(model);
        return mapToResponseDTO(updatedModel);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusão do modelo 3D ID: {}", id);

        if (!modelRepository.existsById(id)) {
            log.error("Erro: Modelo 3D {} não encontrado para exclusão", id);
            throw new RuntimeException("Modelo 3D não encontrado");
        }

        try {
            modelRepository.deleteById(id);
            log.info("Modelo 3D {} removido com sucesso", id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.warn("Falha: Modelo 3D {} possui PrintJobs vinculados e não pode ser deletado", id);
            throw e;
        }
    }

    private Model3DResponseDTO mapToResponseDTO(Model3D model) {
        return new Model3DResponseDTO(
                model.getId(),
                model.getFileName(),
                model.getFilePath(),
                model.getVolumeCm3(),
                model.getCustomer().getId()
        );
    }
}