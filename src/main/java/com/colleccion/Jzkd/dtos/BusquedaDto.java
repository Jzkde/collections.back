package com.colleccion.Jzkd.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusquedaDto {
    private Long id;
    private String nombre;
    private String obs;
    private String tipo;
    private String descrip;
    private String esta;

    public BusquedaDto() {
    }

    public BusquedaDto(Long id, String nombre, String obs, String tipo, String descrip, String esta) {
        this.id = id;
        this.nombre = nombre;
        this.obs = obs;
        this.tipo = tipo;
        this.descrip = descrip;
        this.esta = esta;
    }
}
