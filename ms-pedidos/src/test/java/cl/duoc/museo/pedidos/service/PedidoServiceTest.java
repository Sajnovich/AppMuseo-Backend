package cl.duoc.museo.pedidos.service;

import cl.duoc.museo.pedidos.client.CatalogoClient;
import cl.duoc.museo.pedidos.client.ProductoCatalogo;
import cl.duoc.museo.pedidos.dto.ItemRequest;
import cl.duoc.museo.pedidos.dto.PedidoRequest;
import cl.duoc.museo.pedidos.exception.ReglaNegocioException;
import cl.duoc.museo.pedidos.model.EstadoPedido;
import cl.duoc.museo.pedidos.model.Pedido;
import cl.duoc.museo.pedidos.model.TipoEnvio;
import cl.duoc.museo.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository repositorio;
    @Mock
    private CatalogoClient catalogo;

    private PedidoService servicio;

    private final ProductoCatalogo taza = new ProductoCatalogo(1L, "Taza Museo de los Museos", 8_990, "MERCH", 25, 0.4, true, 25);
    private final ProductoCatalogo polera = new ProductoCatalogo(2L, "Polera algodón orgánico", 14_990, "MERCH", 15, 0.3, true, 15);
    private final ProductoCatalogo curso = new ProductoCatalogo(10L, "Curso: Introducción a la Museología", 89_990, "CURSO", 25, 0, false, 1);

    @BeforeEach
    void preparar() {
        servicio = new PedidoService(repositorio, catalogo, new CotizadorEnvio());
    }

    private PedidoRequest compra(String zona, String direccion, TipoEnvio tipo, ItemRequest... items) {
        return new PedidoRequest("estudiante@museo.cl", "Estudiante Demo", "estudiante@museo.cl",
                direccion, zona, tipo, List.of(items));
    }

    @Test
    void calculaTotalesConPreciosDelCatalogoYEnvioGratis() {
        when(catalogo.buscarProducto(1L)).thenReturn(taza);
        when(catalogo.buscarProducto(2L)).thenReturn(polera);
        when(repositorio.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido p = servicio.crear(compra("Región Metropolitana", "Av. Siempre Viva 742", TipoEnvio.ESTANDAR,
                new ItemRequest(1L, 3), new ItemRequest(2L, 2)));

        assertEquals(56_950, p.getSubtotal());
        assertEquals(0, p.getCostoEnvio());
        assertEquals(56_950, p.getTotal());
        assertEquals(p.getTotal(), p.getNeto() + p.getIva());
        assertEquals(EstadoPedido.PENDIENTE, p.getEstado());
        verify(catalogo).descontarStock(1L, 3);
        verify(catalogo).descontarStock(2L, 2);
    }

    @Test
    void juntaProductosRepetidosEnUnaSolaLinea() {
        when(catalogo.buscarProducto(1L)).thenReturn(taza);
        when(repositorio.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido p = servicio.crear(compra("Región Metropolitana", "Av. Siempre Viva 742", TipoEnvio.EXPRESS,
                new ItemRequest(1L, 1), new ItemRequest(1L, 2)));

        assertEquals(1, p.getLineas().size());
        assertEquals(3, p.getLineas().get(0).getCantidad());
    }

    @Test
    void soloCursosNoPideDespacho() {
        when(catalogo.buscarProducto(10L)).thenReturn(curso);
        when(repositorio.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido p = servicio.crear(compra(null, null, null, new ItemRequest(10L, 1)));

        assertNull(p.getZonaEnvio());
        assertEquals(0, p.getCostoEnvio());
        assertEquals(89_990, p.getTotal());
    }

    @Test
    void productosFisicosExigenZona() {
        when(catalogo.buscarProducto(1L)).thenReturn(taza);

        assertThrows(IllegalArgumentException.class,
                () -> servicio.crear(compra(null, "Av. Siempre Viva 742", TipoEnvio.ESTANDAR, new ItemRequest(1L, 1))));
        verify(catalogo, never()).descontarStock(any(), anyInt());
    }

    @Test
    void noSePuedeComprarMasDeUnCupoDeCurso() {
        when(catalogo.buscarProducto(10L)).thenReturn(curso);

        assertThrows(ReglaNegocioException.class, () -> servicio.crear(compra(null, null, null, new ItemRequest(10L, 2))));
    }

    @Test
    void siFallaElStockSeDevuelveLoYaDescontado() {
        when(catalogo.buscarProducto(1L)).thenReturn(taza);
        when(catalogo.buscarProducto(2L)).thenReturn(polera);
        // lenient: el producto 1 sí se descuenta (con otros argumentos); solo el 2 falla por falta de stock
        lenient().doThrow(new ReglaNegocioException("No hay stock suficiente")).when(catalogo).descontarStock(2L, 2);

        assertThrows(ReglaNegocioException.class, () -> servicio.crear(compra("Región Metropolitana",
                "Av. Siempre Viva 742", TipoEnvio.ESTANDAR, new ItemRequest(1L, 3), new ItemRequest(2L, 2))));

        verify(catalogo).reponerStock(1L, 3);
        verify(repositorio, never()).save(any());
    }

    @Test
    void elEstadoAvanzaHastaEntregado() {
        Pedido p = new Pedido();
        p.setEstado(EstadoPedido.ENVIADA);
        when(repositorio.findById(5L)).thenReturn(Optional.of(p));
        when(repositorio.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals(EstadoPedido.ENTREGADA, servicio.avanzarEstado(5L).getEstado());
        assertThrows(ReglaNegocioException.class, () -> servicio.avanzarEstado(5L));
    }

    @Test
    void noSeAnulaUnPedidoYaEnviado() {
        Pedido p = new Pedido();
        p.setEstado(EstadoPedido.ENVIADA);
        when(repositorio.findById(7L)).thenReturn(Optional.of(p));

        assertThrows(ReglaNegocioException.class, () -> servicio.anular(7L));
        verify(repositorio, never()).delete(any());
    }
}
