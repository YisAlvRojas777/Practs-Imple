package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Producto;
import com.example.demo.repo.productoRepo;

@RestController
public class ProductoController {
    private final productoRepo repo;

    public ProductoController(productoRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/productos")
    public List<Producto> findAllProductos() {
        return repo.findAll();
    }

    @PostMapping("/producto")
    public Producto addProducto(@RequestBody Producto producto) {
        return repo.save(producto);
    }

    @GetMapping("/producto/{id}")
    public Optional<Producto> getById(@PathVariable String id) {
        return repo.findById(id);
    }

    @PutMapping("/producto/{id}")
    public Producto putProduct(@PathVariable String id, @RequestBody Producto producto) {
        producto.setId(id);
        return repo.save(producto);
    }

    @DeleteMapping("/producto/{id}")
    public String deleteProduct(@PathVariable String id) {
        repo.deleteById(id);
        return "{\"success\":true}";
    }
}
