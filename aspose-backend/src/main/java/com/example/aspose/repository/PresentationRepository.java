package com.example.aspose.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aspose.model.Presentation;

public interface PresentationRepository extends JpaRepository<Presentation, UUID> {
}