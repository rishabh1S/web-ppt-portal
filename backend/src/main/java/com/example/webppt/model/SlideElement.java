package com.example.webppt.model;

import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.webppt.utils.StyleConverter;
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
import jakarta.persistence.Transient;
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

    private ElementType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> content;

    private double x;
    private double y;
    private double width;
    private double height;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> style;

    @Transient
    private Map<String, String> cssStyle;

    public Map<String, String> getCssStyle() {
        if (cssStyle == null && style != null) {
            cssStyle = StyleConverter.toCssStyle(style);
        }
        return cssStyle;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slide_id")
    @JsonBackReference
    private Slide slide;
}