package com.example.webppt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.webppt.model.SlideData;

@Repository
public interface SlideDataRepository extends JpaRepository<SlideData, Long> {
}
