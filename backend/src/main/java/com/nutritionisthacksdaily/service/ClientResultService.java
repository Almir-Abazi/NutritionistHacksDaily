package com.nutritionisthacksdaily.service;

import com.nutritionisthacksdaily.dto.ClientResultDto;
import com.nutritionisthacksdaily.entity.ClientResult;
import com.nutritionisthacksdaily.exception.ResourceNotFoundException;
import com.nutritionisthacksdaily.repository.ClientResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientResultService {

    private final ClientResultRepository clientResultRepository;

    public ClientResultService(ClientResultRepository clientResultRepository) {
        this.clientResultRepository = clientResultRepository;
    }

    @Transactional(readOnly = true)
    public List<ClientResultDto> findAll() {
        return clientResultRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientResultDto findById(Long id) {
        return clientResultRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Client result not found with id " + id));
    }

    @Transactional
    public ClientResultDto save(ClientResultDto dto) {
        ClientResult entity = toEntity(dto);
        entity.setId(null);
        return toDto(clientResultRepository.save(entity));
    }

    @Transactional
    public ClientResultDto update(Long id, ClientResultDto dto) {
        ClientResult existing = clientResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client result not found with id " + id));
        existing.setClientName(dto.getClientName());
        existing.setAge(dto.getAge());
        existing.setDiagnosis(dto.getDiagnosis());
        existing.setWeightLossKg(dto.getWeightLossKg());
        existing.setDurationMonths(dto.getDurationMonths());
        existing.setDescription(dto.getDescription());
        existing.setPhotoFilename(dto.getPhotoFilename());
        existing.setDisplayOrder(dto.getDisplayOrder());
        return toDto(clientResultRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!clientResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client result not found with id " + id);
        }
        clientResultRepository.deleteById(id);
    }

    private ClientResultDto toDto(ClientResult entity) {
        return ClientResultDto.builder()
                .id(entity.getId())
                .clientName(entity.getClientName())
                .age(entity.getAge())
                .diagnosis(entity.getDiagnosis())
                .weightLossKg(entity.getWeightLossKg())
                .durationMonths(entity.getDurationMonths())
                .description(entity.getDescription())
                .photoFilename(entity.getPhotoFilename())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private ClientResult toEntity(ClientResultDto dto) {
        return ClientResult.builder()
                .id(dto.getId())
                .clientName(dto.getClientName())
                .age(dto.getAge())
                .diagnosis(dto.getDiagnosis())
                .weightLossKg(dto.getWeightLossKg())
                .durationMonths(dto.getDurationMonths())
                .description(dto.getDescription())
                .photoFilename(dto.getPhotoFilename())
                .displayOrder(dto.getDisplayOrder())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
