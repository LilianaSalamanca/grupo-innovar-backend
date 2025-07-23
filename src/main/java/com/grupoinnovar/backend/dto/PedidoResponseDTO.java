package com.grupoinnovar.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponseDTO {

    private Long id;
    private String estadoPedido;
    private String metodoPago;

    // datos envío
    private String direccionEnvio;
    private String ciudadEnvio;
    private String departamentoEnvio;
    private String codigoPostalEnvio;

    // totales
    private BigDecimal subtotal;
    private BigDecimal costoEnvio;
    private BigDecimal total;

    private LocalDateTime fechaCreacion;

    // detalles
    private List<PedidoDetalleDTO> detalles;

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEstadoPedido() { return estadoPedido; }
    public void setEstadoPedido(String estadoPedido) { this.estadoPedido = estadoPedido; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getCiudadEnvio() { return ciudadEnvio; }
    public void setCiudadEnvio(String ciudadEnvio) { this.ciudadEnvio = ciudadEnvio; }

    public String getDepartamentoEnvio() { return departamentoEnvio; }
    public void setDepartamentoEnvio(String departamentoEnvio) { this.departamentoEnvio = departamentoEnvio; }

    public String getCodigoPostalEnvio() { return codigoPostalEnvio; }
    public void setCodigoPostalEnvio(String codigoPostalEnvio) { this.codigoPostalEnvio = codigoPostalEnvio; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<PedidoDetalleDTO> getDetalles() { return detalles; }
    public void setDetalles(List<PedidoDetalleDTO> detalles) { this.detalles = detalles; }
}