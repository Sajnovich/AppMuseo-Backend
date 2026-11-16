package cl.duoc.museo.pedidos.model;

import jakarta.persistence.*;

// una línea de la boleta: cuántos, qué producto y a qué precio se compró.
// Se guarda el nombre y el precio del momento, así la boleta no cambia si después sube el precio
@Entity
@Table(name = "lineas_pedido")
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(nullable = false)
    private Long productoId;

    @Column(nullable = false, length = 120)
    private String nombre;

    private int cantidad;
    private int precioUnitario;

    public LineaPedido() {
    }

    public LineaPedido(Long productoId, String nombre, int cantidad, int precioUnitario) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getTotal() {
        return cantidad * precioUnitario;
    }

    public Long getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Long getProductoId() { return productoId; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public int getPrecioUnitario() { return precioUnitario; }
}
