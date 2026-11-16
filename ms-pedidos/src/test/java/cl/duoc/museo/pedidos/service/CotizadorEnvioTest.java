package cl.duoc.museo.pedidos.service;

import cl.duoc.museo.pedidos.model.TipoDestino;
import cl.duoc.museo.pedidos.model.TipoEnvio;
import cl.duoc.museo.pedidos.model.Zona;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CotizadorEnvioTest {

    private final CotizadorEnvio cotizador = new CotizadorEnvio();
    private final Zona rm = cotizador.buscarZona("Región Metropolitana");

    @Test
    void elPesoSeCobraPorKiloCompletoConMinimoUno() {
        assertEquals(1, cotizador.pesoFacturable(0.4));
        assertEquals(2, cotizador.pesoFacturable(1.8));
        assertEquals(3, cotizador.pesoFacturable(3.0));
    }

    @Test
    void estandarEnRmBajoElMontoMinimo() {
        // 2.990 + 990 × 2 kg
        Cotizacion c = cotizador.cotizar(rm, 1.8, 20_000, TipoEnvio.ESTANDAR);
        assertEquals(4_970, c.costoEnvio());
        assertFalse(c.envioGratis());
        assertEquals("1 a 2 días hábiles", c.plazo());
    }

    @Test
    void estandarNacionalEsGratisDesde50Mil() {
        Cotizacion c = cotizador.cotizar(rm, 1.8, 56_950, TipoEnvio.ESTANDAR);
        assertTrue(c.envioGratis());
        assertEquals(0, c.costoEnvio());
    }

    @Test
    void expressNuncaEsGratisYCuesta60PorCientoMas() {
        // mismo carrito que se ve en la app: (2.990 + 990 × 2) × 1,6 = 7.952
        Cotizacion c = cotizador.cotizar(rm, 1.8, 56_950, TipoEnvio.EXPRESS);
        assertFalse(c.envioGratis());
        assertEquals(7_952, c.costoEnvio());
        assertEquals("1 día hábil", c.plazo());
    }

    @Test
    void internacionalNoTieneEnvioGratis() {
        Zona sudamerica = cotizador.buscarZona("Sudamérica");
        Cotizacion c = cotizador.cotizar(sudamerica, 1.0, 100_000, TipoEnvio.ESTANDAR);
        assertEquals(TipoDestino.INTERNACIONAL, sudamerica.tipo());
        assertEquals(20_980, c.costoEnvio());
    }

    @Test
    void rechazaPesosFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> cotizador.cotizar(rm, 0, 0, TipoEnvio.ESTANDAR));
        assertThrows(IllegalArgumentException.class, () -> cotizador.cotizar(rm, 31, 0, TipoEnvio.ESTANDAR));
    }

    @Test
    void zonaInexistenteLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> cotizador.buscarZona("Marte"));
    }

    @Test
    void desglosaNetoEIvaComoUnaBoletaChilena() {
        assertEquals(46_193, CotizadorEnvio.neto(54_970));
        assertEquals(8_777, CotizadorEnvio.iva(54_970));
    }
}
