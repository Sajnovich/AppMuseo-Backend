package cl.duoc.museo.catalogo.repository;

import cl.duoc.museo.catalogo.model.Museo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MuseoRepository extends JpaRepository<Museo, Long> {

    // Spring Data arma la consulta a partir del nombre del método
    List<Museo> findByPaisIgnoreCaseOrderByNombre(String pais);

    List<Museo> findByTipoIgnoreCaseOrderByNombre(String tipo);

    List<Museo> findAllByOrderByNombre();
}
