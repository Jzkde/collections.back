package com.colleccion.Jzkd.dtos;

import com.colleccion.Jzkd.enums.Tipo;
import com.colleccion.Jzkd.models.Elemento;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ElementoDto {
    private Long id;
    private String nombre;
    private String obs;
    private String descrip;
    private Tipo tipo;
    private boolean esta;
    private String caratula;
    private Set<String> imagenesPaths = new HashSet<>();

    public ElementoDto() {
    }

    public ElementoDto(Elemento elemento) {
        this.id = elemento.getId();
        this.nombre = elemento.getNombre();
        this.obs = elemento.getObs();
        this.descrip=elemento.getDescrip();
        this.tipo = elemento.getTipo();
        this.esta = elemento.isEsta();
        this.caratula = elemento.getCaratula();
        this.imagenesPaths = elemento.getImagenesPaths();
    }
}
