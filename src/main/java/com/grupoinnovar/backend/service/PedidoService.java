package com.grupoinnovar.backend.service;

import com.grupoinnovar.backend.dto.PedidoDetalleDTO;
import com.grupoinnovar.backend.dto.PedidoResponseDTO;
import com.grupoinnovar.backend.model.Pedido;
import com.grupoinnovar.backend.model.Usuario;
import com.grupoinnovar.backend.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // Obtener todos los pedidos
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    // Obtener pedido por ID
    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con ID: " + id));
    }

    // Guardar un nuevo pedido
    public Pedido crearPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    // Actualizar un pedido existente
    public Pedido actualizarPedido(Long id, Pedido pedidoActualizado) {
        Pedido pedido = obtenerPorId(id);
        pedido.setEstadoPedido(pedidoActualizado.getEstadoPedido());
        pedido.setMetodoPago(pedidoActualizado.getMetodoPago());
        pedido.setDetalles(pedidoActualizado.getDetalles());
        pedido.setDireccionEnvio(pedidoActualizado.getDireccionEnvio());
        pedido.setCiudadEnvio(pedidoActualizado.getCiudadEnvio());
        pedido.setDepartamentoEnvio(pedidoActualizado.getDepartamentoEnvio());
        pedido.setCodigoPostalEnvio(pedidoActualizado.getCodigoPostalEnvio());
        pedido.setSubtotal(pedidoActualizado.getSubtotal());
        pedido.setCostoEnvio(pedidoActualizado.getCostoEnvio());
        pedido.setTotal(pedidoActualizado.getTotal());
        return pedidoRepository.save(pedido);
    }

    // Eliminar un pedido por ID
    public void eliminarPedido(Long id) {
        Pedido pedido = obtenerPorId(id);
        pedidoRepository.delete(pedido);
    }

    // Buscar pedidos por usuario
    public List<Pedido> obtenerPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }

    // Buscar pedidos por estado
    public List<Pedido> obtenerPorEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstadoPedido(estado);
    }

    // Buscar pedidos por email
    public List<PedidoResponseDTO> obtenerPedidosPorEmail(String email) {
        List<Pedido> pedidos = pedidoRepository.findByUsuarioEmail(email);
        return pedidos.stream()
                .map(this::mapToDto)
                .toList();
    }

    // ------------------- Mapeo Pedido -> PedidoResponseDTO -------------------
    private PedidoResponseDTO mapToDto(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setEstadoPedido(pedido.getEstadoPedido().name());
        dto.setMetodoPago(pedido.getMetodoPago().name());
        dto.setDireccionEnvio(pedido.getDireccionEnvio());
        dto.setCiudadEnvio(pedido.getCiudadEnvio());
        dto.setDepartamentoEnvio(pedido.getDepartamentoEnvio());
        dto.setCodigoPostalEnvio(pedido.getCodigoPostalEnvio());
        dto.setSubtotal(pedido.getSubtotal());
        dto.setCostoEnvio(pedido.getCostoEnvio());
        dto.setTotal(pedido.getTotal());
        dto.setFechaCreacion(pedido.getFechaCreacion());
        
        // Mapear detalles
        dto.setDetalles(
            pedido.getDetalles().stream().map(detalle -> {
                PedidoDetalleDTO detalleDTO = new PedidoDetalleDTO();
                detalleDTO.setProductoId(detalle.getProducto().getId());
                detalleDTO.setNombreProducto(detalle.getProducto().getNombre());
                detalleDTO.setImagenDestacada(detalle.getProducto().getImagenDestacada());
                detalleDTO.setCantidad(detalle.getCantidad());
                detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());
                detalleDTO.setSubtotal(detalle.getSubtotal());
                return detalleDTO;
            }).toList()
        );

        return dto;
    }
}