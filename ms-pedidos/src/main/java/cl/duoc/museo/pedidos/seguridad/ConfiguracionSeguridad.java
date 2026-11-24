package cl.duoc.museo.pedidos.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Reglas de acceso de ms-pedidos: qué rutas son públicas y qué rol necesita cada una.
// Sin token válido -> 401; con token pero sin el rol -> 403.
@Configuration
public class ConfiguracionSeguridad {

    @Bean
    public SecurityFilterChain cadenaSeguridad(HttpSecurity http, JwtService jwt) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // API REST sin cookies: no aplica CSRF
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reglas -> reglas
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                        // cotizar envío es público (no guarda nada)
                        .requestMatchers("/api/envios/**").permitAll()
                        // cambiar el estado de un pedido es gestión del Administrador
                        .requestMatchers(HttpMethod.PATCH, "/api/pedidos/*/avanzar").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) ->
                                RespuestasSeguridad.escribir(res, 401, "Falta el token JWT o no es válido. Inicia sesión de nuevo"))
                        .accessDeniedHandler((req, res, ex) ->
                                RespuestasSeguridad.escribir(res, 403, "Tu rol no tiene permiso para esta acción")))
                .addFilterBefore(new FiltroJwt(jwt), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
