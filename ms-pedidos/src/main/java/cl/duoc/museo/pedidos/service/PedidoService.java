package cl.duoc.museo.pedidos.service;

import cl.duoc.museo.pedidos.client.CatalogoClient;
import cl.duoc.museo.pedidos.client.ProductoCatalogo;
import cl.duoc.museo.pedidos.dto.ItemRequest;
import cl.duoc.museo.pedidos.dto.PedidoRequest;
import cl.duoc.museo.pedidos.exception.RecursoNoEncontradoException;
import cl.duoc.museo.pedidos.exception.ReglaNegocioException;
import cl.duoc.museo.pedidos.model.*;
import cl.duoc.museo.pedidos.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository repositorio;
    private final CatalogoClient catalogo;
    private final CotizadorEnvio cotizador;

    public PedidoService(PedidoRepository repositorio, CatalogoClient catalogo, CotizadorEnvio cotizador) {
        this.repositorio = repositorio;
        this.catalogo = catalogo;
        this.cotizador = cotizador;
    }

    /*
     * Crear una compra:
     * 1. Se juntan las cantidades si el mismo producto viene repetido.
     * 2. Se piden precio, peso y stock reales a ms-catalogo.
     * 3. Se valida stock y límite de compra (1 cupo por curso).
     * 4. Si hay productos físicos se exige zona y dirección y se cotiza el envío.
     * 5. Se descuenta el stock en ms-catalogo; si algo falla a la mitad se devuelve lo ya descontado.
     * 6. Se guarda el pedido con neto e IVA calculados.
     */
    @Transactional
    public Pedido crear(PedidoRequest datos) {
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (ItemRequest item : datos.items()) {
            cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
        }

        Pedido pedido = new Pedido();
        pedido.setCorreoUsuario(datos.correoUsuario().trim().toLowerCase());
        pedido.setNombreCliente(datos.nombreCliente().trim());
        pedido.setCorreoCliente(datos.correoCliente().trim().toLowerCase());

        int subtotal = 0;
        double pesoKg = 0;
        boolean requiereEnvio = false;
        for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {
            ProductoCatalogo producto = catalogo.buscarProducto(entrada.getKey());
            int cantidad = entrada.getValue();
            if (cantidad > producto.limiteCompra()) {
                if (producto.stock() == 0) {
                    throw new ReglaNegocioException("\"" + producto.nombre() + "\" está agotado");
                }
                throw new ReglaNegocioException("Puedes comprar como máximo " + producto.limiteCompra()
                        + " de \"" + producto.nombre() + "\"");
            }
            pedido.agregarLinea(new LineaPedido(producto.id(), producto.nombre(), cantidad, producto.precio()));
            subtotal += producto.precio() * cantidad;
            pesoKg += producto.pesoKg() * cantidad;
            requiereEnvio = requiereEnvio || producto.requiereEnvio();
        }

        int costoEnvio = 0;
        if (requiereEnvio) {
            if (datos.zona() == null || datos.zona().isBlank()) {
                throw new IllegalArgumentException("Elige la zona de despacho");
            }
            if (datos.direccion() == null || datos.direccion().trim().length() < 5) {
                throw new IllegalArgumentException("Ingresa una dirección de despacho válida");
            }
            Zona zona = cotizador.buscarZona(datos.zona());
            Cotizacion cotizacion = cotizador.cotizar(zona, pesoKg, subtotal, datos.tipoEnvio());
            costoEnvio = cotizacion.costoEnvio();
            pedido.setZonaEnvio(zona.nombre());
            pedido.setTipoEnvio(cotizacion.tipoEnvio());
            pedido.setDireccion(datos.direccion().trim());
        }

        int total = subtotal + costoEnvio;
        pedido.setSubtotal(subtotal);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setTotal(total);
        pedido.setNeto(CotizadorEnvio.neto(total));
        pedido.setIva(CotizadorEnvio.iva(total));

        descontarConCompensacion(pedido.getLineas());
        return repositorio.save(pedido);
    }

    // si el tercer producto no tiene stock, se devuelve el stock del primero y el segundo
    private void descontarConCompensacion(List<LineaPedido> lineas) {
        List<LineaPedido> descontadas = new ArrayList<>();
        try {
            for (LineaPedido linea : lineas) {
                catalogo.descontarStock(linea.getProductoId(), linea.getCantidad());
                descontadas.add(linea);
            }
        } catch (RuntimeException error) {
            for (LineaPedido linea : descontadas) {
                try {
                    catalogo.reponerStock(linea.getProductoId(), linea.getCantidad());
                } catch (RuntimeException e) {
                    log.error("No se pudo devolver el stock del producto {}", linea.getProductoId(), e);
                }
            }
            throw error;
        }
    }

    @Transactional(readOnly = true)
    public List<Pedido> listar(String correoUsuario) {
        if (correoUsuario == null || correoUsuario.isBlank()) {
            return repositorio.findAllByOrderByFechaDesc();
        }
        return repositorio.findByCorreoUsuarioIgnoreCaseOrderByFechaDesc(correoUsuario.trim());
    }

    @Transactional(readOnly = true)
    public Pedido buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el pedido " + id));
    }

    // pendiente -> confirmada -> enviada -> entregada (simula al courier)
    @Transactional
    public Pedido avanzarEstado(Long id) {
        Pedido pedido = buscar(id);
        EstadoPedido siguiente = pedido.getEstado().siguiente();
        if (siguiente == null) {
            throw new ReglaNegocioException("El pedido ya fue entregado");
        }
        pedido.setEstado(siguiente);
        return repositorio.save(pedido);
    }

    // solo se puede anular antes de que salga a despacho; el stock vuelve al catálogo
    @Transactional
    public void anular(Long id) {
        Pedido pedido = buscar(id);
        if (pedido.getEstado() == EstadoPedido.ENVIADA || pedido.getEstado() == EstadoPedido.ENTREGADA) {
            throw new ReglaNegocioException("El pedido ya fue " + pedido.getEstado().getEtiqueta().toLowerCase()
                    + " y no se puede anular");
        }
        for (LineaPedido linea : pedido.getLineas()) {
            catalogo.reponerStock(linea.getProductoId(), linea.getCantidad());
        }
        repositorio.delete(pedido);
    }
}
