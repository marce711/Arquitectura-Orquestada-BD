package com.umg.pedidos.controlador;

import com.umg.pedidos.entidad.pedidos;
import com.umg.pedidos.repositorio.repositorioPedidos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
public class controladorPedidos {

    private static final Logger log = LoggerFactory.getLogger(controladorPedidos.class);

    @Value("${inventario.url}")
    private String inventarioUrl;

    @Value("${pagos.url}")
    private String pagosUrl;

    @Value("${notificaciones.url}")
    private String notificacionesUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private repositorioPedidos pedidoRepository;

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Map<String, Object> body) {
        log.info("Iniciando creacion de pedido. Body={}", body);

        pedidos nuevoPedido = new pedidos();
        nuevoPedido.setProducto((String) body.get("producto"));
        nuevoPedido.setEstado("PENDIENTE");
        nuevoPedido = pedidoRepository.save(nuevoPedido);
        log.info("Pedido {} creado en estado {}", nuevoPedido.getId(), nuevoPedido.getEstado());

        try {
            reservarInventario(body);
            procesarPago(body);
            nuevoPedido.setEstado("COMPLETADO");
            log.info("Pedido {} completado tras inventario y pago", nuevoPedido.getId());
        } catch (RestClientException e) {
            compensarInventario(body);
            nuevoPedido.setEstado("CANCELADO");
            pedidoRepository.save(nuevoPedido);
            log.error("Pedido {} cancelado por fallo en inventario o pagos: {}", nuevoPedido.getId(), e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    Map.of(
                            "id", nuevoPedido.getId(),
                            "producto", nuevoPedido.getProducto(),
                            "estado", nuevoPedido.getEstado(),
                            "error", "Fallo la comunicacion con inventario o pagos",
                            "detalle", e.getMessage()
                    )
            );
        }

        try {
            enviarNotificacion(nuevoPedido);
        } catch (RestClientException e) {
            log.warn("No se pudo enviar la notificacion para el pedido {}: {}", nuevoPedido.getId(), e.getMessage(), e);
        }

        pedidoRepository.save(nuevoPedido);
        log.info("Pedido {} persistido con estado final {}", nuevoPedido.getId(), nuevoPedido.getEstado());
        return ResponseEntity.ok(nuevoPedido);
    }

    private void reservarInventario(Map<String, Object> body) {
        String url = inventarioUrl + "/inventario/reservar";
        log.info("Llamando a inventario para reservar. url={} body={}", url, body);
        String respuesta = restTemplate.postForObject(url, body, String.class);
        log.info("Respuesta de inventario reservar: {}", respuesta);
    }

    private void procesarPago(Map<String, Object> body) {
        String uuid = UUID.randomUUID().toString();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", uuid);
        HttpEntity<Map<String, Object>> requestPago = new HttpEntity<>(body, headers);
        String url = pagosUrl + "/pagos";

        log.info("Llamando a pagos. url={} idempotencyKey={} body={}", url, uuid, body);
        ResponseEntity<Map> respuesta = restTemplate.postForEntity(url, requestPago, Map.class);
        log.info("Respuesta de pagos. status={} body={}", respuesta.getStatusCode(), respuesta.getBody());
    }

    private void enviarNotificacion(pedidos pedido) {
        String url = notificacionesUrl + "/notificaciones";
        Map<String, Object> body = Map.of("id_pedido", pedido.getId(), "mensaje", "Compra exitosa");
        log.info("Enviando notificacion. url={} body={}", url, body);
        String respuesta = restTemplate.postForObject(
                url,
                body,
                String.class
        );
        log.info("Respuesta de notificaciones: {}", respuesta);
    }

    private void compensarInventario(Map<String, Object> body) {
        try {
            String url = inventarioUrl + "/inventario/liberar";
            log.info("Ejecutando compensacion en inventario. url={} body={}", url, body);
            String respuesta = restTemplate.postForObject(url, body, String.class);
            log.info("Respuesta de inventario liberar: {}", respuesta);
        } catch (RestClientException ex) {
            log.error("Error en compensacion de inventario: {}", ex.getMessage(), ex);
        }
    }
}
