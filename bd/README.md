# Base de datos: MySQL con Docker + MySQL Workbench

## 1. Instalar Docker Desktop (una sola vez)

1. Descarga **Docker Desktop para Mac (Apple Silicon)** desde https://www.docker.com/products/docker-desktop/
2. Ábrelo y espera a que la ballena de la barra superior quede quieta (Docker encendido).

## 2. Encender MySQL

En la terminal, dentro de la carpeta `AppMuseo-Backend`:

```bash
./levantar-bd.sh
```

| Script | Qué hace |
|---|---|
| `./levantar-bd.sh` | Enciende MySQL y espera a que esté listo |
| `./detener-bd.sh` | Lo apaga sin borrar nada |
| `./reiniciar-bd.sh` | Borra todo y lo deja como nuevo (pide confirmación) |

La primera vez descarga la imagen de MySQL 8.4 (unos 200 MB). Después enciende en segundos.

## 3. Conectarse con MySQL Workbench

1. En la pantalla de inicio presiona **+** junto a "MySQL Connections".
2. Completa los datos:

| Campo | Valor |
|---|---|
| Connection Name | AppMuseo (Docker) |
| Hostname | 127.0.0.1 |
| Port | 3306 |
| Username | root |
| Password | **Store in Keychain…** → `museo2026` |

3. Presiona **Test Connection**. Si aparece un aviso de versión, presiona *Continue Anyway*. Luego presiona **OK**.
4. Abre la conexión. A la izquierda, en **Schemas**, aparecen `museo_usuarios`, `museo_catalogo` y `museo_pedidos`. Si no aparecen, presiona el botón de refrescar.
5. Abre `bd/consultas_workbench.sql` (**File → Open SQL Script**) y ejecuta las consultas una por una con **Cmd + Enter**.

Las tablas se crean cuando arranca cada microservicio. Si las bases aparecen vacías, enciende primero los tres servicios.

## 4. Para mostrarlo en la evaluación

1. `./levantar-bd.sh`
2. Enciende `CatalogoApplication`, `UsuariosApplication` y `PedidosApplication`.
3. En Workbench, muestra `usuarios` (la clave cifrada), `museos`, `productos` y `pedidos`.
4. Haz una compra desde la app y vuelve a ejecutar la consulta de `pedidos`. El pedido nuevo aparece y el stock del producto baja en `productos`.

## Problemas comunes

- **"port is already allocated" / el 3306 está ocupado:** ya tienes otro MySQL encendido en el Mac. Apágalo, o cambia `"3306:3306"` por `"3307:3306"` en `docker-compose.yml`. En ese caso conecta Workbench al puerto 3307 y arranca los servicios con la variable `DB_URL` apuntando a `localhost:3307`.
- **"Access denied for user 'root'":** la base se creó antes con otra clave. Corre `./reiniciar-bd.sh`.
