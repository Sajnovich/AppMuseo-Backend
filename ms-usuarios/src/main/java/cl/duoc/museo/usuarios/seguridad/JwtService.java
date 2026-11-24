package cl.duoc.museo.usuarios.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

// Crea y valida los tokens JWT. Los tres microservicios comparten la misma clave secreta (jwt.secreto),
// así un token emitido por ms-usuarios se puede validar en ms-catalogo y ms-pedidos sin llamarlo.
@Service
public class JwtService {

    private final SecretKey clave;
    private final long minutos;

    public JwtService(@Value("${jwt.secreto}") String secreto,
                      @Value("${jwt.minutos:120}") long minutos) {
        // HMAC-SHA256: la clave debe tener al menos 32 bytes
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.minutos = minutos;
    }

    // Emite un token firmado con el correo (subject), el rol y la fecha de expiración
    public TokenEmitido emitir(String correo, String rol) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(minutos, ChronoUnit.MINUTES);
        String token = Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuer("ms-usuarios")
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expira))
                .signWith(clave)
                .compact();
        return new TokenEmitido(token, LocalDateTime.ofInstant(expira, ZoneId.systemDefault()));
    }

    // Devuelve los datos del token si la firma es correcta y no está vencido; si no, vacío
    public Optional<Claims> validar(String token) {
        try {
            return Optional.of(Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public record TokenEmitido(String token, LocalDateTime expiraEn) { }
}
