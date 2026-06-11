package com.nutritionisthacksdaily.controller;

import com.nutritionisthacksdaily.dto.ClientResultDto;
import com.nutritionisthacksdaily.service.ClientResultService;
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
@RequestMapping("/api/v1/results")
public class ClientResultController {

    private final ClientResultService clientResultService;

    public ClientResultController(ClientResultService clientResultService) {
        this.clientResultService = clientResultService;
    }

    @GetMapping
    public ResponseEntity<List<ClientResultDto>> findAll() {
        return ResponseEntity.ok(clientResultService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResultDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientResultService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ClientResultDto> create(@RequestBody ClientResultDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientResultService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResultDto> update(@PathVariable Long id, @RequestBody ClientResultDto dto) {
        return ResponseEntity.ok(clientResultService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
