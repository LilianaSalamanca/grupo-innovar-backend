package com.grupoinnovar.backend.controller;

import com.grupoinnovar.backend.model.Producto;
import com.grupoinnovar.backend.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {
    
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<Producto> buscar(@RequestParam String q) {
        return productoRepository.findByNombreContainingIgnoreCase(q);
    }

    @GetMapping("/categoria/{id}")
    public List<Producto> porCategoria(@PathVariable Long id) {
        return productoRepository.findByCategoriaId(id);
    }

    @GetMapping("/subcategoria/{id}")
    public List<Producto> porSubcategoria(@PathVariable Long id) {
        return productoRepository.findBySubcategoriaId(id);
    }
    
}
