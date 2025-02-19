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
@Table(name = "annotations")
public class Annotation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private AnnotationType type; // Enum: TEXT, FREEHAND, ARROW

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> data; // { "points": [[x1,y1], [x2,y2]], "color": "#ff0000" }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slide_id")
    @JsonBackReference
    private Slide slide;
}
