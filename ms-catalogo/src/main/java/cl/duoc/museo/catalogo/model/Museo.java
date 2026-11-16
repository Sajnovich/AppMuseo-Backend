package cl.duoc.museo.catalogo.model;

import jakarta.persistence.*;

// Museo virtual que aparece en el mapa de la app
@Entity
@Table(name = "museos")
public class Museo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String pais;

    @Column(nullable = false, length = 60)
    private String ciudad;

    // Arte, Historia, Arqueología, Etnografía, Ciencia...
    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(length = 1000)
    private String detalle;

    private double latitud;
    private double longitud;

    // año en que se levantó la información del museo
    private int anioLevantamiento;

    private double calificacion;
    private int resenas;

    public Museo() {
    }

    public Museo(String nombre, String pais, String ciudad, String tipo, String descripcion,
                 double latitud, double longitud, int anioLevantamiento,
                 double calificacion, int resenas, String detalle) {
        this.nombre = nombre;
        this.pais = pais;
        this.ciudad = ciudad;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.anioLevantamiento = anioLevantamiento;
        this.calificacion = calificacion;
        this.resenas = resenas;
        this.detalle = detalle;
    }

    // lo que va impreso en el QR de cada museo, ej: MUSEO-5
    public String getCodigoQr() {
        return "MUSEO-" + id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public int getAnioLevantamiento() { return anioLevantamiento; }
    public void setAnioLevantamiento(int anioLevantamiento) { this.anioLevantamiento = anioLevantamiento; }
    public double getCalificacion() { return calificacion; }
    public void setCalificacion(double calificacion) { this.calificacion = calificacion; }
    public int getResenas() { return resenas; }
    public void setResenas(int resenas) { this.resenas = resenas; }
}
