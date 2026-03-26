package proyectoBD.umg.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Inventario {

    @Id
    private String producto;
    private int stock;
    private double costo; // 🔥 nuevo campo

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
}