package cl.duoc.museo.usuarios.controller;

import cl.duoc.museo.usuarios.seguridad.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtService jwt;

    private String bearer(String correo, String rol) {
        return "Bearer " + jwt.emitir(correo, rol).token();
    }

    @Test
    void loginDeLaCuentaDePrueba() throws Exception {
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@museo.cl\",\"clave\":\"museo123\",\"rol\":\"ADMINISTRADOR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.tipoToken").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
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

    // ---- JWT ----

    @Test
    void listarUsuariosSinTokenDa401() throws Exception {
        mvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.estado").value(401));
    }

    @Test
    void listarUsuariosConTokenDeClienteDa403() throws Exception {
        mvc.perform(get("/api/usuarios").header("Authorization", bearer("estudiante@museo.cl", "CLIENTE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarUsuariosConTokenDeAdministradorDa200() throws Exception {
        mvc.perform(get("/api/usuarios").header("Authorization", bearer("admin@museo.cl", "ADMINISTRADOR")))
                .andExpect(status().isOk());
    }

    @Test
    void tokenAlteradoDa401() throws Exception {
        String token = bearer("admin@museo.cl", "ADMINISTRADOR");
        mvc.perform(get("/api/usuarios").header("Authorization", token.substring(0, token.length() - 3) + "abc"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void elTokenDiceQuienEsElUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/sesion").header("Authorization", bearer("investigador@museo.cl", "INVESTIGADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("investigador@museo.cl"))
                .andExpect(jsonPath("$.rol").value("INVESTIGADOR"));
    }

    @Test
    void unClienteNoPuedeVerElPerfilDeOtro() throws Exception {
        mvc.perform(get("/api/usuarios/correo/admin@museo.cl").header("Authorization", bearer("estudiante@museo.cl", "CLIENTE")))
                .andExpect(status().isForbidden());
    }
}
