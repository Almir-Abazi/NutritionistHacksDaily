package com.nutritionisthacksdaily.repository;

import com.nutritionisthacksdaily.entity.ClientResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientResultRepository extends JpaRepository<ClientResult, Long> {

    List<ClientResult> findAllByOrderByDisplayOrderAsc();
}
