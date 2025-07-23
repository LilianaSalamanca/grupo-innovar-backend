package com.grupoinnovar.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* El pedido dueño de esta línea */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_detalle_pedido_pedido"))
    private Pedido pedido;

    /* Producto */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_detalle_pedido_producto"))
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", precision = 15, scale = 2, nullable = false)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 15, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    /* ------------ LIFECYCLE ------------ */
    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad > 0) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    /* ------------ GETTERS / SETTERS ------------ */

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }

    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Producto getProducto() { return producto; }

    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() { return subtotal; }

    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}