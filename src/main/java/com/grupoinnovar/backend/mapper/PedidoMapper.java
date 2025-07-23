package com.grupoinnovar.backend.mapper;

import com.grupoinnovar.backend.dto.PedidoDetalleDTO;
import com.grupoinnovar.backend.dto.PedidoResponseDTO;
import com.grupoinnovar.backend.model.DetallePedido;
import com.grupoinnovar.backend.model.Pedido;
import com.grupoinnovar.backend.model.Producto;
import java.util.List;
import java.util.stream.Collectors;

public final class PedidoMapper {

    private PedidoMapper() {}

    public static PedidoResponseDTO toDto(Pedido p) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(p.getId());
        dto.setEstadoPedido(p.getEstadoPedido().name());
        dto.setMetodoPago(p.getMetodoPago().name());
        dto.setDireccionEnvio(p.getDireccionEnvio());
        dto.setCiudadEnvio(p.getCiudadEnvio());
        dto.setDepartamentoEnvio(p.getDepartamentoEnvio());
        dto.setCodigoPostalEnvio(p.getCodigoPostalEnvio());
        dto.setSubtotal(p.getSubtotal());
        dto.setCostoEnvio(p.getCostoEnvio());
        dto.setTotal(p.getTotal());
        dto.setFechaCreacion(p.getFechaCreacion());

        List<PedidoDetalleDTO> dets = p.getDetalles()
            .stream()
            .map(PedidoMapper::toDetalleDto)
            .collect(Collectors.toList());
        dto.setDetalles(dets);

        return dto;
    }

    private static PedidoDetalleDTO toDetalleDto(DetallePedido d) {
        PedidoDetalleDTO dto = new PedidoDetalleDTO();
        Producto prod = d.getProducto();
        dto.setProductoId(prod.getId());
        dto.setNombreProducto(prod.getNombre());
        dto.setImagenDestacada(prod.getImagenDestacada());
        dto.setCantidad(d.getCantidad());
        dto.setPrecioUnitario(d.getPrecioUnitario());
        dto.setSubtotal(d.getSubtotal());
        return dto;
    }
}