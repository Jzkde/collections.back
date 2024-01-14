package com.colleccion.Jzkd.criteria;

import com.colleccion.Jzkd.enums.Tipo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@Getter @Setter
public class ElementoCriteria {

    public static class TipoFilter extends Filter<Tipo> {}
    private LongFilter id;
    private StringFilter nombre;
    private StringFilter obs;
    private BooleanFilter esta;
    private TipoFilter tipo;

    public ElementoCriteria() {
    }

    public ElementoCriteria(LongFilter id, StringFilter nombre, StringFilter obs, TipoFilter tipo, BooleanFilter esta) {
        this.id = id;
        this.nombre = nombre;
        this.obs = obs;
        this.tipo = tipo;
        this.esta = esta;
    }
}
