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

import com.example.demo.model.Department;
import com.example.demo.model.Producto;
import com.example.demo.repo.categoriaRepo;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Categorías", description = "Crear, buscar y administrar categorías")
public class CategoriaController {

    @Autowired
    categoriaRepo repo;

    @Autowired
    productoRepo repoProducto;

    @GetMapping("categorias")
    @Operation(summary = "Listar categorías")
    @ApiResponse(responseCode = "200", description = "Lista de categorías (puede estar vacía)")
    public ResponseEntity<List<Department>> findAllCategoria() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("categoria")
    @Operation(summary = "Crear una categoría")
    @ApiResponse(responseCode = "201", description = "Categoría creada; el encabezado Location apunta a /categoria/{id}")
    public ResponseEntity<Department> addCategoria(@RequestBody Department categoria) {
        categoria.setId(null);
        Department saved = repo.save(categoria);
        URI location = URI.create("/categoria/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("addProductoCategoria/{productId}/{categoriaId}")
    @Operation(summary = "Asociar un producto con una categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asociación creada"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto o la categoría")
    })
    public ResponseEntity<Department> addProductoCategoria(
            @PathVariable String productId,
            @PathVariable String categoriaId) {
        Optional<Producto> producto = repoProducto.findById(productId);
        Optional<Department> categoria = repo.findById(categoriaId);
        if (producto.isEmpty() || categoria.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        categoria.get().getProductos().add(producto.get());
        producto.get().getDeparments().add(categoria.get());
        return ResponseEntity.ok(repo.save(categoria.get()));
    }

    @GetMapping("categoria/producto/{productId}")
    @Operation(summary = "Listar las categorías de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categorías del producto"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Set<Department>> getDepartmentsByProduct(@PathVariable String productId) {
        Optional<Producto> producto = repoProducto.findById(productId);
        if (producto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto.get().getDeparments());
    }

    @GetMapping("categoria/{id}")
    @Operation(summary = "Buscar una categoría por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la categoría")
    })
    public ResponseEntity<Department> getById(@PathVariable String id) {
        Optional<Department> result = repo.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.get());
    }

    @PutMapping("categoria/{id}")
    @Operation(summary = "Actualizar una categoría existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
        @ApiResponse(responseCode = "404", description = "No se encontró la categoría; no se crea automáticamente")
    })
    public ResponseEntity<Department> putDepartment(@PathVariable String id, @RequestBody Department nueva) {
        Optional<Department> result = repo.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Department categoria = result.get();
        categoria.setNombre(nueva.getNombre());
        categoria.setDescripcion(nueva.getDescripcion());
        categoria.setTipo(nueva.getTipo());
        categoria.setIcono(nueva.getIcono());
        categoria.setImagenNavbar(nueva.getImagenNavbar());
        return ResponseEntity.ok(repo.save(categoria));
    }

    @DeleteMapping("categoria/{id}")
    @Operation(summary = "Eliminar una categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada, sin cuerpo de respuesta"),
        @ApiResponse(responseCode = "404", description = "No se encontró la categoría")
    })
    public ResponseEntity<Void> deleteCategoria(@PathVariable String id) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
