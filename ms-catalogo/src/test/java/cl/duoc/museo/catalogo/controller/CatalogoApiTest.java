package cl.duoc.museo.catalogo.controller;

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
        mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(json))
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
}
