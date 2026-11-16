package cl.duoc.museo.catalogo.service;

import cl.duoc.museo.catalogo.dto.ProductoRequest;
import cl.duoc.museo.catalogo.exception.RecursoNoEncontradoException;
import cl.duoc.museo.catalogo.exception.ReglaNegocioException;
import cl.duoc.museo.catalogo.model.Categoria;
import cl.duoc.museo.catalogo.model.Producto;
import cl.duoc.museo.catalogo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repositorio;

    public ProductoService(ProductoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar(Categoria categoria, String texto) {
        if (texto != null && !texto.isBlank()) {
            List<Producto> encontrados = repositorio.findByNombreContainingIgnoreCaseOrderById(texto.trim());
            return categoria == null ? encontrados
                    : encontrados.stream().filter(p -> p.getCategoria() == categoria).toList();
        }
        if (categoria != null) {
            return repositorio.findByCategoriaOrderById(categoria);
        }
        return repositorio.findAllByOrderById();
    }

    @Transactional(readOnly = true)
    public Producto buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
    }

    @Transactional(readOnly = true)
    public Producto buscarPorCodigo(String codigo) {
        return repositorio.findByCodigoBarraIgnoreCase(codigo.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay un producto con el código " + codigo.trim()));
    }

    @Transactional
    public Producto crear(ProductoRequest datos) {
        validarReglas(datos, null);
        Producto producto = new Producto();
        copiar(datos, producto);
        return repositorio.save(producto);
    }

    @Transactional
    public Producto actualizar(Long id, ProductoRequest datos) {
        Producto producto = buscar(id);
        validarReglas(datos, producto);
        copiar(datos, producto);
        return repositorio.save(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        repositorio.delete(buscar(id));
    }

    // Regla de negocio: al comprar se descuenta stock (o cupos de un curso).
    // Nunca puede quedar negativo y de un curso se compra un solo cupo.
    @Transactional
    public Producto descontarStock(Long id, int cantidad) {
        Producto producto = buscar(id);
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }
        if (producto.esCurso() && cantidad > 1) {
            throw new ReglaNegocioException("De un curso solo se puede comprar 1 cupo");
        }
        if (producto.getStock() < cantidad) {
            String unidad = producto.esCurso() ? "cupos" : "unidades";
            throw new ReglaNegocioException("No hay stock suficiente de \"" + producto.getNombre()
                    + "\": quedan " + producto.getStock() + " " + unidad);
        }
        producto.setStock(producto.getStock() - cantidad);
        return repositorio.save(producto);
    }

    // devuelve stock cuando se anula una compra
    @Transactional
    public Producto reponerStock(Long id, int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }
        Producto producto = buscar(id);
        producto.setStock(producto.getStock() + cantidad);
        return repositorio.save(producto);
    }

    private void validarReglas(ProductoRequest d, Producto actual) {
        boolean esCurso = d.categoria() == Categoria.CURSO;
        if (esCurso) {
            if (d.modalidad() == null || d.modalidad().isBlank() || d.semanas() == null) {
                throw new IllegalArgumentException("Un curso necesita modalidad y semanas");
            }
        } else {
            if (d.codigoBarra() == null || d.codigoBarra().isBlank()) {
                throw new IllegalArgumentException("Un producto físico necesita código MDM-000");
            }
            if (d.pesoKg() <= 0) {
                throw new IllegalArgumentException("Un producto físico debe tener peso mayor a 0");
            }
            boolean cambiaCodigo = actual == null || !d.codigoBarra().equalsIgnoreCase(actual.getCodigoBarra());
            if (cambiaCodigo && repositorio.existsByCodigoBarraIgnoreCase(d.codigoBarra())) {
                throw new ReglaNegocioException("Ya existe un producto con el código " + d.codigoBarra().toUpperCase());
            }
        }
    }

    private void copiar(ProductoRequest d, Producto p) {
        boolean esCurso = d.categoria() == Categoria.CURSO;
        p.setNombre(d.nombre().trim());
        p.setDescripcion(d.descripcion().trim());
        p.setPrecio(d.precio());
        p.setCategoria(d.categoria());
        p.setStock(d.stock());
        p.setPesoKg(esCurso ? 0 : d.pesoKg());
        p.setCodigoBarra(esCurso ? null : d.codigoBarra().trim().toUpperCase());
        p.setModalidad(esCurso ? d.modalidad().trim() : null);
        p.setSemanas(esCurso ? d.semanas() : null);
        p.setDestacado(d.destacado());
    }
}
