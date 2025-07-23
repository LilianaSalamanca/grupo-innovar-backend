package com.grupoinnovar.backend.service;

import com.grupoinnovar.backend.model.DetallePedido;
import com.grupoinnovar.backend.model.Pedido;
import com.grupoinnovar.backend.repository.DetallePedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    public DetallePedidoService(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> obtenerPorPedido(Pedido pedido) {
        return detallePedidoRepository.findByPedido(pedido);
    }
}