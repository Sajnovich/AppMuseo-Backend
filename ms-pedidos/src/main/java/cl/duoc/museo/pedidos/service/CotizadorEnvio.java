package cl.duoc.museo.pedidos.service;

import cl.duoc.museo.pedidos.model.TipoDestino;
import cl.duoc.museo.pedidos.model.TipoEnvio;
import cl.duoc.museo.pedidos.model.Zona;
import org.springframework.stereotype.Component;

import java.util.List;

// Lógica de negocio del despacho: misma tabla de tarifas que usaba la app, ahora en el servidor
@Component
public class CotizadorEnvio {

    public static final double PESO_MAXIMO_KG = 30.0;
    public static final int MONTO_ENVIO_GRATIS = 50_000;

    private static final List<Zona> ZONAS = List.of(
            new Zona("Región Metropolitana", TipoDestino.NACIONAL, 2_990, 990, 1, 2),
            new Zona("Zona Centro (Valparaíso a Biobío)", TipoDestino.NACIONAL, 3_990, 1_290, 2, 4),
            new Zona("Zona Norte (Arica a Coquimbo)", TipoDestino.NACIONAL, 4_990, 1_590, 3, 5),
            new Zona("Zona Sur (La Araucanía a Los Lagos)", TipoDestino.NACIONAL, 4_990, 1_590, 3, 5),
            new Zona("Zona Austral (Aysén y Magallanes)", TipoDestino.NACIONAL, 6_990, 2_490, 5, 8),
            new Zona("Sudamérica", TipoDestino.INTERNACIONAL, 14_990, 5_990, 7, 12),
            new Zona("Centroamérica, Caribe y México", TipoDestino.INTERNACIONAL, 17_990, 6_990, 10, 15),
            new Zona("España y Portugal", TipoDestino.INTERNACIONAL, 21_990, 8_990, 10, 18)
    );

    public List<Zona> zonas(TipoDestino tipo) {
        return tipo == null ? ZONAS : ZONAS.stream().filter(z -> z.tipo() == tipo).toList();
    }

    public Zona buscarZona(String nombre) {
        return ZONAS.stream()
                .filter(z -> z.nombre().equalsIgnoreCase(nombre == null ? "" : nombre.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La zona de envío \"" + nombre + "\" no existe"));
    }

    // se cobra por kilo completo, mínimo 1 kg (0,4 kg -> 1 kg; 1,8 kg -> 2 kg)
    public int pesoFacturable(double pesoKg) {
        return Math.max(1, (int) Math.ceil(pesoKg));
    }

    public Cotizacion cotizar(Zona zona, double pesoKg, int subtotalCompra, TipoEnvio tipoEnvio) {
        if (pesoKg <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0 kg");
        }
        if (pesoKg > PESO_MAXIMO_KG) {
            throw new IllegalArgumentException("El courier acepta como máximo " + (int) PESO_MAXIMO_KG + " kg por envío");
        }
        TipoEnvio tipo = tipoEnvio == null ? TipoEnvio.ESTANDAR : tipoEnvio;
        int peso = pesoFacturable(pesoKg);
        int costoNormal = (int) ((zona.tarifaBase() + zona.costoPorKg() * peso) * tipo.getRecargo());
        // el envío gratis es solo para el estándar dentro de Chile
        boolean gratis = zona.tipo() == TipoDestino.NACIONAL
                && tipo == TipoEnvio.ESTANDAR
                && subtotalCompra >= MONTO_ENVIO_GRATIS;
        return new Cotizacion(zona, tipo, peso, gratis ? 0 : costoNormal, gratis);
    }

    // en Chile los precios ya incluyen IVA: neto = total / 1,19
    public static int neto(int total) {
        return (int) Math.round(total / 1.19);
    }

    public static int iva(int total) {
        return total - neto(total);
    }
}
