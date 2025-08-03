package com.colleccion.Jzkd.services;

import com.colleccion.Jzkd.models.Elemento;
import com.colleccion.Jzkd.models.Imagen;
import com.colleccion.Jzkd.repositories.ImagenRepository;
import org.springframework.stereotype.Service;

@Service
public class ImagenService {

    private final ImagenRepository imagenRepository;

    public ImagenService(ImagenRepository imagenRepository) {
        this.imagenRepository = imagenRepository;
    }

    public Imagen save(Imagen imagen) {
        return imagenRepository.save(imagen);
    }

    public Imagen getImagen(Long id) {
        return imagenRepository.findById(id).orElse(null);
    }

    public void delete (Long id) {
         imagenRepository.deleteById(id);
    }
}
