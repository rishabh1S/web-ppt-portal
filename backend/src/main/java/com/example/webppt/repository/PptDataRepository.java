package com.example.webppt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.webppt.model.PptData;

@Repository
public interface PptDataRepository extends JpaRepository<PptData, Long> {
}
