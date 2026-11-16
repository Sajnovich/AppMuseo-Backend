package cl.duoc.museo.catalogo.model;

import jakarta.persistence.*;

// Producto de la tienda. Puede ser físico (tiene stock y peso) o un curso (tiene cupos)
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String descripcion;

    // precio en pesos chilenos, con IVA incluido
    @Column(nullable = false)
    private int precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Categoria categoria;

    // productos físicos: unidades disponibles. Cursos: cupos disponibles
    @Column(nullable = false)
    private int stock;

    // 0 para los cursos (no se despachan)
    private double pesoKg;

    // código impreso en el QR del producto, ej: MDM-001 (los cursos no tienen)
    @Column(unique = true, length = 20)
    private String codigoBarra;

    // solo cursos
    @Column(length = 20)
    private String modalidad;
    private Integer semanas;

    private boolean destacado;

    public Producto() {
    }

    public static Producto fisico(String nombre, String descripcion, int precio, Categoria categoria,
                                  int stock, double pesoKg, String codigoBarra, boolean destacado) {
        if (categoria == Categoria.CURSO) {
            throw new IllegalArgumentException("Un producto físico no puede ser un curso");
        }
        Producto p = new Producto();
        p.nombre = nombre;
        p.descripcion = descripcion;
        p.precio = precio;
        p.categoria = categoria;
        p.stock = stock;
        p.pesoKg = pesoKg;
        p.codigoBarra = codigoBarra;
        p.destacado = destacado;
        return p;
    }

    public static Producto curso(String nombre, String descripcion, int precio, int cupos,
                                 String modalidad, int semanas, boolean destacado) {
        Producto p = new Producto();
        p.nombre = nombre;
        p.descripcion = descripcion;
        p.precio = precio;
        p.categoria = Categoria.CURSO;
        p.stock = cupos;
        p.pesoKg = 0;
        p.modalidad = modalidad;
        p.semanas = semanas;
        p.destacado = destacado;
        return p;
    }

    public boolean esCurso() {
        return categoria == Categoria.CURSO;
    }

    // los cursos no se despachan
    public boolean requiereEnvio() {
        return !esCurso();
    }

    // de un curso se compra un solo cupo por persona
    public int limiteCompra() {
        return esCurso() ? Math.min(1, stock) : stock;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getPesoKg() { return pesoKg; }
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }
    public String getCodigoBarra() { return codigoBarra; }
    public void setCodigoBarra(String codigoBarra) { this.codigoBarra = codigoBarra; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    public Integer getSemanas() { return semanas; }
    public void setSemanas(Integer semanas) { this.semanas = semanas; }
    public boolean isDestacado() { return destacado; }
    public void setDestacado(boolean destacado) { this.destacado = destacado; }
}
