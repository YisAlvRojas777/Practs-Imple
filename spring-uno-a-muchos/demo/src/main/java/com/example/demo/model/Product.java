package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "producto")
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter
    private String id;

    private String nombre;
    private String descripcion;
    private String lote;
    private BigDecimal precioUnitario;
    private LocalDate fechaCaducidad;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL,
               fetch = FetchType.EAGER)
    @JsonManagedReference
    @Setter
    private Set<Comment> comentarios;
}
