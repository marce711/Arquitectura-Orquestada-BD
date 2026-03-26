package proyectoBD.umg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyectoBD.umg.services.InventarioService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    @Autowired
    private InventarioService service;

    // 🔹 CREAR
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        service.crear(
                (String) body.get("producto"),
                (Integer) body.get("stock"),
                Double.parseDouble(body.get("costo").toString())
        );
        return ResponseEntity.ok("Producto creado");
    }

    // 🔹 LISTAR TODO 🔥
    @GetMapping("/listar")
    public ResponseEntity<List<?>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // 🔹 RESERVAR
    @PostMapping("/reservar")
    public ResponseEntity<?> reservar(@RequestBody Map<String, Object> body) {
        service.reservar(
                (String) body.get("producto"),
                (Integer) body.get("cantidad")
        );
        return ResponseEntity.ok("Reservado");
    }

    // 🔹 LIBERAR
    @PostMapping("/liberar")
    public ResponseEntity<?> liberar(@RequestBody Map<String, Object> body) {
        service.liberar(
                (String) body.get("producto"),
                (Integer) body.get("cantidad")
        );
        return ResponseEntity.ok("Liberado");
    }
}