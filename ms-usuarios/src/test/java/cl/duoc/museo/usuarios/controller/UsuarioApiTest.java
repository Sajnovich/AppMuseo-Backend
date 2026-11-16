package cl.duoc.museo.usuarios.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void loginDeLaCuentaDePrueba() throws Exception {
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@museo.cl\",\"clave\":\"museo123\",\"rol\":\"ADMINISTRADOR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(content().string(not(containsString("claveHash"))));
    }

    @Test
    void loginConClaveMalaDa401() throws Exception {
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@museo.cl\",\"clave\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registroConClaveDebilDa400() throws Exception {
        mvc.perform(post("/api/usuarios/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"correo\":\"ana@museo.cl\",\"clave\":\"abcdef\",\"tipoUsuario\":\"Estudiante\",\"avatar\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.clave").value("Debe combinar letras y números"));
    }

    @Test
    void registroCorrectoDa201() throws Exception {
        mvc.perform(post("/api/usuarios/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana Pérez\",\"correo\":\"ana@museo.cl\",\"clave\":\"ana2026\",\"tipoUsuario\":\"Estudiante\",\"avatar\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }
}
