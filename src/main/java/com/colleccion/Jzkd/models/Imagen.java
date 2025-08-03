package com.colleccion.Jzkd.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "elemento_id")
    private Elemento elemento;

    public Imagen() {
    }

    public Imagen(String nombre) {
        this.nombre = nombre;
    }
}
