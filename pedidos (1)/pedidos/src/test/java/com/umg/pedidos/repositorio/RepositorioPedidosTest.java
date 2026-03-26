package com.umg.pedidos.repositorio;

import com.umg.pedidos.entidad.pedidos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RepositorioPedidosTest {

    @Autowired
    private repositorioPedidos pedidoRepository;

    @Test
    void generaIdAlGuardarUnPedido() {
        pedidos pedido = new pedidos();
        pedido.setProducto("Laptop");
        pedido.setEstado("PENDIENTE");

        pedidos guardado = pedidoRepository.save(pedido);

        assertNotNull(guardado.getId());
    }
}
