package com.grupoinnovar.backend.controller;

import com.grupoinnovar.backend.dto.CheckoutRequest;
import com.grupoinnovar.backend.dto.PedidoResponseDTO;
import com.grupoinnovar.backend.mapper.PedidoMapper;
import com.grupoinnovar.backend.model.Pedido;
import com.grupoinnovar.backend.model.Usuario;
import com.grupoinnovar.backend.repository.PedidoRepository;
import com.grupoinnovar.backend.repository.UsuarioRepository;
import com.grupoinnovar.backend.service.CheckoutService;
import com.grupoinnovar.backend.service.DetallePedidoService;
import com.grupoinnovar.backend.service.PedidoService;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin // ajusta dominio
public class PedidoController {

    private final CheckoutService checkoutService;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoService pedidoService;

    public PedidoController(
            CheckoutService checkoutService,
            PedidoRepository pedidoRepository,
            DetallePedidoService detallePedidoService,
            UsuarioRepository usuarioRepository,
            PedidoService pedidoService
    ) {
        this.checkoutService = checkoutService;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoService = pedidoService;
    }

    /* Paso 2/3: checkout */
    @PostMapping("/checkout")
    public ResponseEntity<PedidoResponseDTO> checkout(@RequestBody CheckoutRequest req) {
        Pedido p = checkoutService.procesarCheckout(req);
        return ResponseEntity.ok(PedidoMapper.toDto(p));
    }

    /* Paso 3: mostrar pedido */
    @GetMapping("/pedidos/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPedido(@PathVariable Long id) {
        Pedido p = pedidoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + id));

        // Asegurar carga de detalles si LAZY
        p.getDetalles().size(); // fuerza init (o usa @EntityGraph / fetch join en repo)

        return ResponseEntity.ok(PedidoMapper.toDto(p));
    }

    @GetMapping("/usuario")
    public List<PedidoResponseDTO> getPedidosPorEmail(@RequestParam String email) {
        return pedidoService.obtenerPedidosPorEmail(email);
    }


    @GetMapping("/pedidos/usuario")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPedidosPorUsuario(@RequestParam String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con email: " + email));

        List<Pedido> pedidos = pedidoRepository.findByUsuario(usuario);

        List<PedidoResponseDTO> pedidosDto = pedidos.stream()
            .map(PedidoMapper::toDto)
            .toList();

        return ResponseEntity.ok(pedidosDto);
    }

}