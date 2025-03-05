package com.example.webppt.model.DTO;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SlideUpdateDTO {
    private UUID slideId;
    private List<ElementUpdateDTO> elements;
}
