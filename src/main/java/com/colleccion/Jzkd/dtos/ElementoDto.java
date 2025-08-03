package com.colleccion.Jzkd.dtos;

import com.colleccion.Jzkd.enums.Tipo;
import com.colleccion.Jzkd.models.Elemento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class ElementoDto {
    private Long id;
    private String nombre;
    private String obs;
    private String descrip;
    private Tipo tipo;
    private boolean esta;
    private boolean backup;
    private String caratula;
    private String cod;
    private boolean borrado;

    private Set<ImagenDto> imagenes = new HashSet<>();

    public ElementoDto() {
    }

    public ElementoDto(Elemento elemento) {
        this.id = elemento.getId();
        this.nombre = elemento.getNombre();
        this.obs = elemento.getObs();
        this.descrip = elemento.getDescrip();
        this.tipo = elemento.getTipo();
        this.esta = elemento.isEsta();
        this.backup = elemento.isBackup();
        this.caratula = elemento.getCaratula();
        this.cod = elemento.getCod();
        this.borrado = elemento.isBorrado();

        if (elemento.getImagenes() != null && !elemento.getImagenes().isEmpty()) {
            this.imagenes = elemento
                    .getImagenes()
                    .stream()
                    .map(ImagenDto::new)
                    .collect(Collectors.toSet());
        }
    }
}