package com.nutritionisthacksdaily.service;

import com.nutritionisthacksdaily.dto.CertificateDto;
import com.nutritionisthacksdaily.entity.Certificate;
import com.nutritionisthacksdaily.exception.ResourceNotFoundException;
import com.nutritionisthacksdaily.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    @Transactional(readOnly = true)
    public List<CertificateDto> findAll() {
        return certificateRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CertificateDto findById(Long id) {
        return certificateRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id " + id));
    }

    @Transactional
    public CertificateDto save(CertificateDto dto) {
        Certificate entity = toEntity(dto);
        entity.setId(null);
        return toDto(certificateRepository.save(entity));
    }

    @Transactional
    public CertificateDto update(Long id, CertificateDto dto) {
        Certificate existing = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id " + id));
        existing.setTitle(dto.getTitle());
        existing.setIssuer(dto.getIssuer());
        existing.setYear(dto.getYear());
        existing.setImageFilename(dto.getImageFilename());
        existing.setDisplayOrder(dto.getDisplayOrder());
        return toDto(certificateRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!certificateRepository.existsById(id)) {
            throw new ResourceNotFoundException("Certificate not found with id " + id);
        }
        certificateRepository.deleteById(id);
    }

    private CertificateDto toDto(Certificate entity) {
        return CertificateDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .issuer(entity.getIssuer())
                .year(entity.getYear())
                .imageFilename(entity.getImageFilename())
                .displayOrder(entity.getDisplayOrder())
                .build();
    }

    private Certificate toEntity(CertificateDto dto) {
        return Certificate.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .issuer(dto.getIssuer())
                .year(dto.getYear())
                .imageFilename(dto.getImageFilename())
                .displayOrder(dto.getDisplayOrder())
                .build();
    }
}
