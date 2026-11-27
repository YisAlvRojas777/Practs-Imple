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

import com.example.demo.model.Comment;
import com.example.demo.model.Producto;
import com.example.demo.repo.comentarioRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Comentarios", description = "Crear, buscar y administrar comentarios")
public class ComentarioController {

    @Autowired
    comentarioRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("comentarios")
    @Operation(summary = "Listar comentarios")
    @ApiResponse(responseCode = "200", description = "Lista de comentarios (puede estar vacía)")
    public ResponseEntity<List<Comment>> findAllComentarios() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("comentario")
    @Operation(summary = "Crear un comentario")
    @ApiResponse(responseCode = "201", description = "Comentario creado; el encabezado Location apunta a /comentario/{id}")
    public ResponseEntity<Comment> addComentario(@RequestBody Comment comentario) {
        comentario.setId(null);
        Comment saved = repo.save(comentario);
        URI location = URI.create("/comentario/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("comentario/{productId}")
    @Operation(summary = "Crear un comentario para un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comentario creado para el producto"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Comment> addComentario(@RequestBody Comment comentario, @PathVariable String productId) {
        Optional<Producto> producto = repoProducto.findById(productId);
        if (producto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        comentario.setId(null);
        comentario.setProducto(producto.get());
        Comment saved = repo.save(comentario);
        URI location = URI.create("/comentario/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("comentarios/{productId}")
    @Operation(summary = "Listar los comentarios de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comentarios del producto"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Set<Comment>> getCommentsByProduct(@PathVariable String productId) {
        Optional<Producto> producto = repoProducto.findById(productId);
        if (producto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto.get().getComentarios());
    }

    @GetMapping("comentario/{id}")
    @Operation(summary = "Buscar un comentario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comentario encontrado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el comentario")
    })
    public ResponseEntity<Comment> getById(@PathVariable String id) {
        Optional<Comment> result = repo.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.get());
    }

    @PutMapping("comentario/{id}")
    @Operation(summary = "Actualizar un comentario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comentario actualizado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el comentario; no se crea automáticamente")
    })
    public ResponseEntity<Comment> putComentario(@PathVariable String id, @RequestBody Comment nuevo) {
        Optional<Comment> result = repo.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Comment comentario = result.get();
        comentario.setUsername(nuevo.getUsername());
        comentario.setComentario_corto(nuevo.getComentario_corto());
        comentario.setEstrellas(nuevo.getEstrellas());
        comentario.setComentario_largo(nuevo.getComentario_largo());
        comentario.setImagen1(nuevo.getImagen1());
        comentario.setImagen2(nuevo.getImagen2());
        return ResponseEntity.ok(repo.save(comentario));
    }

    @DeleteMapping("comentario/{id}")
    @Operation(summary = "Eliminar un comentario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comentario eliminado, sin cuerpo de respuesta"),
        @ApiResponse(responseCode = "404", description = "No se encontró el comentario")
    })
    public ResponseEntity<Void> deleteComentario(@PathVariable String id) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
