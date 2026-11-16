package cl.duoc.museo.catalogo.service;

import cl.duoc.museo.catalogo.dto.MuseoRequest;
import cl.duoc.museo.catalogo.exception.RecursoNoEncontradoException;
import cl.duoc.museo.catalogo.model.Museo;
import cl.duoc.museo.catalogo.repository.MuseoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MuseoService {

    private final MuseoRepository repositorio;

    public MuseoService(MuseoRepository repositorio) {
        this.repositorio = repositorio;
    }

    // lista todos o filtra por país / tipo (los filtros del mapa)
    @Transactional(readOnly = true)
    public List<Museo> listar(String pais, String tipo) {
        if (pais != null && !pais.isBlank()) {
            return repositorio.findByPaisIgnoreCaseOrderByNombre(pais.trim());
        }
        if (tipo != null && !tipo.isBlank()) {
            return repositorio.findByTipoIgnoreCaseOrderByNombre(tipo.trim());
        }
        return repositorio.findAllByOrderByNombre();
    }

    @Transactional(readOnly = true)
    public Museo buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el museo con id " + id));
    }

    // "MUSEO-5" -> museo 5. Devuelve null si el texto no tiene ese formato
    public static Long idDesdeCodigo(String codigo) {
        if (codigo == null) return null;
        String limpio = codigo.trim().toUpperCase();
        if (!limpio.matches("^MUSEO-\\d{1,6}$")) return null;
        return Long.parseLong(limpio.substring("MUSEO-".length()));
    }

    @Transactional(readOnly = true)
    public Museo buscarPorCodigo(String codigo) {
        Long id = idDesdeCodigo(codigo);
        if (id == null) {
            throw new IllegalArgumentException("El código de museo debe tener el formato MUSEO-5");
        }
        return buscar(id);
    }

    @Transactional
    public Museo crear(MuseoRequest datos) {
        Museo museo = new Museo();
        copiar(datos, museo);
        return repositorio.save(museo);
    }

    @Transactional
    public Museo actualizar(Long id, MuseoRequest datos) {
        Museo museo = buscar(id);
        copiar(datos, museo);
        return repositorio.save(museo);
    }

    @Transactional
    public void eliminar(Long id) {
        repositorio.delete(buscar(id));
    }

    private void copiar(MuseoRequest d, Museo m) {
        m.setNombre(d.nombre().trim());
        m.setPais(d.pais().trim());
        m.setCiudad(d.ciudad().trim());
        m.setTipo(d.tipo().trim());
        m.setDescripcion(d.descripcion().trim());
        m.setDetalle(d.detalle());
        m.setLatitud(d.latitud());
        m.setLongitud(d.longitud());
        m.setAnioLevantamiento(d.anioLevantamiento());
        m.setCalificacion(d.calificacion());
        m.setResenas(d.resenas());
    }
}
