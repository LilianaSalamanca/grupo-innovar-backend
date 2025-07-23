package com.grupoinnovar.backend.service;

import com.grupoinnovar.backend.model.Pago;
import com.grupoinnovar.backend.model.Pedido;
import com.grupoinnovar.backend.repository.PagoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class WompiService {

    @Value("${wompi.public-key}")
    private String wompiPublicKey;

    @Value("${wompi.private-key}")
    private String wompiPrivateKey;

    @Value("${wompi.url}")
    private String wompiEndpoint;

    @Value("${wompi.integrity-secret}")
    private String wompiIntegritySecret;  // Usado para la firma de integridad

    @Value("${app.base-url}")
    private String baseUrl;

    private final PagoRepository pagoRepository;

    public WompiService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @PostConstruct
    private void init() {
        log.info("🔑 WompiService iniciado con:");
        log.info(" - Public Key: {}", wompiPublicKey);
        log.info(" - Private Key: {}", wompiPrivateKey != null ? "********" : "NO DEFINIDA");
        log.info(" - Integrity Secret: {}", wompiIntegritySecret != null ? "********" : "NO DEFINIDO");
        log.info(" - Endpoint: {}", wompiEndpoint);
        log.info(" - Base URL: {}", baseUrl);
    }

    /**
     * Prepara el pago para Wompi (crear la URL de checkout).
     */
    public Pago prepararPagoWompi(Pedido pedido, String redirectUrl) {
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMonto(pedido.getTotal() != null ? pedido.getTotal() : BigDecimal.ZERO);
        pago.setMoneda("COP");
        pago.setEstadoPago(Pago.EstadoPago.PENDIENTE);
        pago.setMetodoPago(Pago.MetodoPago.WOMPI);
        pago.setReferencia(pedido.getId().toString()); // La referencia que usaremos en el widget

        String checkoutUrl = wompiEndpoint + "/checkout/" + pedido.getId();
        pago.setCheckoutUrl(checkoutUrl);

        log.info("💳 Pago preparado para pedido ID {} - URL: {}", pedido.getId(), checkoutUrl);
        return pagoRepository.save(pago);
    }

    /**
     * Genera la firma de integridad para el widget Wompi:
     * SHA256( amountInCents + currency + reference + integritySecret )
     */
    public String generarFirma(long amountInCents, String currency, String reference) {
        String base = amountInCents + currency + reference + wompiIntegritySecret;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generando firma", e);
        }
    }

    /**
     * Confirmar el pago con la transacción de Wompi.
     */
    public boolean confirmarPago(String transactionId) {
        log.info("🔍 Validando transacción Wompi con ID: {}", transactionId);

        // Lógica simulada: aprueba si comienza con "APRO"
        boolean aprobado = transactionId != null && transactionId.toUpperCase().startsWith("APRO");
        log.info("Resultado validación Wompi para {}: {}", transactionId, aprobado ? "APROBADO" : "RECHAZADO");

        return aprobado;
    }

    public String getPublicKey() {
        return wompiPublicKey;
    }

    public String generarRedirectUrl() {
        return baseUrl + "/pago/wompi/retorno";
    }
}
