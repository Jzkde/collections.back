package com.colleccion.Jzkd.models;

import com.colleccion.Jzkd.enums.Tipo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Elemento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String obs;
    private String descrip;
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    private boolean esta;
    private String caratula;
    @ElementCollection
    private Set<String> imagenesPaths = new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disco_id")
    private Disco disco;

    public Elemento() {
    }

    public Elemento(String nombre, String obs, Tipo tipo, boolean esta, String caratula, Set<String> imagenes, String descrip) {
        this.nombre = nombre;
        this.obs = obs;
        this.tipo = tipo;
        this.esta = esta;
        this.caratula = caratula;
        this.imagenesPaths = imagenes;
        this.descrip = descrip;
    }
}
