package com.example.aspose.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aspose.model.Slide;

public interface SlideRepository extends JpaRepository<Slide, UUID> {
}
