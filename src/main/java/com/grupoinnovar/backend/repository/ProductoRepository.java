package com.grupoinnovar.backend.repository;

import com.grupoinnovar.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;  

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByReferencia(String referencia);
    List<Producto> findBySubcategoriaId(Long subcategoriaId);
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
