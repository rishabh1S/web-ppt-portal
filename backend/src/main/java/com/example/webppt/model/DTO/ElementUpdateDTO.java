package com.example.webppt.model.DTO;

import java.util.Map;
import java.util.UUID;

import com.example.webppt.model.ElementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElementUpdateDTO {
    private UUID elementId;
    private ElementType type;
    private Map<String, Object> content;
    private Double x;
    private Double y;
    private Double width;
    private Double height;
    private Map<String, Object> style;
}
