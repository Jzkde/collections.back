package com.colleccion.Jzkd.controllers;

import com.colleccion.Jzkd.criteria.ElementoCriteria;
import com.colleccion.Jzkd.dtos.BusquedaDto;
import com.colleccion.Jzkd.dtos.ElementoDto;
import com.colleccion.Jzkd.dtos.ImagenDto;
import com.colleccion.Jzkd.enums.Tipo;
import com.colleccion.Jzkd.models.Elemento;
import com.colleccion.Jzkd.models.Imagen;
import com.colleccion.Jzkd.services.ElementoService;
import com.colleccion.Jzkd.services.ImagenService;
import org.apache.commons.lang3.StringUtils;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("elemento")
@CrossOrigin
public class ElementoController {

    private final ElementoService elementoService;
    private final ImagenService imagenService;

    public ElementoController(ElementoService elementoService,
                              ImagenService imagenService) {

        this.elementoService = elementoService;
        this.imagenService = imagenService;
    }


    @PostMapping("/nuevo")
    public ResponseEntity<?> nuevo(@RequestBody ElementoDto elemento) {

        Elemento elementoN = new Elemento(
                elemento.getNombre(),
                elemento.getObs(),
                elemento.getDescrip(),
                elemento.getTipo(),
                elemento.isEsta(),
                elemento.isBackup(),
                elemento.getCod(),
                false
        );


        elementoService.save(elementoN);

        return new ResponseEntity<>(elementoN.getId(), HttpStatus.CREATED);
    }

    @PostMapping(value = "/caratula/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirImagen(@PathVariable Long id,
                                         @RequestPart("imagen") MultipartFile imagen) throws IOException {

        if (!elementoService.existsById(id)) {
            return new ResponseEntity<>("Elemento no encontrado", HttpStatus.NOT_FOUND);
        }

        Elemento elemento = elementoService.findById(id);

        String directorio = "imagenes";
        File carpeta = new File(directorio);
        if (!carpeta.exists()) carpeta.mkdirs();

        if (elemento.getCaratula() != null && !elemento.getCaratula().isEmpty()) {
            Path rutaAnterior = Path.of(directorio, elemento.getCaratula());
            File archivoAnterior = rutaAnterior.toFile();
            if (archivoAnterior.exists() && archivoAnterior.isFile()) {
                boolean eliminado = archivoAnterior.delete();
                if (!eliminado) {
                    System.err.println("⚠️ No se pudo eliminar la carátula anterior: " + archivoAnterior.getAbsolutePath());
                }
            }
        }

        String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
        Path ruta = Path.of(directorio, nombreArchivo);

        try (InputStream inputStream = imagen.getInputStream()) {
            Files.copy(inputStream, ruta, StandardCopyOption.REPLACE_EXISTING);
        }

        elemento.setCaratula(nombreArchivo);
        elementoService.save(elemento);

        return new ResponseEntity<>("CARATULA agregada con éxito: " + elemento.getId(), HttpStatus.OK);
    }

