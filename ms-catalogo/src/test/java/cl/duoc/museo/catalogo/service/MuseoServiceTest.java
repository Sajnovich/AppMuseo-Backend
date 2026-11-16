package cl.duoc.museo.catalogo.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MuseoServiceTest {

    @Test
    void leeElIdDesdeElCodigoQr() {
        assertEquals(5L, MuseoService.idDesdeCodigo("MUSEO-5"));
        assertEquals(12L, MuseoService.idDesdeCodigo(" museo-12 "));
    }

    @Test
    void codigosInvalidosDevuelvenNull() {
        assertNull(MuseoService.idDesdeCodigo("MDM-001"));
        assertNull(MuseoService.idDesdeCodigo("MUSEO-"));
        assertNull(MuseoService.idDesdeCodigo(null));
    }
}
