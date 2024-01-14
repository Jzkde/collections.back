package com.colleccion.Jzkd.controllers;

import com.colleccion.Jzkd.criteria.ElementoCriteria;
import com.colleccion.Jzkd.dtos.BusquedaDto;
import com.colleccion.Jzkd.dtos.ElementoDto;
import com.colleccion.Jzkd.dtos.Mensaje;
import com.colleccion.Jzkd.enums.Tipo;
import com.colleccion.Jzkd.models.Elemento;
import com.colleccion.Jzkd.repositories.ElementoRepository;
import com.colleccion.Jzkd.services.ElementoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("elemento")
@CrossOrigin
public class ElementoController {
    @Autowired
    private ElementoService elementoService;
    private ElementoRepository elementoRepository;


    //Crea un nuevo elemento y le agrega la imagen principal
    @PostMapping(value = "/nuevo", consumes = "multipart/form-data")
    public ResponseEntity<?> nuevo(@RequestParam String nombre,
                                   @RequestParam String obs,
                                   @RequestParam String descrip,
                                   @RequestParam Tipo tipo,
                                   @RequestParam boolean esta,
                                   @RequestPart("imagen") MultipartFile imagen) {
        try {
            elementoService.crearElemento(nombre, obs, descrip, tipo, esta, new HashSet<>(Arrays.asList(imagen)));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al crear el elemento.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Agrega nuevas imágenes a un elemento existente
    @PostMapping("/agregar_img/{id}")
    @Transactional
    public ResponseEntity<String> agregarImagen(@PathVariable("id") Long id,
                                                @RequestParam("imagen") Set<MultipartFile> imagen) {
        try {
            elementoService.agregarImagen(id, imagen);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener las imagenes para visualizarlas
    @GetMapping("/{imagen}")
    public ResponseEntity<byte[]> obtenerImagen( @PathVariable("imagen") String imagen) throws IOException {
        String rutaCompleta = "imagenes/" + imagen;

        // Reemplaza las barras invertidas con barras diagonales en la ruta
        rutaCompleta = rutaCompleta.replace("\\", "/");

        File imagenFile = new File(rutaCompleta);

        if (imagenFile.exists()) {
            byte[] imageBytes = Files.readAllBytes(imagenFile.toPath());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //Obtener todos los elementos
    @GetMapping("/lista")
    public ResponseEntity<List<ElementoDto>> lista() {
        List<ElementoDto> list = elementoService.getElementosDto();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //Obtener solo un elemento
    @GetMapping("/uno/{id}")
    public ResponseEntity<ElementoDto> uno(@PathVariable("id") Long id) {
        if (elementoService.getElemento(id) == null) {
            return new ResponseEntity("El elemento no existe", HttpStatus.BAD_REQUEST);
        }
        ElementoDto uno = elementoService.getElementoDto(id);
        return new ResponseEntity<>(uno, HttpStatus.OK);
    }


    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Elemento>> filtroTipo(@PathVariable("tipo") String tipo) {
        BusquedaDto busquedaDto = new BusquedaDto();
        busquedaDto.setTipo(tipo);
        ElementoCriteria pedidoCriteria = createCriteria(busquedaDto);
        List<Elemento> list = elementoService.findByCriteria(pedidoCriteria);
        return new ResponseEntity<List<Elemento>>(list, HttpStatus.OK);
    }

    //Actualizar un elemente
    @PutMapping("/editar/{id}")
    @Transactional
    public ResponseEntity<?> editar(@PathVariable("id") Long id, @RequestBody ElementoDto editar) {
        if (elementoService.getElemento(id) == null) {
            return new ResponseEntity("El elemento no existe", HttpStatus.BAD_REQUEST);
        }
        Elemento elemento = elementoService.getElemento(id);

        elemento.setNombre(editar.getNombre());
        elemento.setObs(editar.getObs());
        elemento.setDescrip(editar.getDescrip());
        elemento.setTipo(editar.getTipo());
        elemento.setEsta(editar.isEsta());

        elementoService.save(elemento);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Eliminar un elemento
    @DeleteMapping("/borrar/{id}")
    @Transactional
    public ResponseEntity<?> borrar(@PathVariable("id") Long id) {
        if (!elementoService.existById(id)) {
            return new ResponseEntity(new Mensaje("El elemento no existe"), HttpStatus.NOT_FOUND);
        }
        elementoService.delete(id);
        return new ResponseEntity(new Mensaje("Elemento eliminado"), HttpStatus.OK);

    }

    //Busqueda dinamica
    @PostMapping("/filtro")
    public ResponseEntity<List<Elemento>> filtro(@RequestBody BusquedaDto elementoDto) {
        ElementoCriteria elementoCriteria = createCriteria(elementoDto);
        List<Elemento> list = elementoService.findByCriteria(elementoCriteria);
        return new ResponseEntity<List<Elemento>>(list, HttpStatus.OK);
    }

    private ElementoCriteria createCriteria(BusquedaDto busqueda) {
        ElementoCriteria elementoCriteria = new ElementoCriteria();
        if (busqueda != null) {
            //Id
            if (busqueda.getId() != null) {
                LongFilter filter = new LongFilter();
                filter.setEquals(busqueda.getId());
                elementoCriteria.setId(filter);
            }
            //Nombre
            if (!StringUtils.isBlank(busqueda.getNombre())) {
                StringFilter filter = new StringFilter();
                filter.setContains(busqueda.getNombre());
                elementoCriteria.setNombre(filter);
            }
            //Observaciones
            if (!StringUtils.isBlank(busqueda.getObs())) {
                StringFilter filter = new StringFilter();
                filter.setContains(busqueda.getObs());
                elementoCriteria.setObs(filter);
            }
            //Tipo
            if (!StringUtils.isBlank(busqueda.getTipo())) {
                ElementoCriteria.TipoFilter filter = new ElementoCriteria.TipoFilter();
                String tipo = busqueda.getTipo().toUpperCase();
                filter.setEquals(Tipo.valueOf(tipo));
                elementoCriteria.setTipo(filter);
            }
            //Esta
            if (!StringUtils.isBlank(busqueda.getEsta())) {
                BooleanFilter filter = new BooleanFilter();
                if (busqueda.getEsta().equals("true")) {
                    filter.setEquals(true);
                } else {
                    filter.setEquals(false);
                }
                elementoCriteria.setEsta(filter);
            }
        }
        return elementoCriteria;
    }

}