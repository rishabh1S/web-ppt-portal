package com.example.webppt.model;

import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "slide_elements")
public class SlideElement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private ElementType type; // Enum: TEXT, IMAGE, SHAPE

    @Column(columnDefinition = "TEXT")
    private String content; // Text or image URL

    private double x; // X position (% of slide width)
    private double y; // Y position (% of slide height)
    private double width; // Width (% of slide)
    private double height; // Height (% of slide)

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> style;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slide_id")
    @JsonBackReference
    private Slide slide;
}