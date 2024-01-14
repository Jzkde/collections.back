package com.colleccion.Jzkd.repositories;

import com.colleccion.Jzkd.models.Elemento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementoRepository extends JpaRepository<Elemento, Long>
        , JpaSpecificationExecutor<Elemento>
{
}
