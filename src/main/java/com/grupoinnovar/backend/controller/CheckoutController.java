package com.grupoinnovar.backend.controller;

import com.grupoinnovar.backend.dto.CheckoutRequest;
import com.grupoinnovar.backend.dto.CheckoutResponse;
import com.grupoinnovar.backend.model.Pedido;
import com.grupoinnovar.backend.service.CheckoutService;
import com.grupoinnovar.backend.service.WompiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para el flujo de checkout (creación de pedidos y confirmación de pago).
 */
@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*")  // Permitir peticiones desde Angular u otros frontends
public class CheckoutController {

    private final CheckoutService checkoutService;
    public CheckoutController(CheckoutService checkoutService, WompiService wompiService) {
        this.checkoutService = checkoutService;
    }

    /**
     * Inicia el proceso de checkout, creando un pedido pendiente.
     * @param req Datos enviados desde el frontend.
     * @return CheckoutResponse con datos del pedido y URL de pago (si es WOMPI).
     */
    @PostMapping("/iniciar")
    public ResponseEntity<CheckoutResponse> iniciarCheckout(@RequestBody CheckoutRequest req) {
        CheckoutResponse resp = checkoutService.iniciarCheckout(req);
        return ResponseEntity.ok(resp);
    }

    /**
     * Confirma el estado del pago de WOMPI después de la transacción.
     */
    public static record ConfirmRequest(Long pedidoId, String transactionId) {}

    @PostMapping("/wompi/confirmar")
    public ResponseEntity<Pedido> confirmarPagoWompi(@RequestBody ConfirmRequest body) {
        Pedido pedido = checkoutService.confirmarPagoWompi(body.pedidoId(), body.transactionId());
        return ResponseEntity.ok(pedido);
    }

}
