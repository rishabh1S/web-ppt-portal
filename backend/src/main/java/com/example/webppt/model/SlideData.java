package com.example.webppt.model;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Type;

import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "slide_data")
public class SlideData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb") 
    @ElementCollection
    @CollectionTable(name = "slide_texts")
    private List<Map<String, Object>> texts;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb") 
    @ElementCollection
    @CollectionTable(name = "slide_images")
    private List<Map<String, Object>> images;


}