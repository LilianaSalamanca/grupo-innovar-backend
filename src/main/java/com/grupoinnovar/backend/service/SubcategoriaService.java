package com.grupoinnovar.backend.service;

import com.grupoinnovar.backend.model.Subcategoria;
import com.grupoinnovar.backend.repository.SubcategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubcategoriaService {
    
    private final SubcategoriaRepository subcategoriaRepository;

    public SubcategoriaService(SubcategoriaRepository subcategoriaRepository) {
        this.subcategoriaRepository = subcategoriaRepository;
    }

    public List<Subcategoria> listarTodas() {
        return subcategoriaRepository.findAll();
    }

    public Subcategoria guardar(Subcategoria subcategoria) {
        return subcategoriaRepository.save(subcategoria);
    }
}
