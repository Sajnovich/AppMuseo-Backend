package cl.duoc.museo.pedidos.controller;

import cl.duoc.museo.pedidos.seguridad.JwtService;
import cl.duoc.museo.pedidos.client.CatalogoClient;
import cl.duoc.museo.pedidos.client.ProductoCatalogo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ms-catalogo se reemplaza por un mock para probar ms-pedidos solo
@SpringBootTest
@AutoConfigureMockMvc
class PedidoApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CatalogoClient catalogo;

    @Autowired
    private JwtService jwt;

    private static final String CLIENTE = "estudiante@museo.cl";

    private String bearer(String correo, String rol) {
        return "Bearer " + jwt.emitir(correo, rol).token();
    }

    @Test
    void creaUnPedidoYLuegoMuestraSuBoleta() throws Exception {
        when(catalogo.buscarProducto(1L))
                .thenReturn(new ProductoCatalogo(1L, "Taza Museo de los Museos", 8_990, "MERCH", 25, 0.4, true, 25));

        String json = """
                {"correoUsuario":"estudiante@museo.cl","nombreCliente":"Estudiante Demo",
                 "correoCliente":"estudiante@museo.cl","direccion":"Av. Siempre Viva 742",
                 "zona":"Región Metropolitana","tipoEnvio":"ESTANDAR",
                 "items":[{"productoId":1,"cantidad":2}]}
                """;
        String respuesta = mvc.perform(post("/api/pedidos").header("Authorization", bearer(CLIENTE, "CLIENTE"))
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subtotal").value(17_980))
                .andExpect(jsonPath("$.costoEnvio").value(3_980))
                .andExpect(jsonPath("$.total").value(21_960))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn().getResponse().getContentAsString();

        String id = respuesta.replaceAll(".*\"id\":(\\d+).*", "$1");
        mvc.perform(get("/api/pedidos/" + id + "/boleta").header("Authorization", bearer(CLIENTE, "CLIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emisor").value("MUSEO DE LOS MUSEOS"))
                .andExpect(jsonPath("$.lineas[0].cantidad").value(2));
    }

    @Test
    void carritoVacioDa400() throws Exception {
        String json = """
                {"correoUsuario":"estudiante@museo.cl","nombreCliente":"Estudiante Demo",
                 "correoCliente":"estudiante@museo.cl","items":[]}
                """;
        mvc.perform(post("/api/pedidos").header("Authorization", bearer(CLIENTE, "CLIENTE"))
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.items").value("El carrito está vacío"));
    }

    @Test
    void listaLasZonasNacionales() throws Exception {
        mvc.perform(get("/api/envios/zonas").param("tipo", "NACIONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    // ---- JWT: cada rol ve y hace solo lo que le corresponde ----

    @Test
    void sinTokenNoSePuedeComprar() throws Exception {
        mvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unClienteNoVeLosPedidosDeTodos() throws Exception {
        mvc.perform(get("/api/pedidos").header("Authorization", bearer(CLIENTE, "CLIENTE")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/pedidos").param("correo", "otra@museo.cl").header("Authorization", bearer(CLIENTE, "CLIENTE")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/pedidos").param("correo", CLIENTE).header("Authorization", bearer(CLIENTE, "CLIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void elAdministradorVeTodasLasSolicitudes() throws Exception {
        mvc.perform(get("/api/pedidos").header("Authorization", bearer("admin@museo.cl", "ADMINISTRADOR")))
                .andExpect(status().isOk());
    }

    @Test
    void soloElAdministradorCambiaElEstado() throws Exception {
        mvc.perform(patch("/api/pedidos/1/avanzar").header("Authorization", bearer(CLIENTE, "CLIENTE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unClienteNoPuedeComprarANombreDeOtro() throws Exception {
        String json = """
                {"correoUsuario":"otra@museo.cl","nombreCliente":"Otra","correoCliente":"otra@museo.cl",
                 "direccion":"Calle Falsa 123","zona":"Región Metropolitana","tipoEnvio":"ESTANDAR","items":[{"productoId":1,"cantidad":1}]}
                """;
        mvc.perform(post("/api/pedidos").header("Authorization", bearer(CLIENTE, "CLIENTE"))
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isForbidden());
    }
}
