package com.colleccion.Jzkd.services;

import com.colleccion.Jzkd.criteria.ElementoCriteria;
import com.colleccion.Jzkd.dtos.ElementoDto;
import com.colleccion.Jzkd.enums.Tipo;
import com.colleccion.Jzkd.models.Elemento;
import com.colleccion.Jzkd.models.Elemento_;
import com.colleccion.Jzkd.repositories.ElementoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.service.QueryService;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ElementoService extends QueryService<Elemento> {


    @Autowired
    ElementoRepository elementoRepository;

    @Transactional
    public void crearElemento(String nombre, String obs, String descrip, Tipo tipo, boolean esta,boolean backup, String cod, Set<MultipartFile> imagenes) throws IOException {

        // Crea un muevo elemento
        Elemento elemento = new Elemento();
        elemento.setNombre(nombre);
        elemento.setObs(obs);
        elemento.setDescrip(descrip);
        elemento.setTipo(tipo);
        elemento.setEsta(esta);
        elemento.setBackup(backup);
        elemento.setCod(cod);

        String caratula = elemento.getCaratula();

        // Carga la imagen principal
        for (MultipartFile imagen : imagenes) {
            String directorioDeAlmacenamiento = "imagenes" ;

            // Crea directorio si es necesario
            File directorio = new File(directorioDeAlmacenamiento);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // Renombra la imagen
            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path rutaArchivo = Path.of(directorioDeAlmacenamiento, nombreArchivo);


            // Copia la imagen
            try (InputStream inputStream = imagen.getInputStream()) {
                Files.copy(inputStream, rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
                caratula = (nombreArchivo.toString());
            } catch (IOException e) {
                // Manejo de excepciones
                e.printStackTrace();
                throw e;
            }

            redimensionarImagen(rutaArchivo, 1000);

        }

        // Guarda el nuevo nombre de la imagen
        elemento.setCaratula(caratula);

        // Persiste el objeto
        elementoRepository.save(elemento);
    }

    @Transactional
    public void agregarImagen(Long elementoId, Set<MultipartFile> imagenes) throws IOException {

        //Busca el elemento
        Elemento elemento = elementoRepository.findById(elementoId).orElse(null);

        Set<String> imagenPaths = elemento.getImagenesPaths();
        String directorioDeAlmacenamiento = "imagenes" ;

        //Agrega la nueva imagen al elemento existente
        for (MultipartFile imagen : imagenes) {
            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path rutaArchivo = Path.of(directorioDeAlmacenamiento, nombreArchivo);

            try (InputStream inputStream = imagen.getInputStream()) {
                Files.copy(inputStream, rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
                imagenPaths.add(nombreArchivo.toString());
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
            redimensionarImagen(rutaArchivo, 1000);
        }

        elemento.setImagenesPaths(imagenPaths);
        elementoRepository.save(elemento);
    }

    private void redimensionarImagen(Path rutaArchivo, int nuevoAncho) throws IOException {

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
    public boolean existById(Long id) {
        return elementoRepository.existsById(id);
    }

    public Elemento getElemento(Long id) {
        return elementoRepository.findById(id).orElse(null);
    }

    public void save(Elemento elemento) {
        elementoRepository.save(elemento);
    }

    public List<ElementoDto> getElementosDto() {
        return elementoRepository.findAll().stream().map(elemento -> new ElementoDto(elemento)).collect(Collectors.toList());
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




