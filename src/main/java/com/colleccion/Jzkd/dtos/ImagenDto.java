package com.colleccion.Jzkd.dtos;

import com.colleccion.Jzkd.models.Imagen;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImagenDto {

    private Long id;
    private String nombre;

    public ImagenDto() {
    }

    public ImagenDto(Imagen imagen) {
        this.id = imagen.getId();
        this.nombre = imagen.getNombre();
    }
}
