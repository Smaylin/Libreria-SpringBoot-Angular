package com.srobles.crud.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String imageURl;

    @NotBlank
    private String imageId;

    public Image(String name, String imageUrl, String imageId) {
        this.name = name;
        this.imageURl = imageUrl;
        this.imageId = imageId;
    }
}
