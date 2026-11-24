package cl.duoc.museo.pedidos.client;

import cl.duoc.museo.pedidos.exception.RecursoNoEncontradoException;
import cl.duoc.museo.pedidos.exception.ReglaNegocioException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

// Comunicación entre microservicios: ms-pedidos le pregunta a ms-catalogo por HTTP (REST)
@Component
public class CatalogoClient {

    private final RestClient http;
    private final ObjectMapper json = new ObjectMapper();

    public CatalogoClient(RestClient.Builder builder, @Value("${catalogo.url}") String urlCatalogo) {
        // reenvía el token JWT del usuario que hizo la compra: ms-catalogo exige estar autenticado para mover stock
        this.http = builder.baseUrl(urlCatalogo)
                .requestInterceptor((peticion, cuerpo, ejecucion) -> {
                    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes atributos) {
                        String token = atributos.getRequest().getHeader("Authorization");
                        if (token != null) peticion.getHeaders().set("Authorization", token);
                    }
                    return ejecucion.execute(peticion, cuerpo);
                })
                .build();
    }

    public ProductoCatalogo buscarProducto(Long id) {
        try {
            return http.get().uri("/api/productos/{id}", id)
                    .retrieve()
                    .body(ProductoCatalogo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("El producto " + id + " no existe en el catálogo");
        } catch (ResourceAccessException e) {
            throw new CatalogoNoDisponibleException("El catálogo no está disponible, intenta más tarde", e);
        }
    }

    public void descontarStock(Long id, int cantidad) {
        mover("/api/productos/{id}/descontar-stock", id, cantidad);
    }

    public void reponerStock(Long id, int cantidad) {
        mover("/api/productos/{id}/reponer-stock", id, cantidad);
    }

    private void mover(String ruta, Long id, int cantidad) {
        try {
            http.post().uri(ruta, id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("cantidad", cantidad))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            // ms-catalogo explica el motivo (ej: "No hay stock suficiente..."): se reenvía tal cual
            throw new ReglaNegocioException(mensajeDe(e, "No se pudo actualizar el stock del producto " + id));
        } catch (ResourceAccessException e) {
            throw new CatalogoNoDisponibleException("El catálogo no está disponible, intenta más tarde", e);
        }
    }

    private String mensajeDe(HttpClientErrorException e, String porDefecto) {
        try {
            JsonNode cuerpo = json.readTree(e.getResponseBodyAsString());
            return cuerpo.hasNonNull("mensaje") ? cuerpo.get("mensaje").asText() : porDefecto;
        } catch (Exception ignorado) {
            return porDefecto;
        }
    }
}
