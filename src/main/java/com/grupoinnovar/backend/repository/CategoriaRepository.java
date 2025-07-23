package com.grupoinnovar.backend.repository;

import com.grupoinnovar.backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}