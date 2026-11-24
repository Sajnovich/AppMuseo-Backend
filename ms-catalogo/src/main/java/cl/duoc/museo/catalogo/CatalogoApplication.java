package cl.duoc.museo.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// sin usuario/contraseña por defecto de Spring Security: la autenticación es solo con JWT
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class CatalogoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogoApplication.class, args);
    }
}
