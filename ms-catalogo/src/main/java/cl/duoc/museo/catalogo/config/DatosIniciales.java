package cl.duoc.museo.catalogo.config;

import cl.duoc.museo.catalogo.model.Categoria;
import cl.duoc.museo.catalogo.model.Museo;
import cl.duoc.museo.catalogo.model.Producto;
import cl.duoc.museo.catalogo.repository.MuseoRepository;
import cl.duoc.museo.catalogo.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// Carga los 14 museos y 12 productos del caso la primera vez que arranca (si las tablas están vacías)
@Component
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    private final MuseoRepository museos;
    private final ProductoRepository productos;

    public DatosIniciales(MuseoRepository museos, ProductoRepository productos) {
        this.museos = museos;
        this.productos = productos;
    }

    @Override
    public void run(String... args) {
        if (museos.count() == 0) {
            museos.saveAll(museosDelCaso());
            log.info("Se cargaron {} museos iniciales", museos.count());
        }
        if (productos.count() == 0) {
            productos.saveAll(productosDelCaso());
            log.info("Se cargaron {} productos iniciales", productos.count());
        }
    }

    static List<Museo> museosDelCaso() {
        return List.of(
                new Museo("Museo Virtual de Arte Precolombino", "Chile", "Santiago", "Arte",
                        "Colección digital de textiles y cerámica andina en 3D.", -33.44, -70.65, 2026, 4.8, 132,
                        "Recorre salas virtuales con piezas de las culturas Diaguita, Mapuche e Inca. Cada objeto se puede girar en 3D y trae una ficha con su origen, material y época."),
                new Museo("Museo Virtual del Puerto", "Chile", "Valparaíso", "Historia",
                        "Recorrido inmersivo por la historia portuaria y sus ascensores.", -33.05, -71.62, 2025, 4.6, 98,
                        "Fotografías restauradas, planos antiguos y relatos de trabajadores del puerto. Incluye un recorrido 360° por los ascensores patrimoniales."),
                new Museo("Museo Virtual de la Pampa", "Argentina", "Buenos Aires", "Historia",
                        "Archivo fotográfico digitalizado de la vida rural.", -34.60, -58.38, 2025, 4.4, 76,
                        "Más de dos mil fotografías de estancias, oficios y fiestas rurales, ordenadas por década y con buscador por lugar."),
                new Museo("Museo Virtual del Río de la Plata", "Uruguay", "Montevideo", "Arte",
                        "Galería de arte contemporáneo con visitas guiadas en línea.", -34.90, -56.16, 2026, 4.5, 64,
                        "Exposiciones de artistas rioplatenses con visitas guiadas en vivo cada semana y material educativo descargable."),
                new Museo("Museo Virtual Andino", "Perú", "Lima", "Arqueología",
                        "Modelos 3D de sitios arqueológicos de la costa y la sierra.", -12.05, -77.04, 2025, 4.9, 210,
                        "Sitios como Caral y Chan Chan reconstruidos en 3D, con capas que muestran cómo eran en su época de mayor esplendor."),
                new Museo("Museo Virtual del Altiplano", "Bolivia", "La Paz", "Etnografía",
                        "Saberes y fiestas del altiplano en formato de archivo sonoro.", -16.50, -68.15, 2026, 4.3, 41,
                        "Grabaciones de música, cantos y testimonios aymaras y quechuas, acompañadas de fotografías y transcripciones."),
                new Museo("Museo Virtual del Café", "Colombia", "Bogotá", "Etnografía",
                        "La cultura cafetera contada por sus comunidades.", 4.71, -74.07, 2025, 4.7, 120,
                        "El proceso del café desde la semilla hasta la taza, contado por familias caficultoras. Incluye mapas de las regiones productoras."),
                new Museo("Museo Virtual de la Amazonía", "Brasil", "Manaos", "Ciencias naturales",
                        "Biodiversidad amazónica con colecciones científicas digitalizadas.", -3.12, -60.02, 2026, 4.8, 155,
                        "Ejemplares de flora y fauna digitalizados en alta resolución, con fichas científicas y sonidos de la selva."),
                new Museo("Museo Virtual de Arte Moderno", "Brasil", "São Paulo", "Arte",
                        "Exposiciones temporales de arte moderno latinoamericano.", -23.55, -46.63, 2025, 4.5, 88,
                        "Obras del modernismo latinoamericano con audioguías en español y portugués."),
                new Museo("Museo Virtual Mesoamericano", "México", "Ciudad de México", "Arqueología",
                        "Reconstrucciones digitales de ciudades mesoamericanas.", 19.43, -99.13, 2026, 4.9, 240,
                        "Tenochtitlan y Teotihuacan reconstruidas digitalmente, con recorridos guiados por sus templos y mercados."),
                new Museo("Museo Virtual del Caribe", "Cuba", "La Habana", "Historia",
                        "Historia marítima y cultural del Caribe.", 23.11, -82.37, 2025, 4.2, 37,
                        "Rutas comerciales, fortificaciones y música del Caribe en una línea de tiempo interactiva."),
                new Museo("Museo Virtual de Ciencias del Pacífico", "Costa Rica", "San José", "Ciencia",
                        "Laboratorios virtuales sobre volcanes y océanos.", 9.93, -84.09, 2026, 4.6, 59,
                        "Simuladores de erupciones y corrientes oceánicas pensados para colegios, con guías para profesores."),
                new Museo("Museo Virtual de Historia Ibérica", "España", "Madrid", "Historia",
                        "Vínculos históricos entre la península y América.", 40.42, -3.70, 2025, 4.4, 71,
                        "Documentos, mapas y objetos que muestran el intercambio entre la península ibérica y América."),
                new Museo("Museo Virtual de la Navegación", "Portugal", "Lisboa", "Historia",
                        "Cartografía histórica y rutas de navegación atlánticas.", 38.72, -9.14, 2026, 4.5, 66,
                        "Cartas náuticas digitalizadas e instrumentos de navegación que se pueden explorar en detalle.")
        );
    }

    static List<Producto> productosDelCaso() {
        return List.of(
                Producto.fisico("Taza Museo de los Museos", "Taza de cerámica de 350 ml con el isotipo del museo.",
                        8_990, Categoria.MERCH, 25, 0.4, "MDM-001", true),
                Producto.fisico("Polera algodón orgánico", "Polera unisex con ilustración de columnas clásicas.",
                        14_990, Categoria.MERCH, 15, 0.3, "MDM-002", false),
                Producto.fisico("Tote bag ilustrada", "Bolso de tela reutilizable con mapa de museos de Iberoamérica.",
                        7_990, Categoria.MERCH, 30, 0.2, "MDM-003", false),
                Producto.fisico("Guantes de nitrilo (caja 100)", "Para manipulación segura de piezas y documentos.",
                        12_990, Categoria.CONSERVACION, 20, 0.6, "MDM-004", false),
                Producto.fisico("Papel libre de ácido (50 hojas)", "Papel de pH neutro para guarda de obra sobre papel.",
                        18_990, Categoria.CONSERVACION, 12, 2.5, "MDM-005", true),
                Producto.fisico("Caja de conservación archivística", "Caja de cartón libre de ácido para fotografías y documentos.",
                        24_990, Categoria.CONSERVACION, 8, 1.8, "MDM-006", false),
                Producto.fisico("Manual de Museología Contemporánea", "Libro de referencia sobre gestión y políticas de museos.",
                        22_990, Categoria.PUBLICACION, 10, 0.9, "MDM-007", true),
                Producto.fisico("Guía de Mediación Cultural", "Estrategias prácticas para trabajar con distintas audiencias.",
                        15_990, Categoria.PUBLICACION, 14, 0.5, "MDM-008", false),
                Producto.fisico("Revista Audiencias & Museos N°12", "Investigación aplicada sobre públicos en Iberoamérica.",
                        6_990, Categoria.PUBLICACION, 40, 0.3, "MDM-009", false),
                Producto.curso("Curso: Introducción a la Museología", "Fundamentos de la museología contemporánea. Certificado digital.",
                        89_990, 25, "Online", 6, true),
                Producto.curso("Curso: Conservación Preventiva", "Cuidado de colecciones. Incluye kit de práctica.",
                        119_990, 18, "Híbrido", 8, false),
                Producto.curso("Curso: Experiencias para Audiencias", "Diseño de visitas y mediación para distintos públicos.",
                        99_990, 20, "Online", 5, false)
        );
    }
}
