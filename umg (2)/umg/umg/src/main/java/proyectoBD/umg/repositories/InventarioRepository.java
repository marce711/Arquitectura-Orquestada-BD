package proyectoBD.umg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyectoBD.umg.entity.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, String> {
}