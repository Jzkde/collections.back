package com.colleccion.Jzkd.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Disco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private int estuche;
    private int posicion;
    @OneToMany(mappedBy = "disco", fetch = FetchType.EAGER)
    private Set<Elemento> elementos = new HashSet<>();

    public Disco() {
    }

    public Disco(String nombre, int estuche, int posicion) {
        this.nombre = nombre;
        this.estuche = estuche;
        this.posicion = posicion;
    }

    public void addElemento(Elemento elemento) {
        elemento.setDisco(this);
        elementos.add(elemento);
    }
}
