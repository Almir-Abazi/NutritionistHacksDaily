package com.nutritionisthacksdaily.repository;

import com.nutritionisthacksdaily.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findAllByOrderByDisplayOrderAsc();
}
