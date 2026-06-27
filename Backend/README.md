# Food Store — Backend

Sistema de gestión de pedidos de comida desarrollado en **Java con JPA/Hibernate y base de datos H2** como parte del TPI de Programación III (UTN).
La interacción se realiza completamente a través de un **menú interactivo de consola**.

## Tecnologías

| Tecnología           | Versión         |
|----------------------|-----------------|
| Java                 | 17+             |
| Gradle               | 9.5             |
| Hibernate ORM        | 6.6.4.Final     |
| Jakarta Persistence  | 3.0             |
| H2 Database          | 2.3.232         |
| Lombok               | 1.18.46         |

## Estructura del proyecto

```
src/main/java/com/tup/programacion3/
├── entities/
│   ├── Base.java              ← @MappedSuperclass con id, eliminado, createdAt
│   ├── Categoria.java
│   ├── Producto.java
│   ├── Usuario.java
│   ├── Pedido.java
│   └── DetallePedido.java
├── enums/
│   ├── Rol.java               ← ADMIN, USUARIO
│   ├── Estado.java            ← PENDIENTE, CONFIRMADO, TERMINADO, CANCELADO
│   └── FormaPago.java         ← TARJETA, TRANSFERENCIA, EFECTIVO
├── interfaces/
│   └── Calculable.java        ← define calcularTotal():void
├── repository/
│   ├── BaseRepository.java    ← CRUD genérico abstracto
│   ├── CategoriaRepository.java
│   ├── ProductoRepository.java
│   ├── UsuarioRepository.java
│   └── PedidoRepository.java
├── util/
│   ├── JPAUtil.java           ← Singleton del EntityManagerFactory
│   └── DataSeeder.java
└── Main.java                  ← Menú principal de consola
```

## Cómo ejecutar

### Requisitos
- JDK 17 o superior
- No requiere instalación adicional — H2 es embebida y Gradle descarga las dependencias

### Pasos

**Desde línea de comandos:**
```bash
cd Backend
gradlew.bat run        # Windows
./gradlew run          # Linux/Mac
```

**Desde IntelliJ IDEA:**
- Abrir la carpeta `Backend` como proyecto Gradle
- Ejecutar la clase `Main.java`

### Base de datos H2

La base se crea automáticamente en `./data/jpa_db` al iniciar la aplicación.

Para acceder a la consola web mientras el programa está corriendo:
- URL: `http://localhost:8082`
- JDBC URL: `jdbc:h2:file:./data/jpa_db;AUTO_SERVER=TRUE`
- Usuario: `sa`
- Contraseña: *(vacía)*

## Menú de consola

```
=== FOOD STORE — MENU PRINCIPAL ===
1. Gestionar Categorias
2. Gestionar Productos
3. Gestionar Usuarios
4. Gestionar Pedidos
5. Reportes
0. Salir
```

### Categorías
- Alta (nombre obligatorio, descripción opcional)
- Modificar (campo vacío conserva valor anterior)
- Baja lógica (`eliminado = true`)
- Listar activas

### Productos
- Alta (requiere categoría activa, precio > 0, stock >= 0)
- Modificar (campo vacío conserva valor anterior)
- Baja lógica
- Listar activos (ID, nombre, precio, stock, categoría)

### Usuarios
- Alta (valida mail único con `buscarPorMail`)
- Modificar (campo vacío conserva valor anterior, valida mail si cambia)
- Baja lógica (sus pedidos permanecen en el sistema)
- Listar activos
- Buscar por mail

### Pedidos
- **Alta** — flujo atómico en una única transacción:
  - Seleccionar usuario y forma de pago
  - Agregar productos validando disponibilidad y stock
  - Calcula subtotales y total automáticamente
  - Reduce stock al confirmar
  - Rollback completo ante cualquier error
- Cambiar estado (PENDIENTE → CONFIRMADO → TERMINADO → CANCELADO)
- Baja lógica (stock NO se restaura)
- Listar activos
- Filtrar por usuario
- Filtrar por estado

### Reportes
- Productos por categoría (JPQL navegando `Categoria → c.productos`)
- Pedidos por usuario (JPQL navegando `Usuario → u.pedidos`)
- Pedidos por estado
- Total facturado (suma de pedidos en estado TERMINADO, formato `$%.2f`)

## Arquitectura — Patrón Repository

```
Main.java          →  menú e interacción con el usuario
Repository         →  lógica de persistencia (JPA/EntityManager)
Entities           →  modelo de dominio
JPAUtil            →  singleton del EntityManagerFactory
```

`BaseRepository<T>` es una clase abstracta genérica con las operaciones CRUD comunes:

| Método                    | Descripción                                                    |
|---------------------------|----------------------------------------------------------------|
| `guardar(T entity)`       | `persist()` si id es null, `merge()` si ya tiene id           |
| `buscarPorId(Long id)`    | Retorna `Optional<T>`                                          |
| `listarActivos()`         | JPQL con `WHERE e.eliminado = false`                           |
| `eliminarLogico(Long id)` | Baja lógica: `setEliminado(true)` + `merge()`, retorna boolean |

Cada método abre su propio `EntityManager` y lo cierra en `finally`.

## Enumerados

| Enum        | Valores                                        |
|-------------|------------------------------------------------|
| `Rol`       | `ADMIN`, `USUARIO`                             |
| `Estado`    | `PENDIENTE`, `CONFIRMADO`, `TERMINADO`, `CANCELADO` |
| `FormaPago` | `TARJETA`, `TRANSFERENCIA`, `EFECTIVO`         |

## Decisiones de diseño

**Borrado lógico:** todas las bajas establecen `eliminado = true`. El registro permanece en la BD para preservar el historial.

**Transacción atómica en alta de pedido:** todo el alta (crear pedido, agregar detalles, reducir stock) ocurre en un único `EntityManager` con una única transacción. Si falla cualquier paso se hace rollback completo.

**Lista temporal en alta de pedido:** los productos se acumulan en una lista de `long[]` (id, cantidad) antes de abrir la transacción. Dentro de la transacción se recupera cada `Producto` con `em.find()` para que quede gestionado y el cambio de stock se sincronice automáticamente en el commit.

**JPAUtil como Singleton:** crear un `EntityManagerFactory` es costoso. `JPAUtil` garantiza que toda la aplicación comparte una sola instancia.
