package com.off3d.studio.manufacturing.service;

import com.off3d.studio.manufacturing.domain.Model3D;
import com.off3d.studio.manufacturing.dto.Model3DRequestDTO;
import com.off3d.studio.manufacturing.dto.Model3DResponseDTO;
import com.off3d.studio.manufacturing.repository.Model3DRepository;
import com.off3d.studio.sales.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
        var customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Model3D model = new Model3D();
        model.setFileName(dto.fileName());
        model.setFilePath(dto.filePath());
        model.setVolumeCm3(dto.volumeCm3());
        model.setCustomer(customer);

        Model3D savedModel = modelRepository.save(model);

        return new Model3DResponseDTO(
                savedModel.getId(),
                savedModel.getFileName(),
                savedModel.getFilePath(),
                savedModel.getVolumeCm3(),
                customer.getId()
        );
    }

    public List<Model3D> findAll() {
        return modelRepository.findAll();
    }

    public Model3D findById(UUID id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modelo 3D não encontrado: " + id));
    }

    @Transactional
    public void delete(UUID id) {
        Model3D model = findById(id);
        modelRepository.delete(model);
    }
}