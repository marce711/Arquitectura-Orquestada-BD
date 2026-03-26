package com.umg.pedidos.repositorio;

import com.umg.pedidos.entidad.pedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface repositorioPedidos extends JpaRepository<pedidos, Long> {
}
