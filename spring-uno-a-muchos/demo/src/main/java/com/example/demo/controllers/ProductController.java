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

import com.example.demo.model.Product;
import com.example.demo.repo.ProductRepo;

@RestController
public class ProductController {
    private final ProductRepo repo;

    public ProductController(ProductRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/productos")
    public List<Product> findAllProductos() {
        return repo.findAll();
    }

    @PostMapping("/producto")
    public Product addProducto(@RequestBody Product producto) {
        return repo.save(producto);
    }

    @GetMapping("/producto/{id}")
    public Optional<Product> getById(@PathVariable String id) {
        return repo.findById(id);
    }

    @PutMapping("/producto/{id}")
    public Product putProducto(@PathVariable String id, @RequestBody Product producto) {
        producto.setId(id);
        return repo.save(producto);
    }

    @DeleteMapping("/producto/{id}")
    public String deleteProducto(@PathVariable String id) {
        repo.deleteById(id);
        return "{\"success\":true}";
    }
}
