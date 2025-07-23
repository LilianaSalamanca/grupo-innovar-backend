package com.grupoinnovar.backend.repository;

import com.grupoinnovar.backend.model.Pedido;
import com.grupoinnovar.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuario(Usuario usuario);

    List<Pedido> findByEstadoPedido(Pedido.EstadoPedido estadoPedido);

    List<Pedido> findByUsuarioEmail(String email);

}
