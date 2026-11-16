package cl.duoc.museo.pedidos.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // dueño del pedido: así cada perfil ve solo sus compras
    @Column(nullable = false, length = 120)
    private String correoUsuario;

    @Column(nullable = false, length = 80)
    private String nombreCliente;

    @Column(nullable = false, length = 120)
    private String correoCliente;

    // vacíos si solo se compraron cursos
    @Column(length = 60)
    private String zonaEnvio;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoEnvio tipoEnvio;

    @Column(length = 200)
    private String direccion;

    private int subtotal;
    private int costoEnvio;
    private int total;
    private int neto;
    private int iva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    // al borrar el pedido se borran sus líneas
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<LineaPedido> lineas = new ArrayList<>();

    public void agregarLinea(LineaPedido linea) {
        linea.setPedido(this);
        lineas.add(linea);
    }

    public int getCantidadProductos() {
        return lineas.stream().mapToInt(LineaPedido::getCantidad).sum();
    }

    // número "bonito" para mostrar, ej: #2026-00007
    public String getCodigo() {
        return "#" + fecha.getYear() + "-" + String.format("%05d", id == null ? 0 : id);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getCorreoCliente() { return correoCliente; }
    public void setCorreoCliente(String correoCliente) { this.correoCliente = correoCliente; }
    public String getZonaEnvio() { return zonaEnvio; }
    public void setZonaEnvio(String zonaEnvio) { this.zonaEnvio = zonaEnvio; }
    public TipoEnvio getTipoEnvio() { return tipoEnvio; }
    public void setTipoEnvio(TipoEnvio tipoEnvio) { this.tipoEnvio = tipoEnvio; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }
    public int getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(int costoEnvio) { this.costoEnvio = costoEnvio; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getNeto() { return neto; }
    public void setNeto(int neto) { this.neto = neto; }
    public int getIva() { return iva; }
    public void setIva(int iva) { this.iva = iva; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public List<LineaPedido> getLineas() { return lineas; }
}
