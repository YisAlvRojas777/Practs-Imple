package com.example.demo.producto;

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
import com.example.demo.repo.ProductoRepo;

@RestController
public class ProductoController {
    private final ProductoRepo repo;

    public ProductoController(ProductoRepo repo) {
        this.repo = repo;
    }

    // Lista de productos
    @GetMapping("/productos")
    public List<Producto> findAllProductos() {
        return repo.findAll();
    }

    // POST: registrar un producto nuevo
    @PostMapping("/producto")
    public Producto addProducto(@RequestBody Producto producto) {
        return repo.save(producto);
    }

    // GET: consultar un producto por id
    @GetMapping("/producto/{id}")
    public Optional<Producto> getById(@PathVariable Long id) {
        return repo.findById(id);
    }

    // PUT: actualizar un producto existente
    @PutMapping("/producto/{id}")
    public Producto putProduct(@PathVariable Long id, @RequestBody Producto producto) {
        producto.setId(id);
        return repo.save(producto);
    }

    // DELETE: eliminar un producto
    @DeleteMapping("/producto/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        repo.deleteById(id);
        return "{\"success\":true}";
    }
}
