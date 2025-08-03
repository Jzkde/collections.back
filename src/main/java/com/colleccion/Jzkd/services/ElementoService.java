package com.colleccion.Jzkd.services;

import com.colleccion.Jzkd.criteria.ElementoCriteria;
import com.colleccion.Jzkd.dtos.ElementoDto;
import com.colleccion.Jzkd.models.Elemento;
import com.colleccion.Jzkd.models.Elemento_;
import com.colleccion.Jzkd.repositories.ElementoRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ElementoService extends QueryService<Elemento> {

    private final ElementoRepository elementoRepository;

    public ElementoService(ElementoRepository elementoRepository) {
        this.elementoRepository = elementoRepository;
    }

    String directorioDeAlmacenamiento = "imagenes";

    public void crearElemento(Elemento elemento) {
        elementoRepository.save(elemento);
    }

    public void eliminarImagenPath(Long elementoId, String imagenPath) {

        // Recupera la entidad desde la base de datos
        Elemento elemento = elementoRepository.findById(elementoId).orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        // Elimina el path de la imagen de la colección
        boolean eliminado = elemento.getImagenes().remove(imagenPath);
        if (!eliminado) {
            throw new RuntimeException("La imagen no existe en el conjunto de paths");
        }

        // Guarda la entidad actualizada en la base de datos
        elementoRepository.save(elemento);

        // Define el path completo del archivo
        Path archivoPath = Path.of(directorioDeAlmacenamiento, imagenPath);
        File file = archivoPath.toFile();

        // Intenta eliminar el archivo del sistema
        if (file.exists()) {
            if (file.delete()) {
            } else {
            }
        } else {
        }
    }

    public void redimensionarImagen(Path rutaArchivo, int nuevoAncho) throws IOException {

        //Lee la imagen
        BufferedImage imagenOriginal = ImageIO.read(rutaArchivo.toFile());

        // Calcula la nueva altura manteniendo la proporción
        int nuevoAlto = (int) Math.round((double) imagenOriginal.getHeight() / imagenOriginal.getWidth() * nuevoAncho);

        // Crea una nueva imagen redimensionada
        Image imagenRedimensionada = imagenOriginal.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
        BufferedImage imagenBuffered = new BufferedImage(nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_RGB);
        imagenBuffered.getGraphics().drawImage(imagenRedimensionada, 0, 0, null);

        // Guarda la imagen redimensionada
        ImageIO.write(imagenBuffered, "jpg", rutaArchivo.toFile());
    }

    //Metodos basicos
    public boolean existsById(Long id) {
        return elementoRepository.existsById(id);
    }

    public Elemento getElemento(Long id) {
        return elementoRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Elemento elemento) {
        elementoRepository.save(elemento);
    }

    public List<ElementoDto> getElementosDto() {
        return elementoRepository.findAll().stream().map(ElementoDto::new).collect(Collectors.toList());
    }

    public List<Elemento> getElementos() {
        return elementoRepository.findAll();
    }

    public ElementoDto getElementoDto(Long id) {
        return new ElementoDto(this.getElemento(id));
    }

    public void delete(Long id) {
        elementoRepository.deleteById(id);
    }

    public Elemento findById(Long id) {
        return elementoRepository.findById(id).orElse(null);
    }


    //Busqueda dimanica
    public List<Elemento> findByCriteria(ElementoCriteria elementoCriteria) {
        final Specification<Elemento> specification = createSpecification(elementoCriteria);
        return elementoRepository.findAll(specification);
    }

    private Specification<Elemento> createSpecification(ElementoCriteria elementoCriteria) {
        Specification<Elemento> specification = Specification.where(null);
        if (elementoCriteria != null) {
            if (elementoCriteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(elementoCriteria.getId(), Elemento_.id));
            }
            if (elementoCriteria.getNombre() != null) {
                specification = specification.and(buildStringSpecification(elementoCriteria.getNombre(), Elemento_.nombre));
            }
            if (elementoCriteria.getObs() != null) {
                specification = specification.and(buildStringSpecification(elementoCriteria.getObs(), Elemento_.obs));
            }
            if (elementoCriteria.getTipo() != null) {
                specification = specification.and(buildSpecification(elementoCriteria.getTipo(), Elemento_.tipo));
            }
            if (elementoCriteria.getEsta() != null) {
                specification = specification.and(buildSpecification(elementoCriteria.getEsta(), Elemento_.esta));
            }
            if (elementoCriteria.getBackup() != null) {
                specification = specification.and(buildSpecification(elementoCriteria.getBackup(), Elemento_.backup));
            }
            if (elementoCriteria.getBorrado() != null) {
                specification = specification.and(buildSpecification(elementoCriteria.getBorrado(), Elemento_.borrado));
            }
        }
        return specification;
    }

}




