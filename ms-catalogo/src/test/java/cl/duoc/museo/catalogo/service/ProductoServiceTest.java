package cl.duoc.museo.catalogo.service;

import cl.duoc.museo.catalogo.exception.RecursoNoEncontradoException;
import cl.duoc.museo.catalogo.exception.ReglaNegocioException;
import cl.duoc.museo.catalogo.model.Categoria;
import cl.duoc.museo.catalogo.model.Producto;
import cl.duoc.museo.catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas unitarias: el repositorio es un "doble" (mock), así se prueba solo la lógica del servicio
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repositorio;

    @InjectMocks
    private ProductoService servicio;

    private Producto taza;
    private Producto curso;

    @BeforeEach
    void preparar() {
        taza = Producto.fisico("Taza", "Taza de cerámica", 8_990, Categoria.MERCH, 5, 0.4, "MDM-001", true);
        taza.setId(1L);
        curso = Producto.curso("Curso", "Curso online", 89_990, 2, "Online", 6, false);
        curso.setId(10L);
    }

    @Test
    @DisplayName("Descontar stock resta las unidades compradas")
    void descontarStockResta() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(taza));
        when(repositorio.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto resultado = servicio.descontarStock(1L, 3);

        assertEquals(2, resultado.getStock());
        verify(repositorio).save(taza);
    }

    @Test
    @DisplayName("No se puede comprar más de lo que hay en stock")
    void sinStockSuficienteLanzaError() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(taza));

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> servicio.descontarStock(1L, 6));

        assertTrue(error.getMessage().contains("quedan 5"));
        verify(repositorio, never()).save(any());
    }

    @Test
    @DisplayName("De un curso solo se compra un cupo")
    void cursoSoloUnCupo() {
        when(repositorio.findById(10L)).thenReturn(Optional.of(curso));

        assertThrows(ReglaNegocioException.class, () -> servicio.descontarStock(10L, 2));
    }

    @Test
    @DisplayName("Reponer stock suma las unidades devueltas")
    void reponerStockSuma() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(taza));
        when(repositorio.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals(8, servicio.reponerStock(1L, 3).getStock());
    }

    @Test
    @DisplayName("Buscar un producto que no existe da 404")
    void productoInexistente() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.buscar(99L));
    }

    @Test
    @DisplayName("Un producto físico no puede ser de la categoría curso")
    void fisicoNoPuedeSerCurso() {
        assertThrows(IllegalArgumentException.class,
                () -> Producto.fisico("X", "Y", 1000, Categoria.CURSO, 1, 1, "MDM-999", false));
    }

    @Test
    @DisplayName("Los cursos no requieren envío y su límite de compra es 1")
    void reglasDelCurso() {
        assertFalse(curso.requiereEnvio());
        assertEquals(1, curso.limiteCompra());
        assertTrue(taza.requiereEnvio());
        assertEquals(5, taza.limiteCompra());
    }
}
