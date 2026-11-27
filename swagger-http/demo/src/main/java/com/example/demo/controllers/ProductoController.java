package com.example.demo.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Producto;
import com.example.demo.repo.productoRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Productos", description = "Crear, buscar y administrar productos")
public class ProductoController {

    @Autowired
    productoRepo repo;

    @GetMapping("productos")
    @Operation(summary = "Listar productos")
    @ApiResponse(responseCode = "200", description = "Lista de productos (puede estar vacía)")
    public ResponseEntity<List<Producto>> findAllProductos() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping("producto")
    @Operation(summary = "Crear un producto")
    @ApiResponse(responseCode = "201", description = "Producto creado; el encabezado Location apunta a /producto/{id}")
    public ResponseEntity<Producto> addProducto(@RequestBody Producto producto) {
        producto.setId(null); // el ID siempre lo genera el servidor
        Producto saved = repo.save(producto);
        URI location = URI.create("/producto/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("producto/{id}")
    @Operation(summary = "Buscar un producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Producto> getById(@PathVariable String id) {
        Optional<Producto> result = repo.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.get());
    }

    @PutMapping("producto/{id}")
    @Operation(summary = "Actualizar un producto existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto; no se crea automáticamente")
    })
    public ResponseEntity<Producto> putProduct(@PathVariable String id, @RequestBody Producto nuevo) {
        Optional<Producto> result = repo.findById(id);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Producto producto = result.get();
        producto.setNombre(nuevo.getNombre());
        producto.setDescripcion(nuevo.getDescripcion());
        producto.setLote(nuevo.getLote());
        producto.setPrecioUnitario(nuevo.getPrecioUnitario());
        producto.setFechaCaducidad(nuevo.getFechaCaducidad());
        return ResponseEntity.ok(repo.save(producto));
    }

    @DeleteMapping("producto/{id}")
    @Operation(summary = "Eliminar un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado, sin cuerpo de respuesta"),
        @ApiResponse(responseCode = "404", description = "No se encontró el producto")
    })
    public ResponseEntity<Void> deleteProducto(@PathVariable String id) {
        if (repo.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
