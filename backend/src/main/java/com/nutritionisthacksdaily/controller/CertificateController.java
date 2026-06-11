package com.nutritionisthacksdaily.controller;

import com.nutritionisthacksdaily.dto.CertificateDto;
import com.nutritionisthacksdaily.service.CertificateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public ResponseEntity<List<CertificateDto>> findAll() {
        return ResponseEntity.ok(certificateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(certificateService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CertificateDto> create(@RequestBody CertificateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificateDto> update(@PathVariable Long id, @RequestBody CertificateDto dto) {
        return ResponseEntity.ok(certificateService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        certificateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
