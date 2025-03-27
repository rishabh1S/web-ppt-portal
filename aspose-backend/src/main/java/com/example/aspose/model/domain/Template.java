package com.example.aspose.model.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "templates")
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long standardTemplateId;

    private String name;
    private String originalFilePath;
    private double width;
    private double height;
    private String slideTitle;
    private boolean isCSI;
    private String[] legalEntities;
    private String[] presenters;
    @OneToMany(mappedBy = "templates", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TemplateObject> stdObjectContent = new ArrayList<>();
}