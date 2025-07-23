package com.grupoinnovar.backend.repository;

import com.grupoinnovar.backend.model.DetallePedido;
import com.grupoinnovar.backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedido(Pedido pedido);
}
