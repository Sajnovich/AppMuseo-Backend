package cl.duoc.museo.catalogo.repository;

import cl.duoc.museo.catalogo.model.Categoria;
import cl.duoc.museo.catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoriaOrderById(Categoria categoria);

    List<Producto> findByNombreContainingIgnoreCaseOrderById(String texto);

    List<Producto> findAllByOrderById();

    Optional<Producto> findByCodigoBarraIgnoreCase(String codigoBarra);

    boolean existsByCodigoBarraIgnoreCase(String codigoBarra);
}
