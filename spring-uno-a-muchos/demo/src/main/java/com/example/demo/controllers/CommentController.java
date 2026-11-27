package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Comment;
import com.example.demo.model.Product;
import com.example.demo.repo.CommentRepo;
import com.example.demo.repo.ProductRepo;

@RestController
public class CommentController {
    private final CommentRepo repo;
    private final ProductRepo repoProducto;

    public CommentController(CommentRepo repo, ProductRepo repoProducto) {
        this.repo = repo;
        this.repoProducto = repoProducto;
    }

    @GetMapping("/comentarios")
    public List<Comment> findAllComentarios() {
        return repo.findAll();
    }

    // Crea un comentario y lo asocia al producto indicado (asigna Comment.producto)
    @PostMapping("/comentario/{productId}")
    public Comment addComentario(@PathVariable String productId, @RequestBody Comment comentario) {
        Optional<Product> p = repoProducto.findById(productId);
        if (p.isEmpty()) {
            return null;
        }
        comentario.setProducto(p.get());
        return repo.save(comentario);
    }

    // Comentarios de un producto en particular
    @GetMapping("/comentarios/{productId}")
    public Set<Comment> getComentariosByProducto(@PathVariable String productId) {
        Optional<Product> p = repoProducto.findById(productId);
        return p.map(Product::getComentarios).orElse(null);
    }

    @GetMapping("/comentario/{id}")
    public Optional<Comment> getById(@PathVariable String id) {
        return repo.findById(id);
    }

    @PutMapping("/comentario/{id}")
    public Comment putComentario(@PathVariable String id, @RequestBody Comment comentario) {
        // Conserva el producto al que ya pertenecía el comentario
        repo.findById(id).ifPresent(existente -> comentario.setProducto(existente.getProducto()));
        comentario.setId(id);
        return repo.save(comentario);
    }

    @DeleteMapping("/comentario/{id}")
    public String deleteComentario(@PathVariable String id) {
        repo.deleteById(id);
        return "{\"success\":true}";
    }
}