    @PostMapping("/agregar_img/{id}")
    @Transactional
    public ResponseEntity<String> agregarImagen(@PathVariable("id") Long id,
                                                @RequestParam("imagen") Set<MultipartFile> imagenes) throws IOException {

        Elemento elemento = elementoService.findById(id);


        for (MultipartFile imagen : imagenes) {
            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path rutaArchivo = Path.of("imagenes", nombreArchivo);
            Files.copy(imagen.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            elementoService.redimensionarImagen(rutaArchivo, 1000);
            Imagen imgagen = new Imagen();
            imgagen.setNombre(nombreArchivo);
            imgagen.setElemento(elemento);
            imagenService.save(imgagen);
            elemento.addImagen(imgagen);

        }

        elementoService.save(elemento);

        return new ResponseEntity<>("IMAGENES guardadas", HttpStatus.OK);
    }

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
        elemento.setBackup(editar.isBackup());
        elemento.setCod(editar.getCod());

        elementoService.save(elemento);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/filtro")
    public ResponseEntity<List<ElementoDto>> filtro(@RequestBody BusquedaDto busquedaDto) {

        ElementoCriteria elementoCriteria = createCriteria(busquedaDto);

        List<Elemento> lista = elementoService.findByCriteria(elementoCriteria);

        List<ElementoDto> listaDto = lista.stream().map(elemento -> {

            ElementoDto elementoDto = new ElementoDto();

            elementoDto.setId(elemento.getId());
            elementoDto.setNombre(elemento.getNombre());
            elementoDto.setObs(elemento.getObs());
            elementoDto.setDescrip(elemento.getDescrip());
            elementoDto.setTipo(elemento.getTipo());
            elementoDto.setEsta(elemento.isEsta());
            elementoDto.setBackup(elemento.isBackup());
            elementoDto.setCaratula(elemento.getCaratula());
            elementoDto.setCod(elemento.getCod());
            elementoDto.setBackup(elemento.isBorrado());
            Set<ImagenDto> imagenesDto = elemento.getImagenes().stream()
                    .map(ImagenDto::new)
                    .collect(Collectors.toSet());
            elementoDto.setImagenes(imagenesDto);


            return elementoDto;

        }).collect(Collectors.toList());

        return new ResponseEntity<>(listaDto, HttpStatus.OK);
    }

    @GetMapping("/uno/{id}")
    public ResponseEntity<ElementoDto> uno(@PathVariable("id") Long id) {
        if (elementoService.getElemento(id) == null) {
            return new ResponseEntity("El elemento no existe", HttpStatus.BAD_REQUEST);
        }
        ElementoDto uno = elementoService.getElementoDto(id);
        return new ResponseEntity<>(uno, HttpStatus.OK);
    }

    @GetMapping("/{imagen}")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable("imagen") String imagen) throws IOException {
        String rutaCompleta = "imagenes/" + imagen;

        // Reemplaza las barras invertidas con barras diagonales en la ruta
        rutaCompleta = rutaCompleta.replace("\\", "/");

        File imagenFile = new File(rutaCompleta);

        if (imagenFile.exists()) {
            byte[] imageBytes = Files.readAllBytes(imagenFile.toPath());

            MediaType mediaType = imagen.toLowerCase().endsWith(".png") ?
                    MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;

            return ResponseEntity.ok().contentType(mediaType).body(imageBytes);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("eliminar_img/{id}")
    @Transactional
    public ResponseEntity<String> eliminarImagen(@PathVariable Long id) {

        Imagen imagen = imagenService.getImagen(id);

        Elemento elemento = imagen.getElemento();
        elemento.getImagenes().remove(imagen);
        elementoService.save(elemento);


        Path archivoPath = Path.of("imagenes", imagen.getNombre());
        File file = archivoPath.toFile();
        if (file.exists()) {
            if (!file.delete()) {
                System.err.println("⚠️ No se pudo eliminar el archivo físico: " + archivoPath);
            }
        }

        imagenService.delete(imagen.getId());
        return null;
    }

    @DeleteMapping("/borrar/{id}")
    @Transactional
    public ResponseEntity<?> borrar(@PathVariable("id") Long id) {
        if (!elementoService.existsById(id)) {
            return new ResponseEntity("El elemento no existe", HttpStatus.NOT_FOUND);
        }
        Elemento borrado = elementoService.getElemento(id);
        if (borrado.isBorrado() == false) {
            borrado.setBorrado(true);
        } else {
            borrado.setBorrado(false);
        }
        elementoService.save(borrado);
        return new ResponseEntity("Elemento marcado como borrado", HttpStatus.OK);
    }


    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Elemento>> filtroTipo(@PathVariable("tipo") String tipo) {
        BusquedaDto busquedaDto = new BusquedaDto();
        busquedaDto.setTipo(tipo);
        ElementoCriteria pedidoCriteria = createCriteria(busquedaDto);
        List<Elemento> list = elementoService.findByCriteria(pedidoCriteria);
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
            //Esta?
            if (!StringUtils.isBlank(busqueda.getEsta())) {
                BooleanFilter filter = new BooleanFilter();
                if (busqueda.getEsta().equals("true")) {
                    filter.setEquals(true);
                } else {
                    filter.setEquals(false);
                }
                elementoCriteria.setEsta(filter);
            }
            //Backup?
            if (!StringUtils.isBlank(busqueda.getBackup())) {
                BooleanFilter filter = new BooleanFilter();
                if (busqueda.getBackup().equals("true")) {
                    filter.setEquals(true);
                } else {
                    filter.setEquals(false);
                }
                elementoCriteria.setBackup(filter);
            }
            //Borrado?
            if (!StringUtils.isBlank(busqueda.getBorrado())) {
                BooleanFilter filter = new BooleanFilter();
                if (busqueda.getBorrado().equals("false")) {
                    filter.setEquals(false);
                } else {
                    filter.setEquals(true);
                }
                elementoCriteria.setBorrado(filter);
            }
        }
        return elementoCriteria;
    }

}