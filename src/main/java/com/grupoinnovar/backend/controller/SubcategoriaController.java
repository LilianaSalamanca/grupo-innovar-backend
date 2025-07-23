package com.grupoinnovar.backend.controller;

import com.grupoinnovar.backend.model.Subcategoria;
import com.grupoinnovar.backend.service.SubcategoriaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcategorias")
@CrossOrigin(origins = "*")
public class SubcategoriaController {
    
    private final SubcategoriaService subcategoriaService;

    public SubcategoriaController(SubcategoriaService subcategoriaService) {
        this.subcategoriaService = subcategoriaService;
    }

    @GetMapping
    public List<Subcategoria> listar() {
        return subcategoriaService.listarTodas();
    }

    @PostMapping
    public Subcategoria guardar(@RequestBody Subcategoria subcategoria) {
        return subcategoriaService.guardar(subcategoria);
    }
}
