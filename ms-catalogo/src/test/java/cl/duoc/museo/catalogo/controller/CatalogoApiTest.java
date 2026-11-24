package cl.duoc.museo.catalogo.controller;

import cl.duoc.museo.catalogo.seguridad.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Prueba de integración: levanta el microservicio completo con H2 y llama a la API REST
@SpringBootTest
@AutoConfigureMockMvc
class CatalogoApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtService jwt;

    private String bearer(String rol) {
        return "Bearer " + jwt.emitir(rol.toLowerCase() + "@museo.cl", rol).token();
    }

    @Test
    void listaLosCatorceMuseosDelCaso() throws Exception {
        mvc.perform(get("/api/museos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(14)));
    }

    @Test
    void filtraMuseosPorPais() throws Exception {
        mvc.perform(get("/api/museos").param("pais", "Brasil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void resuelveUnQrDeProducto() throws Exception {
        mvc.perform(get("/api/qr/MDM-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("PRODUCTO"))
                .andExpect(jsonPath("$.nombre").value("Taza Museo de los Museos"));
    }

    @Test
    void qrInvalidoDa400() throws Exception {
        mvc.perform(get("/api/qr/HOLA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", containsString("no reconocido")));
    }

    @Test
    void crearProductoConDatosInvalidosDevuelveLosCampos() throws Exception {
        String json = """
                {"nombre":"","descripcion":"x","precio":0,"categoria":"MERCH","stock":1,"pesoKg":1,"codigoBarra":"ABC"}
                """;
        mvc.perform(post("/api/productos").header("Authorization", bearer("ADMINISTRADOR"))
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nombre").exists())
                .andExpect(jsonPath("$.campos.precio").exists())
                .andExpect(jsonPath("$.campos.codigoBarra").exists());
    }

    @Test
    void productoInexistenteDa404() throws Exception {
        mvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound());
    }

    // ---- JWT: permisos por rol ----

    private static final String PRODUCTO = """
            {"nombre":"Lápiz Museo","descripcion":"Lápiz de grafito con logo","precio":1990,
             "categoria":"MERCH","stock":50,"pesoKg":0.05,"codigoBarra":"MDM-099"}
            """;

    @Test
    void crearProductoSinTokenDa401() throws Exception {
        mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearProductoComoClienteDa403() throws Exception {
        mvc.perform(post("/api/productos").header("Authorization", bearer("CLIENTE"))
                        .contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
                .andExpect(status().isForbidden());
    }

    @Test
    void elInvestigadorNoPuedeBorrarProductosPeroSiFichasDeMuseo() throws Exception {
        mvc.perform(delete("/api/productos/1").header("Authorization", bearer("INVESTIGADOR")))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/museos/999").header("Authorization", bearer("INVESTIGADOR"))
                        .contentType(MediaType.APPLICATION_JSON).content(MUSEO))
                .andExpect(status().isNotFound());   // pasó la seguridad; el museo 999 no existe
        mvc.perform(delete("/api/museos/1").header("Authorization", bearer("INVESTIGADOR")))
                .andExpect(status().isForbidden());  // borrar una ficha publicada es del Administrador
    }

    private static final String MUSEO = """
            {"nombre":"Museo de Prueba","pais":"Chile","ciudad":"Santiago","tipo":"Historia",
             "descripcion":"Ficha creada en la prueba","detalle":"","latitud":-33.44,"longitud":-70.65,
             "anioLevantamiento":2026,"calificacion":4.5,"resenas":0}
            """;
}
