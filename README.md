# AppMuseo · Backend (Museo de los Museos)

Backend de la app **AppMuseo** (DSY1105 · EA3). Son **tres microservicios Spring Boot** independientes, cada uno con su propia base de datos **MySQL**, su **API REST** y su **lógica de negocio**. La app Android los consume con **Retrofit**.

| Microservicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `ms-usuarios` | 8081 | `museo_usuarios` | Registro, inicio de sesión, roles (Administrador, Investigador, Cliente) y perfil |
| `ms-catalogo` | 8082 | `museo_catalogo` | Museos virtuales, productos, stock y lectura de códigos QR |
| `ms-pedidos`  | 8083 | `museo_pedidos`  | Cotización de envío, compras, seguimiento y boleta |

```
App Android (Retrofit)
   ├── ms-usuarios :8081 ── MySQL museo_usuarios
   ├── ms-catalogo :8082 ── MySQL museo_catalogo
   └── ms-pedidos  :8083 ── MySQL museo_pedidos
              └── consulta precios y descuenta stock en ms-catalogo (REST)
```

## Tecnologías

- Java 17 · Spring Boot 3.3 (Web, Data JPA, Validation)
- MySQL 8 · Hibernate crea las tablas solo (`ddl-auto: update`)
- Spring Security Crypto (contraseñas con **BCrypt**)
- springdoc-openapi (documentación en **Swagger UI**)
- JUnit 5 + Mockito + MockMvc (pruebas) · H2 en memoria solo para las pruebas

## Cómo ejecutarlo

### 1. Base de datos (Docker + MySQL Workbench)

```bash
./levantar-bd.sh
```

Esto enciende MySQL 8 en Docker con las tres bases. Los pasos para instalar Docker Desktop y conectarse con **MySQL Workbench** (127.0.0.1:3306, usuario `root`, clave `museo2026`) están en [`bd/README.md`](bd/README.md), junto con consultas listas en `bd/consultas_workbench.sql`.

Si prefieres un MySQL instalado en el computador, usa el mismo usuario y clave (o cambia las variables `DB_USER` y `DB_PASSWORD`). Las bases se crean solas al arrancar (`createDatabaseIfNotExist=true`).

### 2. Microservicios

Abre la carpeta en IntelliJ IDEA o Spring Tool Suite y ejecuta, en este orden:

1. `CatalogoApplication` (ms-catalogo)
2. `UsuariosApplication` (ms-usuarios)
3. `PedidosApplication` (ms-pedidos, necesita al catálogo encendido)

O desde la terminal, en cada carpeta: `mvn spring-boot:run`.

La primera vez se cargan los datos del caso: 14 museos, 12 productos y 3 cuentas de prueba.

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@museo.cl | museo123 |
| Investigador | investigador@museo.cl | inves2026 |
| Cliente | estudiante@museo.cl | duoc2026 |

### 3. Documentación de la API

- http://localhost:8081/swagger-ui.html
- http://localhost:8082/swagger-ui.html
- http://localhost:8083/swagger-ui.html

También está la colección `postman/AppMuseo.postman_collection.json`.

### 4. Pruebas

```bash
mvn test
```

## Endpoints principales

**ms-usuarios** (`/api/usuarios`)
- `POST /registro` · `POST /login` · `GET /` · `GET /{id}` · `GET /correo/{correo}` · `PUT /{id}` · `PATCH /{id}/rol` · `DELETE /{id}`

**ms-catalogo**
- `GET /api/museos?pais=&tipo=` · `GET /api/museos/{id}` · `GET /api/museos/codigo/MUSEO-5` · `POST/PUT/DELETE /api/museos`
- `GET /api/productos?categoria=&q=` · `GET /api/productos/{id}` · `GET /api/productos/codigo/MDM-001` · `POST/PUT/DELETE /api/productos`
- `POST /api/productos/{id}/descontar-stock` · `POST /api/productos/{id}/reponer-stock`
- `GET /api/qr/{codigo}`: devuelve el museo o producto del código escaneado

**ms-pedidos**
- `GET /api/envios/zonas?tipo=NACIONAL` · `POST /api/envios/cotizar`
- `POST /api/pedidos` · `GET /api/pedidos?correo=` · `GET /api/pedidos/{id}` · `GET /api/pedidos/{id}/boleta` · `PATCH /api/pedidos/{id}/avanzar` · `DELETE /api/pedidos/{id}`

## Reglas de negocio

- La app manda solo qué compra y cuánto. **Precios, envío y totales los calcula el servidor**, así no se pueden alterar desde el teléfono.
- El stock nunca queda negativo. De un curso se compra **un solo cupo**.
- Si una compra falla a la mitad (ej: el tercer producto no tiene stock), **se devuelve el stock ya descontado** de los anteriores.
- Envío: `(tarifa base + costo por kg × kilos) × recargo`. Se cobra por kilo completo (mínimo 1 kg), con un máximo de 30 kg. Express cuesta 60 % más y tarda la mitad.
- **Envío estándar gratis** dentro de Chile en compras desde **$50.000**.
- Boleta: los precios incluyen IVA. `neto = total / 1,19` e `IVA = total − neto`.
- El pedido avanza: Pendiente → Confirmada → Enviada → Entregada. Solo se puede anular antes de ser enviado; al anularlo vuelve el stock.
- Las contraseñas se guardan con BCrypt. Una cuenta nueva siempre parte como Cliente y solo un administrador cambia roles.
- Si al iniciar sesión se elige un tipo de cuenta que no corresponde, el servidor responde 403.

## Códigos de respuesta

| Código | Cuándo |
|---|---|
| 200 / 201 / 204 | Todo bien / creado / eliminado |
| 400 | Datos inválidos (incluye el detalle por campo en `campos`) |
| 401 | Correo o contraseña incorrectos |
| 403 | La cuenta no es del tipo elegido |
| 404 | No existe el recurso |
| 409 | Se rompe una regla de negocio (sin stock, correo repetido...) |
| 503 | ms-pedidos no puede comunicarse con ms-catalogo |
