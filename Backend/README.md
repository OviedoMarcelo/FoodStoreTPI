# Parcial JPA — Programación III
**Tecnicatura Universitaria en Programación · UTN**
**Alumno:** Marcelo Gastón Oviedo Maccari

---

## Descripción del proyecto

Sistema de gestión de catálogo desarrollado en Java con JPA/Hibernate y base de datos H2.
Permite realizar operaciones ABM (Alta, Baja, Modificación) sobre **Categorías** y **Productos**
a través de un menú interactivo por consola, e incluye una consulta JPQL para filtrar productos
por categoría.

El proyecto extiende el TP base de la Unidad 8, agregando una capa de repositorios genéricos
que encapsulan toda la lógica de persistencia, sin modificar ninguna clase del TP original.

---

## Tecnologías utilizadas

| Tecnología | Versión |
|---|---|
| Java | 17+ |
| Gradle | 9.5 |
| Hibernate ORM | 6.6.4.Final |
| Jakarta Persistence | 3.0 |
| H2 Database | 2.3.232 |
| Lombok | Última estable |

---

## Estructura del proyecto

```
src/main/java/com/tup/programacion3/
├── entities/               ← Entidades JPA (no modificadas del TP base)
│   ├── Base.java           ← @MappedSuperclass con id, eliminado, createdAt
│   ├── Categoria.java
│   ├── Producto.java
│   ├── Usuario.java
│   ├── Pedido.java
│   └── DetallePedido.java
├── enums/                  ← Enums del dominio (no modificados)
│   ├── Rol.java
│   ├── Estado.java
│   └── FormaPago.java
├── interfaces/             ← Interfaces del dominio (no modificadas)
│   └── Calculable.java
├── repository/             ← NUEVO: capa de persistencia
│   ├── BaseRepository.java      ← Repositorio genérico con CRUD común
│   ├── CategoriaRepository.java ← Extiende BaseRepository<Categoria>
│   └── ProductoRepository.java  ← Extiende BaseRepository<Producto> + buscarPorCategoria()
├── util/
│   ├── JPAUtil.java        ← Singleton del EntityManagerFactory
│   └── DataSeeder.java     ← Datos de prueba del TP base
└── Main.java               ← NUEVO: menú interactivo de consola
```

---

## Arquitectura — Patrón Repository

El proyecto implementa el patrón **Repository** para separar responsabilidades:

```
Main.java          →  interacción con el usuario (menú, Scanner)
Repository         →  lógica de persistencia (JPA, EntityManager)
Entities           →  modelo de dominio
JPAUtil            →  singleton del EntityManagerFactory
```

`BaseRepository<T>` es una clase abstracta genérica que implementa las operaciones CRUD comunes.
Cada método abre y cierra su propio `EntityManager`, garantizando un manejo correcto de recursos.

| Método | Descripción |
|---|---|
| `guardar(T entity)` | INSERT o UPDATE usando `merge()`. Retorna la entidad con id asignado |
| `buscarPorId(Long id)` | SELECT por PK. Retorna `Optional<T>` |
| `listarActivos()` | SELECT con `WHERE eliminado = false` usando JPQL |
| `eliminarLogico(Long id)` | Baja lógica: `setEliminado(true)` + `merge()`. Retorna `boolean` |

---

## Funcionalidades implementadas

### ABM de Categorías
- **Alta:** solicita nombre (obligatorio) y descripción. Muestra el ID generado.
- **Baja lógica:** marca `eliminado = true`. Valida que el ID exista y no esté ya dado de baja.
- **Modificación:** muestra valores actuales. Campo vacío conserva el valor anterior.
- **Listado:** muestra todas las categorías activas con ID, nombre y descripción.

### ABM de Productos
- **Alta:** lista categorías activas para seleccionar. Solicita nombre, descripción, precio (> 0) y stock (>= 0). Muestra ID generado y categoría asignada.
- **Baja lógica:** marca `eliminado = true`. Muestra el nombre del producto afectado.
- **Modificación:** muestra valores actuales. Precio y stock conservan valor anterior si se deja vacío. Valida precio > 0 y stock >= 0.
- **Listado:** muestra todos los productos activos con ID, nombre, precio, stock y categoría.

### Reportes
- **Productos por categoría:** lista categorías activas, el usuario elige una, y se muestran sus productos activos usando JPQL con parámetro nombrado `:categoriaId`.

---

## Instrucciones para ejecutar

### Requisitos previos
- JDK 17 o superior instalado
- No requiere instalación adicional — H2 es embebida y se descarga con Gradle

### Pasos

**1. Clonar o descomprimir el proyecto**
```bash
cd Oviedo_Marcelo_parcial_2
```

**2. Ejecutar con Gradle**
```bash
./gradlew run
# En Windows:
gradlew.bat run
```

**3. O ejecutar directamente desde IntelliJ IDEA**
- Abrir el proyecto
- Ejecutar la clase `Main.java`

### Base de datos

La base de datos H2 se crea automáticamente en `./data/jpa_db` al iniciar la aplicación.
No requiere configuración adicional.

Para acceder a la consola web de H2 mientras el programa está corriendo:
- URL: `http://localhost:8082`
- JDBC URL: `jdbc:h2:file:./data/jpa_db;AUTO_SERVER=TRUE`
- Usuario: `sa`
- Contraseña: *(vacía)*

---

## Configuración JPA — persistence.xml

La unidad de persistencia se llama `programacion3UTN` y está configurada con:

- **Driver:** `org.h2.Driver`
- **Base de datos:** archivo local `./data/jpa_db`
- **Estrategia DDL:** `update` — mantiene los datos entre ejecuciones
- **SQL visible:** `hibernate.show_sql = true` para seguimiento en consola

---

## Decisiones de diseño

### Borrado lógico
Todas las bajas son **lógicas** — se establece `eliminado = true` en vez de borrar el registro.
Esto preserva la integridad referencial y el historial de datos.
El campo `eliminado` está definido en `Base` y es heredado por todas las entidades.

### JPAUtil como Singleton
Crear un `EntityManagerFactory` es costoso (lee el XML, conecta con la BD, inicializa Hibernate).
`JPAUtil` garantiza que toda la aplicación comparte una sola instancia, mejorando el rendimiento.

### Optional en buscarPorId()
El método retorna `Optional<T>` en vez de `null` para forzar el manejo explícito del caso
"no encontrado" en el llamador, evitando `NullPointerException`.

### Set en relaciones @OneToMany
Las colecciones `@OneToMany` usan `Set<>` con `HashSet<>`, siguiendo la especificación JPA
y las mejores prácticas de Hibernate para relaciones de colección.
