package com.umg.pedidos.entidad;

import jakarta.persistence.*;

@Entity

public class pedidos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String producto;
    private String estado; // PENDIENTE, COMPLETADO, CANCELADO

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
