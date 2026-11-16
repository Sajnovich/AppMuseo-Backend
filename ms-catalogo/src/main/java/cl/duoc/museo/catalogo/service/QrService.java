package cl.duoc.museo.catalogo.service;

import cl.duoc.museo.catalogo.dto.QrResponse;
import cl.duoc.museo.catalogo.model.Museo;
import cl.duoc.museo.catalogo.model.Producto;
import org.springframework.stereotype.Service;

// Traduce el texto de un QR: MUSEO-5 es un museo, MDM-001 es un producto
@Service
public class QrService {

    private final MuseoService museos;
    private final ProductoService productos;

    public QrService(MuseoService museos, ProductoService productos) {
        this.museos = museos;
        this.productos = productos;
    }

    public QrResponse resolver(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código está vacío");
        }
        String limpio = codigo.trim().toUpperCase();
        if (MuseoService.idDesdeCodigo(limpio) != null) {
            Museo m = museos.buscarPorCodigo(limpio);
            return new QrResponse("MUSEO", m.getId(), m.getCodigoQr(), m.getNombre(), m.getDescripcion(),
                    "Museo · " + m.getTipo());
        }
        if (limpio.matches("^MDM-\\d{3}$")) {
            Producto p = productos.buscarPorCodigo(limpio);
            return new QrResponse("PRODUCTO", p.getId(), p.getCodigoBarra(), p.getNombre(), p.getDescripcion(),
                    "Producto · " + p.getCategoria().name());
        }
        throw new IllegalArgumentException("Código no reconocido. Usa MUSEO-5 o MDM-001");
    }
}
