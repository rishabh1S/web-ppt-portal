package com.example.webppt.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webppt.model.Presentation;

public interface PresentationRepository extends JpaRepository<Presentation, UUID> {
}