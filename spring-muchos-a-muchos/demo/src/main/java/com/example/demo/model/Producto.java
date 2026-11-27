package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter
    private String id;

    private String nombre;
    private String descripcion;
    private String lote;
    private BigDecimal precioUnitario;
    private LocalDate fechaCaducidad;

    @ManyToMany(mappedBy = "productos",fetch = FetchType.EAGER)
    @JsonIgnore
    @Setter
    @Builder.Default
    private Set<Department> deparments = new HashSet<>();
}
