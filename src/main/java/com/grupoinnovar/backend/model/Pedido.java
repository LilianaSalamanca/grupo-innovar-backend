package com.grupoinnovar.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    /* ------------ ENUMS ------------ */

    public enum EstadoPedido {
        PENDIENTE,
        PROCESANDO,
        ENVIADO,
        COMPLETADO,
        CANCELADO
    }

    public enum MetodoPago {
        WOMPI,
        CONTRA_ENTREGA
    }

    /* ------------ CAMPOS ------------ */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Cliente que hizo el pedido (puede ser invitado). */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pedido_usuario"))
    private Usuario usuario;

    /* Detalles / líneas del pedido */
    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DetallePedido> detalles = new ArrayList<>();

    /* Direcciones de envío copiadas del usuario en el momento del pedido. */
    @Column(name = "direccion_envio", length = 200, nullable = false)
    private String direccionEnvio;

    @Column(name = "ciudad_envio", length = 100, nullable = false)
    private String ciudadEnvio;

    @Column(name = "departamento_envio", length = 100, nullable = false)
    private String departamentoEnvio;

    @Column(name = "codigo_postal_envio", length = 20)
    private String codigoPostalEnvio;

    /* Totales monetarios */
    @Column(name = "subtotal", precision = 15, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "costo_envio", precision = 15, scale = 2, nullable = false)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(name = "total", precision = 15, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido", length = 20, nullable = false)
    private EstadoPedido estadoPedido = EstadoPedido.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 20, nullable = false)
    private MetodoPago metodoPago = MetodoPago.CONTRA_ENTREGA;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Column(name = "total_descuento", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalDescuento = BigDecimal.ZERO;

    /* ------------ LIFECYCLE ------------ */

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaCreacion = now;
        this.fechaActualizacion = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    /* ------------ CONVENIENCE METHODS ------------ */

    public void addDetalle(DetallePedido det) {
        detalles.add(det);
        det.setPedido(this);
    }

    public void removeDetalle(DetallePedido det) {
        detalles.remove(det);
        det.setPedido(null);
    }

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Pago pago;

    /* ------------ GETTERS / SETTERS ------------ */

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario; 
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles.clear();
        if (detalles != null) {
            for (DetallePedido d : detalles) {
                addDetalle(d);
            }
        }
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getCiudadEnvio() {
        return ciudadEnvio;
    }

    public void setCiudadEnvio(String ciudadEnvio) {
        this.ciudadEnvio = ciudadEnvio;
    }

    public String getDepartamentoEnvio() {
        return departamentoEnvio;
    }

    public void setDepartamentoEnvio(String departamentoEnvio) {
        this.departamentoEnvio = departamentoEnvio;
    }

    public String getCodigoPostalEnvio() {
        return codigoPostalEnvio;
    }

    public void setCodigoPostalEnvio(String codigoPostalEnvio) {
        this.codigoPostalEnvio = codigoPostalEnvio;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) { 
        this.subtotal = subtotal; 
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) { 
        this.costoEnvio = costoEnvio; 
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) { 
        this.total = total; 
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public BigDecimal getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(BigDecimal totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public Pago getPago() { return pago;}
    public void setPago(Pago pago) {
        this.pago = pago;
        if (pago != null) {
            pago.setPedido(this);
        }
    }
}