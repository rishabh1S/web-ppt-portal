package com.example.aspose.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aspose.model.domain.Template;

public interface TemplateRepository extends JpaRepository<Template, Long> {
}