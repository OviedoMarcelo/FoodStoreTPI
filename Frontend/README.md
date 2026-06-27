# Food Store — Frontend

Aplicación web para una tienda de comidas desarrollada con **TypeScript + Vite + HTML5 + CSS3** como parte del TPI de Programación III (UTN).

## Tecnologías

- TypeScript (sin frameworks)
- Vite (bundler y servidor de desarrollo)
- HTML5 + CSS3 con variables personalizadas
- localStorage para persistencia del carrito y sesión
- Datos servidos desde archivos JSON estáticos (`public/data/`)

## Estructura de carpetas

```
Frontend/
├── public/
│   ├── data/
│   │   ├── categorias.json
│   │   ├── productos.json
│   │   ├── pedidos.json
│   │   └── usuarios.json
│   └── images/           # Imágenes de productos y categorías
├── src/
│   ├── pages/
│   │   ├── auth/
│   │   │   ├── login/        # Login de usuario
│   │   │   └── registro/     # Registro de nuevo cliente
│   │   ├── store/
│   │   │   ├── home/         # Catálogo de productos
│   │   │   ├── productDetail/# Detalle de producto
│   │   │   └── cart/         # Carrito de compras
│   │   ├── client/
│   │   │   └── orders/       # Mis pedidos (cliente)
│   │   └── admin/
│   │       ├── adminHome/    # Dashboard admin
│   │       ├── categories/   # CRUD categorías
│   │       ├── products/     # CRUD productos
│   │       └── orders/       # Gestión de pedidos
│   ├── types/                # Interfaces TypeScript
│   ├── utils/                # Funciones utilitarias
│   └── style.css             # Estilos globales
```

## Cómo ejecutar

```bash
cd Frontend
npm install
npm run dev
```

Accedé desde el navegador a `http://localhost:5173`.

## Usuarios de prueba

| Rol    | Email                  | Contraseña |
|--------|------------------------|------------|
| Admin  | admin@foodstore.com    | admin123   |
| Cliente | juan@gmail.com        | juan123    |
| Cliente | maria@gmail.com       | maria123   |
| Cliente | carlos@gmail.com      | carlos123  |

## Roles y permisos

| Acción                  | Admin     | Cliente  |
|-------------------------|-----------|----------|
| Panel de administración | Sí        | No       |
| Ver catálogo            | Sí        | Sí       |
| Detalle de producto     | Sí        | Sí       |
| Carrito y compras       | No aplica | Sí       |
| Ver mis pedidos         | No aplica | Sí       |
| Ver todos los pedidos   | Sí        | No       |
| CRUD categorías         | Sí        | No       |
| CRUD productos          | Sí        | No       |

## Funcionalidades principales

### Autenticación
- Login con validación de credenciales contra `usuarios.json`
- Registro de nuevos clientes con validación de email y contraseña (mínimo 6 caracteres)
- Sesión persistida en localStorage
- Redirección automática según rol al iniciar sesión

### Tienda (cliente y admin)
- Catálogo con filtro por categoría y búsqueda por nombre/descripción
- Ordenamiento por nombre (A-Z / Z-A) y precio (mayor/menor)
- Detalle de producto con selector de cantidad y botón agregar al carrito

### Carrito
- Persistencia en localStorage entre sesiones
- Modificar cantidad con validación de stock disponible
- Eliminar productos individuales y vaciar carrito completo
- Resumen con subtotal, envío y total
- **Costo de envío fijo: $1.000** (constante `ENVIO` en `cart.ts`)
- Checkout con formulario: teléfono, dirección, forma de pago y notas opcionales

### Mis Pedidos (cliente)
- Combina pedidos del JSON (`pedidos.json`) y pedidos realizados en sesión (localStorage)
- Filtrados por usuario logueado, ordenados por fecha descendente
- Modal con detalle completo de cada pedido

### Panel Admin
- **Dashboard**: conteo de categorías, productos, pedidos y productos disponibles
- **Categorías**: CRUD completo en memoria (crear, editar, eliminar)
- **Productos**: CRUD completo en memoria con selector de categoría
- **Pedidos**: vista de todos los pedidos con filtro por estado y cambio de estado

> Las operaciones de CRUD se realizan en memoria. Al recargar la página el estado vuelve al JSON original. La persistencia real se implementa en la Parte 2 (backend Java).

## Notas técnicas

- `checkAuth(role)` valida sesión y redirige si el rol no coincide. Acepta un rol o un array de roles.
- Los datos se obtienen con `fetch()` a los JSON en `public/data/` (client-side).
- Los nuevos pedidos se guardan en `localStorage` con `saveOrder()`.
- El `id` de nuevos usuarios y pedidos se genera con `Date.now().toString()`.
