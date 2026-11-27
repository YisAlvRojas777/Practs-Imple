package com.example.demo.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Image;
import com.example.demo.model.Producto;
import com.example.demo.repo.imagenRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Imágenes", description = "Crear, buscar y administrar imágenes")
public class ImagenController {

    @Autowired
    imagenRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("imagenes")
    @Operation(summary = "Listar imágenes")
    @ApiResponse(responseCode = "200", description = "Lista de imágenes (puede estar vacía)")
    public ResponseEntity<List<Image>> findAllImagenes() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("imagen")
    @Operation(summary = "Crear una imagen")
    @ApiResponse(responseCode = "201", description = "Imagen creada; el encabezado Location apunta a /imagen/{id}")
    public ResponseEntity<Image> addImagen(@RequestBody Image imagen) {
        imagen.setId(null);
        Image saved = repo.save(imagen);
        URI location = URI.create("/imagen/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("imagen/{productId}")
    @Operation(summary = "Crear una imagen para un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Imagen creada para el producto"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Image> addImagen(@RequestBody Image imagen, @PathVariable String productId) {
        Optional<Producto> producto = repoProducto.findById(productId);
        if (producto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        imagen.setId(null);
        imagen.setProducto(producto.get());
        Image saved = repo.save(imagen);
        URI location = URI.create("/imagen/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    // Se usa /imagenes/producto/{productId} para no chocar con GET /imagen/{id}.
    @GetMapping("imagenes/producto/{productId}")
    @Operation(summary = "Listar las imágenes de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imágenes del producto"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Set<Image>> getImagenesByProduct(@PathVariable String productId) {
        Optional<Producto> producto = repoProducto.findById(productId);
        if (producto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto.get().getImagenes());
    }

    @GetMapping("imagen/{id}")
    @Operation(summary = "Buscar una imagen por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen encontrada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la imagen")
    })
    public ResponseEntity<Image> getById(@PathVariable String id) {
        Optional<Image> result = repo.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.get());
    }

    @PutMapping("imagen/{id}")
    @Operation(summary = "Actualizar una imagen existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen actualizada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la imagen; no se crea automáticamente")
    })
    public ResponseEntity<Image> putImagen(@PathVariable String id, @RequestBody Image nueva) {
        Optional<Image> result = repo.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Image imagen = result.get();
        imagen.setUrl(nueva.getUrl());
        imagen.setPrioridad(nueva.getPrioridad());
        imagen.setTamanio(nueva.getTamanio());
        imagen.setResponsiva(nueva.isResponsiva());
        return ResponseEntity.ok(repo.save(imagen));
    }

    @DeleteMapping("imagen/{id}")
    @Operation(summary = "Eliminar una imagen")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Imagen eliminada, sin cuerpo de respuesta"),
        @ApiResponse(responseCode = "404", description = "No se encontró la imagen")
    })
    public ResponseEntity<Void> deleteImagen(@PathVariable String id) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
