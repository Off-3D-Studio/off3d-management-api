package com.off3d.studio.manufacturing.service;

import com.off3d.studio.auth.service.AuthService;
import com.off3d.studio.infra.exceptions.BusinessException;
import com.off3d.studio.infra.exceptions.ResourceNotFoundException;
import com.off3d.studio.manufacturing.domain.Model3D;
import com.off3d.studio.manufacturing.dto.Model3DRequestDTO;
import com.off3d.studio.manufacturing.dto.Model3DResponseDTO;
import com.off3d.studio.manufacturing.repository.Model3DRepository;
import com.off3d.studio.sales.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.off3d.studio.infra.config.ErrorMessages.CUSTOMER_NOT_FOUND;
import static com.off3d.studio.infra.config.ErrorMessages.MODEL_NOT_FOUND;

@Slf4j
@Service
public class Model3DService {

    private final Model3DRepository modelRepository;
    private final CustomerRepository customerRepository;
    private final AuthService authService;

    public Model3DService(Model3DRepository modelRepository, CustomerRepository customerRepository, AuthService authService) {
        this.modelRepository = modelRepository;
        this.customerRepository = customerRepository;
        this.authService = authService;
    }

    @Transactional
    public Model3DResponseDTO save(Model3DRequestDTO dto) {
        log.info("Cadastrando novo modelo 3D: {}", dto.fileName());

        var customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND + dto.customerId()));

        Model3D model = new Model3D();
        model.setFileName(dto.fileName());
        model.setFilePath(dto.filePath());
        model.setVolumeCm3(dto.volumeCm3());
        model.setCustomer(customer);
        model.setCreatedBy(authService.getCurrentUser());

        return mapToResponseDTO(modelRepository.save(model));
    }

    @Transactional(readOnly = true)
    public List<Model3DResponseDTO> findAll() {
        return modelRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public Model3D findEntityById(UUID id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MODEL_NOT_FOUND + id));
    }

    @Transactional(readOnly = true)
    public Model3DResponseDTO findByIdDetailed(UUID id) {
        return mapToResponseDTO(findEntityById(id));
    }

    @Transactional
    public Model3DResponseDTO update(UUID id, Model3DRequestDTO dto) {
        log.info("Atualizando modelo 3D ID: {}", id);

        Model3D model = findEntityById(id);

        var customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND + dto.customerId()));

        model.setFileName(dto.fileName());
        model.setFilePath(dto.filePath());
        model.setVolumeCm3(dto.volumeCm3());
        model.setCustomer(customer);

        return mapToResponseDTO(modelRepository.save(model));
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusão do modelo 3D ID: {}", id);

        Model3D model = findEntityById(id);

        try {
            modelRepository.delete(model);
            log.info("Modelo 3D {} removido com sucesso", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha: Modelo 3D {} possui vínculos ativos", id);
            throw new BusinessException("O modelo 3D possui registros vinculados (trabalhos de impressão) e não pode ser deletado.");
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