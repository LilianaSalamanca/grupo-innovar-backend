package com.grupoinnovar.backend.service;

import com.grupoinnovar.backend.dto.CheckoutRequest;
import com.grupoinnovar.backend.dto.CheckoutResponse;
import com.grupoinnovar.backend.dto.ItemCheckout;
import com.grupoinnovar.backend.model.*;
import com.grupoinnovar.backend.model.Pago.EstadoPago;
import com.grupoinnovar.backend.model.Pedido.EstadoPedido;
import com.grupoinnovar.backend.model.Pedido.MetodoPago;
import com.grupoinnovar.backend.repository.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagoRepository pagoRepository;
    private final WompiService wompiService;

    @Value("${app.base-url}")
    private String baseUrl;

    public CheckoutService(PedidoRepository pedidoRepository,
                           ProductoRepository productoRepository,
                           UsuarioRepository usuarioRepository,
                           PagoRepository pagoRepository,
                           WompiService wompiService) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pagoRepository = pagoRepository;
        this.wompiService = wompiService;
    }

    @Transactional
    public CheckoutResponse iniciarCheckout(CheckoutRequest req) {
        Pedido pedido = procesarCheckout(req);
        Long amountInCents = pedido.getTotal().multiply(BigDecimal.valueOf(100)).longValue();
        String reference = pedido.getId().toString();
        String integritySignature = wompiService.generarFirma(amountInCents, "COP", reference);
        
        CheckoutResponse resp = new CheckoutResponse();
        resp.setPedidoId(pedido.getId());
        resp.setTotal(pedido.getTotal());
        resp.setSubtotal(pedido.getSubtotal());
        resp.setCostoEnvio(pedido.getCostoEnvio());
        resp.setEstadoPedido(pedido.getEstadoPedido().name());
        resp.setMetodoPago(pedido.getMetodoPago().name());
        resp.setFechaCreacion(pedido.getFechaCreacion().toString());
        resp.setAmountInCents(amountInCents);
        resp.setReference(reference);
        resp.setIntegritySignature(integritySignature);

        // Si el pago es por WOMPI, genera la URL del widget
        if (pedido.getMetodoPago() == MetodoPago.WOMPI && pedido.getPago() != null) {
            resp.setWompiUrl(pedido.getPago().getCheckoutUrl());
            resp.setWompiPublicKey(wompiService.getPublicKey());
        }

        return resp;
    }

    @Transactional
    public Pedido procesarCheckout(CheckoutRequest req) {
        // ----- Usuario (existente o invitado) -----
        Usuario usuario = usuarioRepository.findByEmail(req.getEmail())
        .orElseGet(() -> {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(req.getNombre());
            nuevoUsuario.setApellido(req.getApellido());
            nuevoUsuario.setEmail(req.getEmail());
            nuevoUsuario.setTelefono(req.getTelefono());
            nuevoUsuario.setDireccion(req.getDireccion());
            nuevoUsuario.setCiudad(req.getCiudad());
            nuevoUsuario.setDepartamento(req.getDepartamento());
            nuevoUsuario.setCodigoPostal(req.getCodigoPostal());
            nuevoUsuario.setInvitado(true);
            return usuarioRepository.save(nuevoUsuario);
        });

        // ----- Método de pago -----
        MetodoPago metodo;
        if (req.getMetodoPago() == null) {
            metodo = MetodoPago.CONTRA_ENTREGA;
        } else {
            try {
                metodo = MetodoPago.valueOf(req.getMetodoPago().toUpperCase());
            } catch (IllegalArgumentException ex) {
                metodo = MetodoPago.CONTRA_ENTREGA;
            }
        }

        // ----- Construir detalles & subtotal -----
        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemCheckout item : req.getItems()) {
            Producto prod = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProductoId()));

            int cant = Math.max(item.getCantidad(), 1);

            BigDecimal precioUnit = cant >= 5 ? prod.getPrecioMayorista() : prod.getPrecioPublico();
            BigDecimal totalLinea = precioUnit.multiply(BigDecimal.valueOf(cant));
            subtotal = subtotal.add(totalLinea);

            DetallePedido det = new DetallePedido();
            det.setProducto(prod);
            det.setCantidad(cant);
            det.setPrecioUnitario(precioUnit);
            det.setSubtotal(totalLinea);
            detalles.add(det);
        }

        // ----- Envío -----
        BigDecimal costoEnvio = calcularEnvio(subtotal);

        // ----- Total -----
        BigDecimal total = subtotal.add(costoEnvio);

        // (Opcional) verificación totalFront
        if (req.getTotalFront() != null && req.getTotalFront().compareTo(total) != 0) {
            System.out.println("Advertencia: totalFront!=" + total + " front=" + req.getTotalFront());
        }

        // ----- Crear Pedido -----
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setDireccionEnvio(usuario.getDireccion());
        pedido.setCiudadEnvio(usuario.getCiudad());
        pedido.setDepartamentoEnvio(usuario.getDepartamento());
        pedido.setCodigoPostalEnvio(usuario.getCodigoPostal());
        pedido.setSubtotal(subtotal);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setTotal(total);
        pedido.setMetodoPago(metodo);
        pedido.setEstadoPedido(EstadoPedido.PENDIENTE);
        pedido.setDetalles(detalles);
        pedido = pedidoRepository.save(pedido);

        // ----- Crear registro Pago según método -----
        if (metodo == MetodoPago.WOMPI) {

            String redirectUrl = baseUrl + "/pago/wompi/retorno";
            Pago pago = wompiService.prepararPagoWompi(pedido, redirectUrl);
            pedido.setPago(pago);
        } else {
            Pago pago = new Pago();
            pago.setPedido(pedido);
            pago.setMonto(total);
            pago.setMoneda("COP");
            pago.setEstadoPago(EstadoPago.PENDIENTE);
            pago.setMetodoPago(metodo == MetodoPago.WOMPI ? Pago.MetodoPago.WOMPI : Pago.MetodoPago.CONTRA_ENTREGA);
            pagoRepository.save(pago);
            pedido.setPago(pago);
        }

        return pedido;
    }

    @Transactional
    public Pedido confirmarPagoWompi(Long pedidoId, String transactionId) {
        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró pago para el pedido: " + pedidoId));

        boolean aprobado = wompiService.confirmarPago(transactionId);

        Pedido pedido = pago.getPedido();
        if (aprobado) {
            pago.setEstadoPago(Pago.EstadoPago.APROBADO);
            pedido.setEstadoPedido(Pedido.EstadoPedido.COMPLETADO);
        } else {
            pago.setEstadoPago(Pago.EstadoPago.RECHAZADO);
            pedido.setEstadoPedido(Pedido.EstadoPedido.CANCELADO);
        }

        pagoRepository.save(pago);
        pedidoRepository.save(pedido);
        return pedido;
    }

    private BigDecimal calcularEnvio(BigDecimal subtotal) {
        BigDecimal umbralEnvioGratis = new BigDecimal("2000000");
        return subtotal.compareTo(umbralEnvioGratis) >= 0 ? BigDecimal.ZERO : new BigDecimal("15000");
    }

}
