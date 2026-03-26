package proyectoBD.umg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyectoBD.umg.entity.Inventario;
import proyectoBD.umg.repositories.InventarioRepository;

import java.util.List;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository repo;

    // 🔹 CREAR
    public void crear(String producto, int stock, double costo) {

        if (repo.existsById(producto)) {
            throw new RuntimeException("El producto ya existe");
        }

        Inventario inv = new Inventario();
        inv.setProducto(producto);
        inv.setStock(stock);
        inv.setCosto(costo);

        repo.save(inv);
    }

    // 🔹 LISTAR TODO
    public List<Inventario> listar() {
        return repo.findAll();
    }

    // 🔹 RESERVAR
    public void reservar(String producto, int cantidad) {
        Inventario inv = repo.findById(producto)
                .orElseThrow(() -> new RuntimeException("Producto no existe"));

        if (inv.getStock() < cantidad) {
            throw new RuntimeException("Sin stock");
        }

        inv.setStock(inv.getStock() - cantidad);
        repo.save(inv);
    }

    // 🔹 LIBERAR
    public void liberar(String producto, int cantidad) {
        Inventario inv = repo.findById(producto)
                .orElseThrow(() -> new RuntimeException("Producto no existe"));

        inv.setStock(inv.getStock() + cantidad);
        repo.save(inv);
    }
}