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
    private boolean backup;
    private String caratula;
    private String cod;
    @ElementCollection
    private Set<String> imagenesPaths = new HashSet<>();

    public Elemento() {
    }

    public Elemento(String nombre, String obs, String descrip, Tipo tipo, boolean esta, boolean backup, String caratula, String cod, Set<String> imagenesPaths) {
        this.nombre = nombre;
        this.obs = obs;
        this.descrip = descrip;
        this.tipo = tipo;
        this.esta = esta;
        this.backup = backup;
        this.caratula = caratula;
        this.cod = cod;
        this.imagenesPaths = imagenesPaths;
    }
}
