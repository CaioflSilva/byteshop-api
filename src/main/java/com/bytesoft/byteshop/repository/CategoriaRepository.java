package com.bytesoft.byteshop.repository;

import com.bytesoft.byteshop.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNome(String nome);
    Optional<Categoria> findByNomeIgnoreCase(String nome);
}