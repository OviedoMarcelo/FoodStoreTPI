package com.tup.programacion3;

import com.tup.programacion3.entities.*;
import com.tup.programacion3.enums.Estado;
import com.tup.programacion3.enums.FormaPago;
import com.tup.programacion3.enums.Rol;
import com.tup.programacion3.repository.*;
import com.tup.programacion3.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CategoriaRepository categoriaRepo = new CategoriaRepository();
    private static final ProductoRepository productoRepo = new ProductoRepository();
    private static final UsuarioRepository usuarioRepo = new UsuarioRepository();
    private static final PedidoRepository pedidoRepo = new PedidoRepository();

    public static void main(String[] args) throws SQLException, UnsupportedEncodingException {
        //Encoding para poder ver palabras con tildes y otros ascii
        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        //Server H2 console
        org.h2.tools.Server webServer = org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
        webServer.start();
        System.out.println("H2 Console: http://localhost:8082");

        int opcion = -1;
        do {
            System.out.println("\n=== FOOD STORE — MENU PRINCIPAL ===");
            System.out.println("1. Gestionar Categorias");
            System.out.println("2. Gestionar Productos");
            System.out.println("3. Gestionar Usuarios");
            System.out.println("4. Gestionar Pedidos");
            System.out.println("5. Reportes");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> menuCategorias();
                case 2 -> menuProductos();
                case 3 -> menuUsuarios();
                case 4 -> menuPedidos();
                case 5 -> menuReportes();
                case 0 -> {
                    JPAUtil.close();
                    System.out.println("Hasta luego!");
                }
                default -> System.out.println("Opcion invalida");
            }
        } while (opcion != 0);
    }

//=================MENÚ CATEGORÍA Y SUS OPERACIONES ==================//

    private static void menuCategorias() {
        int opcion;
        do {
            System.out.println("\n=== MENU DE CATEGORIAS ===");
            System.out.println("1. Alta");
            System.out.println("2. Baja lógica");
            System.out.println("3. Modificación");
            System.out.println("4. Listar categorías (solo vigentes)");
            System.out.println("0. Volver...");
            System.out.print("Opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar buffer

            switch (opcion) {
                case 1 -> altaCategoria();
                case 2 -> bajaCategoria();
                case 3 -> modificacionCategoria();
                case 4 -> listarCategorias();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opcion invalida");
            }
        } while (opcion != 0);
    }

    private static void altaCategoria() {
        System.out.println("=== Alta nueva Categoria ===");
        System.out.print("Ingrese el nombre de la categoria: ");
        String nombre = scanner.nextLine();

        if (nombre.isBlank()) {
            System.out.println("Error: el nombre no puede estar vacio.");
            return;
        }

        System.out.print("Ingrese la descripcion: ");
        String descripcion = scanner.nextLine();

        Categoria nueva = Categoria.builder()
                .categoria(nombre)
                .descripcion(descripcion)
                .build();

        Categoria guardada = categoriaRepo.guardar(nueva);
        System.out.println("Categoria creada con ID: " + guardada.getId());
    }

    private static void bajaCategoria() {
        System.out.println("=== Baja la Categoria ===");
        System.out.print("Ingrese el ID de la categoria que quiere eliminar: ");
        String idIngresado = scanner.nextLine();

        if (idIngresado.isBlank()) {
            System.out.println("Error: no se ingreso un valor.");
            return;
        }

        try {
            Long id = Long.parseLong(idIngresado);
            Optional<Categoria> opt = categoriaRepo.buscarPorId(id);
            if (opt.isEmpty()) {
                System.out.println("Error: no existe categoria con ese ID.");
                return;
            }
            String nombre = opt.get().getCategoria();
            if (categoriaRepo.eliminarLogico(id)) {
                System.out.println("Categoria '" + nombre + "dada de baja correctamente.");
            } else {
                System.out.println("ID de categoría" + id + "ya esta dado de baja.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un numero.");
        }
    }

    private static void modificacionCategoria() {
        System.out.println("=== Modificacion de Categoria ===");
        listarCategorias();
        System.out.println("Ingrese el id de la categoria que quiere modificar: ");
        String idIngresado = scanner.nextLine();
        if (idIngresado.isBlank()) {
            System.out.println("Error: no se ingreso un valor.");
            return;
        }
        try {
            Long id = Long.parseLong(idIngresado);
            Optional<Categoria> categoriaMod = categoriaRepo.buscarPorId(id);
            if (categoriaMod.isEmpty()) {
                System.out.println("Error: no existe categoria con ese ID.");
                return;
            }
            System.out.println("Categoria a modificar: ");
            System.out.println("Categoría: " + categoriaMod.get().getCategoria() + "|| Descripcion: " + categoriaMod.get().getDescripcion());
            System.out.print("Ingrese el nuevo nombre de la categoria: ");
            String nuevoNombre = scanner.nextLine();
            System.out.print("Ingrese la nueva descripcion de la categoria: ");
            String nuevaDescripcion = scanner.nextLine();
            if (!nuevoNombre.isBlank()) {
                categoriaMod.get().setCategoria(nuevoNombre);
            }
            if (!nuevaDescripcion.isBlank()) {
                categoriaMod.get().setDescripcion(nuevaDescripcion);
            }
            categoriaRepo.guardar(categoriaMod.get());
            System.out.println("Categoria modificada correctamente.");

        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un numero.");
        }
    }


    private static void listarCategorias() {
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias activas.");
            return;
        }
        System.out.println("=========Categorias activas===========");
        categorias.forEach(c -> System.out.println("ID: " + c.getId() + " | Nombre: " + c.getCategoria() + " | Descripcion: " + c.getDescripcion()));
        System.out.println("=====================================");

    }

    //Metodo auxiliar para seleccionar una categoría y validar que sea correcta puede devolver null

    private static Categoria seleccionarCategoria() {
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias activas.");
            return null;
        }
        listarCategorias();
        System.out.print("Ingrese el ID de la categoria: ");
        String input = scanner.nextLine();
        if (input.isBlank()) return null;
        try {
            Long id = Long.parseLong(input);
            return categoriaRepo.buscarPorId(id)
                    .orElse(null);
        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un numero.");
            return null;
        }
    }


//=================MENÚ CATEGORÍA Y SUS OPERACIONES ==================//

    private static void menuProductos() {
        int opcion;
        do {
            System.out.println("\n=== MENU DE PRODUCTOS ===");
            System.out.println("1. Alta");
            System.out.println("2. Baja lógica");
            System.out.println("3. Modificación");
            System.out.println("4. Listar productos (solo vigentes)");
            System.out.println("0. Volver...");
            System.out.print("Opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar buffer

            switch (opcion) {
                case 1 -> altaProducto();
                case 2 -> bajaProducto();
                case 3 -> modificacionProducto();
                case 4 -> listarProductos();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opcion invalida");
            }
        } while (opcion != 0);
    }

    private static void listarProductos() {
        List<Producto> productos = productoRepo.listarActivos();
        if (productos.isEmpty()) {
            System.out.println("Actualmente no hay productos.");
        }
        System.out.println("========= Productos activos ===========");
        System.out.printf("%-5s %-30s %-40s %10s %8s %-15s%n",
                "ID", "Nombre", "Descripcion", "Precio", "Stock", "Categoria");
        System.out.println("-".repeat(110));
        productos.forEach(p -> System.out.printf(
                "%-5s %-30s %-40s %10s %8s %-15s%n",
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getStock(),
                p.getCategoria().getCategoria() //Para esto tuve que poner eager
        ));
        System.out.println("=====================================");

    }

    private static void modificacionProducto() {
        System.out.println("====== Modificacion de productos ======");
        listarProductos();
        System.out.println("Ingrese el id del producto a modificar");
        String idIngresado = scanner.nextLine();
        if (idIngresado.isBlank()) {
            System.out.println("Error: no se ingreso un valor.");
            return;
        }
        try {
            Long id = Long.parseLong(idIngresado);
            Optional<Producto> productoMod = productoRepo.buscarPorId(id);
            if (productoMod.isEmpty()) {
                System.out.println("Error: no existe producto con ese ID.");
                return;
            }
            System.out.println("Producto a modificar: ");
            System.out.println("Nombre: " + productoMod.get().getNombre()
                    + "|| Descripcion: " + productoMod.get().getDescripcion()
                    + "|| Precio: " + productoMod.get().getPrecio()
                    + "|| Stock: " + productoMod.get().getStock()
                    + "|| Categoria: " + productoMod.get().getCategoria().getCategoria());
            System.out.print("Ingrese el nuevo nombre del producto: ");
            String nuevoNombre = scanner.nextLine();
            System.out.print("Ingrese la nueva descripcion del producto: ");
            String nuevaDescripcion = scanner.nextLine();
            System.out.print("Ingrese el nuevo precio: ");
            String precioInput = scanner.nextLine();
            if (!precioInput.isBlank()) {
                double nuevoPrecio = Double.parseDouble(precioInput);
                if (nuevoPrecio <= 0) {
                    System.out.println("Error: el precio debe ser mayor a 0.");
                    return;
                }
                productoMod.get().setPrecio(nuevoPrecio);
            }
            System.out.print("Ingrese el nuevo stock (Enter para mantener " + productoMod.get().getStock() + "): ");
            String stockInput = scanner.nextLine();
            if (!stockInput.isBlank()) {
                int nuevoStock = Integer.parseInt(stockInput);
                if (nuevoStock < 0) {
                    System.out.println("Error: el stock no puede ser negativo.");
                    return;
                }
                productoMod.get().setStock(nuevoStock);
            }
            if (!nuevoNombre.isBlank()) {
                productoMod.get().setNombre(nuevoNombre);
            }
            if (!nuevaDescripcion.isBlank()) {
                productoMod.get().setDescripcion(nuevaDescripcion);
            }
            productoRepo.guardar(productoMod.get());
            System.out.println("Producto modificado correctamente.");

        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un numero.");
        }
    }


    private static void bajaProducto() {
        System.out.println("=== Baja el Producto ===");
        System.out.print("Ingrese el ID del producto que quiere eliminar: ");
        String idIngresado = scanner.nextLine();

        if (idIngresado.isBlank()) {
            System.out.println("Error: no se ingreso un valor.");
            return;
        }

        try {
            Long id = Long.parseLong(idIngresado);
            Optional<Producto> opt = productoRepo.buscarPorId(id);
            if (opt.isEmpty()) {
                System.out.println("Error: no existe producto con ese ID.");
                return;
            }
            String nombre = opt.get().getNombre();
            if (productoRepo.eliminarLogico(id)) {
                System.out.println("El producto " + nombre + " fue dado de baja correctamente.");
            } else {
                System.out.println("ID de producto " + id + " ya esta dado de baja.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un numero.");
        }
    }

    private static void altaProducto() {
        System.out.println("=== Alta de Producto ===");
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias activas.");
            return;
        }
        Categoria categoriaSeleccionada = seleccionarCategoria();
        if (categoriaSeleccionada == null) {
            System.out.println("La categoria seleccionada no existe.");
            return;
        }
        System.out.print("Ingrese el nombre del producto");
        String nombreProducto = scanner.nextLine();
        if (nombreProducto.isBlank()) {
            System.out.println("Error: no se ingreso un valor de nombre.");
            return;
        }
        System.out.println("Ingrese el descripcion del producto");
        String descripcionProducto = scanner.nextLine();
        if (descripcionProducto.isBlank()) {
            System.out.println("Error: no se ingreso un valor de descripcion.");
            return;
        }
        System.out.println("Ingrese el precio del producto");
        double precioProducto = scanner.nextDouble();
        scanner.nextLine();
        if (precioProducto <= 0) {
            System.out.println("Error: el precio del producto debe ser mayor a 0.");
            return;
        }
        System.out.println("Ingrese el stock del producto");
        int stock = scanner.nextInt();
        scanner.nextLine();
        if (stock < 0) {
            System.out.println("Error: el stock del producto debe ser mayor o igual a 0.");
            return;
        }
        Producto productoNuevo = Producto.builder()
                .nombre(nombreProducto)
                .descripcion(descripcionProducto)
                .precio(precioProducto)
                .stock(stock)
                .categoria(categoriaSeleccionada)
                .build();
        Producto productoGuardado = productoRepo.guardar(productoNuevo);
        System.out.println("Producto creado correctamente.");
        System.out.println("===============================");
        //Aca si quiero acceder a productoGuardado.getCategoría tengo problema de Lazy vs eager
        System.out.println("ID: " + productoGuardado.getId() + " || Categoria asignada: " + categoriaSeleccionada.getCategoria());
        System.out.println("===============================");

    }


//=================MENÚ USUARIOS Y SUS OPERACIONES ==================//

    private static void menuUsuarios() {
        int opcion;
        do {
            System.out.println("\n=== MENU DE USUARIOS ===");
            System.out.println("1. Alta");
            System.out.println("2. Modificar");
            System.out.println("3. Baja logica");
            System.out.println("4. Listar usuarios");
            System.out.println("5. Buscar por mail");
            System.out.println("0. Volver");
            System.out.print("Opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> altaUsuario();
                case 2 -> modificarUsuario();
                case 3 -> bajaUsuario();
                case 4 -> listarUsuarios();
                case 5 -> buscarUsuarioPorMail();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opcion invalida");
            }
        } while (opcion != 0);
    }

    private static void altaUsuario() {
        System.out.println("=== Alta de Usuario ===");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("Mail: ");
        String mail = scanner.nextLine();

        if (nombre.isBlank() || apellido.isBlank() || mail.isBlank()) {
            System.out.println("Error: nombre, apellido y mail son obligatorios.");
            return;
        }

        // Validar que el mail no esté ya en uso
        if (usuarioRepo.buscarPorMail(mail).isPresent()) {
            System.out.println("Error: ya existe un usuario activo con ese mail.");
            return;
        }

        System.out.print("Celular (opcional, Enter para omitir): ");
        String celular = scanner.nextLine();
        System.out.print("Contrasena: ");
        String contrasena = scanner.nextLine();

        System.out.println("Rol: 1. ADMIN  2. USUARIO");
        System.out.print("Opcion: ");
        int rolOpcion = scanner.nextInt();
        scanner.nextLine();
        Rol rol = (rolOpcion == 1) ? Rol.ADMIN : Rol.USUARIO;

        Usuario nuevo = Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .mail(mail)
                .celular(celular.isBlank() ? null : celular)
                .contrasena(contrasena)
                .rol(rol)
                .build();

        Usuario guardado = usuarioRepo.guardar(nuevo);
        System.out.println("Usuario creado con ID: " + guardado.getId());
    }

    private static void modificarUsuario() {
        System.out.println("=== Modificar Usuario ===");
        listarUsuarios();
        System.out.print("Ingrese el ID del usuario a modificar: ");
        String input = scanner.nextLine();
        if (input.isBlank()) { System.out.println("Error: no se ingreso un valor."); return; }

        try {
            Long id = Long.parseLong(input);
            Optional<Usuario> opt = usuarioRepo.buscarPorId(id);
            if (opt.isEmpty() || opt.get().isEliminado()) {
                System.out.println("Error: no existe usuario activo con ese ID.");
                return;
            }
            Usuario u = opt.get();
            System.out.println("Valores actuales — Nombre: " + u.getNombre() + " | Apellido: " + u.getApellido()
                    + " | Mail: " + u.getMail() + " | Celular: " + u.getCelular() + " | Rol: " + u.getRol());

            System.out.print("Nuevo nombre (Enter para conservar): ");
            String nombre = scanner.nextLine();
            System.out.print("Nuevo apellido (Enter para conservar): ");
            String apellido = scanner.nextLine();
            System.out.print("Nuevo mail (Enter para conservar): ");
            String mail = scanner.nextLine();
            System.out.print("Nuevo celular (Enter para conservar): ");
            String celular = scanner.nextLine();
            System.out.print("Nueva contrasena (Enter para conservar): ");
            String contrasena = scanner.nextLine();

            if (!nombre.isBlank()) u.setNombre(nombre);
            if (!apellido.isBlank()) u.setApellido(apellido);
            if (!celular.isBlank()) u.setCelular(celular);
            if (!contrasena.isBlank()) u.setContrasena(contrasena);
            if (!mail.isBlank()) {
                // Si cambia el mail, validar que no lo use otro usuario activo
                Optional<Usuario> existente = usuarioRepo.buscarPorMail(mail);
                if (existente.isPresent() && !existente.get().getId().equals(id)) {
                    System.out.println("Error: ese mail ya esta en uso por otro usuario.");
                    return;
                }
                u.setMail(mail);
            }

            usuarioRepo.guardar(u);
            System.out.println("Usuario modificado correctamente.");
        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un numero.");
        }
    }

    private static void bajaUsuario() {
        System.out.println("=== Baja de Usuario ===");
        System.out.print("Ingrese el ID del usuario: ");
        String input = scanner.nextLine();
        if (input.isBlank()) { System.out.println("Error: no se ingreso un valor."); return; }

        try {
            Long id = Long.parseLong(input);
            Optional<Usuario> opt = usuarioRepo.buscarPorId(id);
            if (opt.isEmpty()) { System.out.println("Error: no existe usuario con ese ID."); return; }
            String nombre = opt.get().getNombre() + " " + opt.get().getApellido();
            if (usuarioRepo.eliminarLogico(id)) {
                System.out.println("Usuario '" + nombre + "' dado de baja. Sus pedidos permanecen en el sistema.");
            } else {
                System.out.println("Error: el usuario ya estaba dado de baja.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un numero.");
        }
    }

    private static void listarUsuarios() {
        List<Usuario> usuarios = usuarioRepo.listarActivos();
        if (usuarios.isEmpty()) { System.out.println("No hay usuarios activos."); return; }
        System.out.println("========= Usuarios activos =========");
        usuarios.forEach(u -> System.out.printf(
                "ID: %-5d | Nombre: %-20s | Mail: %-30s | Rol: %s%n",
                u.getId(), u.getNombre() + " " + u.getApellido(), u.getMail(), u.getRol()
        ));
        System.out.println("====================================");
    }

    private static void buscarUsuarioPorMail() {
        System.out.print("Ingrese el mail a buscar: ");
        String mail = scanner.nextLine();
        usuarioRepo.buscarPorMail(mail).ifPresentOrElse(
                u -> System.out.printf("ID: %d | Nombre: %s %s | Mail: %s | Celular: %s | Rol: %s%n",
                        u.getId(), u.getNombre(), u.getApellido(), u.getMail(), u.getCelular(), u.getRol()),
                () -> System.out.println("No existe usuario activo con ese mail.")
        );
    }

    // Metodo auxiliar para seleccionar un usuario activo
    private static Usuario seleccionarUsuario() {
        List<Usuario> usuarios = usuarioRepo.listarActivos();
        if (usuarios.isEmpty()) { System.out.println("No hay usuarios activos."); return null; }
        listarUsuarios();
        System.out.print("Ingrese el ID del usuario: ");
        String input = scanner.nextLine();
        if (input.isBlank()) return null;
        try {
            Long id = Long.parseLong(input);
            return usuarioRepo.buscarPorId(id).orElse(null);
        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un numero.");
            return null;
        }
    }

//=================MENÚ PEDIDOS Y SUS OPERACIONES ==================//

    private static void menuPedidos() {
        int opcion;
        do {
            System.out.println("\n=== MENU DE PEDIDOS ===");
            System.out.println("1. Alta de pedido");
            System.out.println("2. Cambiar estado");
            System.out.println("3. Baja logica");
            System.out.println("4. Listar pedidos");
            System.out.println("5. Pedidos por usuario");
            System.out.println("6. Pedidos por estado");
            System.out.println("0. Volver");
            System.out.print("Opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> altaPedido();
                case 2 -> cambiarEstadoPedido();
                case 3 -> bajaPedido();
                case 4 -> listarPedidos();
                case 5 -> pedidosPorUsuario();
                case 6 -> pedidosPorEstado();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opcion invalida");
            }
        } while (opcion != 0);
    }

    private static void altaPedido() {
        System.out.println("=== Alta de Pedido ===");

        // Seleccionar usuario
        Usuario usuario = seleccionarUsuario();
        if (usuario == null) { System.out.println("Error: no se selecciono un usuario valido."); return; }

        // Seleccionar forma de pago
        System.out.println("Forma de pago: 1. TARJETA  2. TRANSFERENCIA  3. EFECTIVO");
        System.out.print("Opcion: ");
        int pagoOpcion = scanner.nextInt();
        scanner.nextLine();
        FormaPago formaPago = switch (pagoOpcion) {
            case 1 -> FormaPago.TARJETA;
            case 2 -> FormaPago.TRANSFERENCIA;
            default -> FormaPago.EFECTIVO;
        };

        // Lista temporal: guardamos solo id y cantidad, no entidades de otro EM
        List<long[]> itemsTemp = new ArrayList<>(); // [productoId, cantidad]

        String continuar = "s";
        while (continuar.equalsIgnoreCase("s")) {
            listarProductos();
            System.out.print("ID del producto a agregar: ");
            String inputProd = scanner.nextLine();
            if (inputProd.isBlank()) { System.out.println("Operacion cancelada."); return; }

            try {
                Long prodId = Long.parseLong(inputProd);
                Optional<Producto> optProd = productoRepo.buscarPorId(prodId);
                if (optProd.isEmpty() || optProd.get().isEliminado()) {
                    System.out.println("Error: producto no encontrado o dado de baja.");
                } else {
                    Producto p = optProd.get();
                    if (!p.isDisponible()) {
                        System.out.println("Error: el producto '" + p.getNombre() + "' no esta disponible.");
                    } else {
                        System.out.print("Cantidad: ");
                        int cantidad = scanner.nextInt();
                        scanner.nextLine();
                        if (cantidad <= 0) {
                            System.out.println("Error: la cantidad debe ser mayor a 0.");
                        } else if (p.getStock() < cantidad) {
                            System.out.println("Error: stock insuficiente. Disponible: " + p.getStock());
                        } else {
                            itemsTemp.add(new long[]{prodId, cantidad});
                            System.out.println("Producto agregado: " + p.getNombre() + " x" + cantidad);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: ID invalido.");
            }

            System.out.print("Agregar otro producto? (s/n): ");
            continuar = scanner.nextLine();
        }

        if (itemsTemp.isEmpty()) {
            System.out.println("El pedido debe tener al menos un producto. Operacion cancelada.");
            return;
        }

        // Abrir un unico EntityManager y ejecutar todo en una sola transaccion
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // Recuperar el usuario gestionado en este EM
            Usuario usuarioGestionado = em.find(Usuario.class, usuario.getId());

            Pedido pedido = Pedido.crear(formaPago, usuarioGestionado);

            for (long[] item : itemsTemp) {
                Long prodId = item[0];
                int cantidad = (int) item[1];
                Producto producto = em.find(Producto.class, prodId);
                pedido.addDetallePedido(cantidad, producto);
                // Reducir stock: el producto esta gestionado, el cambio se sincroniza en commit
                producto.setStock(producto.getStock() - cantidad);
            }

            pedido.calcularTotal();
            em.persist(pedido); // cascade ALL persiste los DetallePedido

            em.getTransaction().commit();

            // Mostrar resumen
            System.out.println("\n=== Pedido creado exitosamente ===");
            System.out.println("ID: " + pedido.getId());
            System.out.println("Usuario: " + usuarioGestionado.getNombre() + " " + usuarioGestionado.getApellido());
            System.out.println("Forma de pago: " + formaPago);
            System.out.println("Fecha: " + pedido.getFecha());
            pedido.getDetallePedidos().forEach(d -> System.out.printf(
                    "  - %s x%d  Subtotal: $%.2f%n",
                    d.getProducto().getNombre(), d.getCantidad(), d.getSubtotal()
            ));
            System.out.printf("Total: $%.2f%n", pedido.getTotal());

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.out.println("Error al crear el pedido. Se hizo rollback: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private static void cambiarEstadoPedido() {
        System.out.println("=== Cambiar Estado de Pedido ===");
        System.out.print("Ingrese el ID del pedido: ");
        String input = scanner.nextLine();
        if (input.isBlank()) { System.out.println("Error: no se ingreso un valor."); return; }

        try {
            Long id = Long.parseLong(input);
            Optional<Pedido> opt = pedidoRepo.buscarPorId(id);
            if (opt.isEmpty() || opt.get().isEliminado()) {
                System.out.println("Error: no existe pedido activo con ese ID.");
                return;
            }
            Pedido pedido = opt.get();
            System.out.println("Estado actual: " + pedido.getEstado());
            System.out.println("1. PENDIENTE  2. CONFIRMADO  3. TERMINADO  4. CANCELADO");
            System.out.print("Nuevo estado: ");
            int estadoOp = scanner.nextInt();
            scanner.nextLine();
            Estado nuevoEstado = switch (estadoOp) {
                case 1 -> Estado.PENDIENTE;
                case 2 -> Estado.CONFIRMADO;
                case 3 -> Estado.TERMINADO;
                default -> Estado.CANCELADO;
            };
            pedido.setEstado(nuevoEstado);
            pedidoRepo.guardar(pedido);
            System.out.println("Pedido #" + id + " actualizado a: " + nuevoEstado);
        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un numero.");
        }
    }

    private static void bajaPedido() {
        System.out.println("=== Baja de Pedido ===");
        System.out.print("Ingrese el ID del pedido: ");
        String input = scanner.nextLine();
        if (input.isBlank()) { System.out.println("Error: no se ingreso un valor."); return; }

        try {
            Long id = Long.parseLong(input);
            Optional<Pedido> opt = pedidoRepo.buscarPorId(id);
            if (opt.isEmpty()) { System.out.println("Error: no existe pedido con ese ID."); return; }
            double total = opt.get().getTotal();
            if (pedidoRepo.eliminarLogico(id)) {
                System.out.printf("Pedido #%d dado de baja. Total: $%.2f. Stock NO restaurado.%n", id, total);
            } else {
                System.out.println("Error: el pedido ya estaba dado de baja.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un numero.");
        }
    }

    private static void listarPedidos() {
        List<Pedido> pedidos = pedidoRepo.listarActivos();
        if (pedidos.isEmpty()) { System.out.println("No hay pedidos activos."); return; }
        System.out.println("========= Pedidos activos =========");
        pedidos.forEach(p -> System.out.printf(
                "ID: %-5d | Fecha: %s | Estado: %-12s | Pago: %-15s | Total: $%.2f%n",
                p.getId(), p.getFecha(), p.getEstado(), p.getFormaPago(), p.getTotal()
        ));
        System.out.println("===================================");
    }

    private static void pedidosPorUsuario() {
        System.out.println("=== Pedidos por Usuario ===");
        Usuario usuario = seleccionarUsuario();
        if (usuario == null) { System.out.println("Error: no se selecciono un usuario valido."); return; }
        List<Pedido> pedidos = usuarioRepo.buscarPedidosPorUsuario(usuario.getId());
        if (pedidos.isEmpty()) {
            System.out.println("El usuario no tiene pedidos activos.");
            return;
        }
        pedidos.forEach(p -> System.out.printf(
                "ID: %-5d | Fecha: %s | Estado: %-12s | Pago: %-15s | Total: $%.2f%n",
                p.getId(), p.getFecha(), p.getEstado(), p.getFormaPago(), p.getTotal()
        ));
    }

    private static void pedidosPorEstado() {
        System.out.println("=== Pedidos por Estado ===");
        System.out.println("1. PENDIENTE  2. CONFIRMADO  3. TERMINADO  4. CANCELADO");
        System.out.print("Seleccione estado: ");
        int op = scanner.nextInt();
        scanner.nextLine();
        Estado estado = switch (op) {
            case 1 -> Estado.PENDIENTE;
            case 2 -> Estado.CONFIRMADO;
            case 3 -> Estado.TERMINADO;
            default -> Estado.CANCELADO;
        };
        List<Pedido> pedidos = pedidoRepo.buscarPorEstado(estado);
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos con estado " + estado + ".");
            return;
        }
        pedidos.forEach(p -> System.out.printf(
                "ID: %-5d | Fecha: %s | Estado: %-12s | Total: $%.2f%n",
                p.getId(), p.getFecha(), p.getEstado(), p.getTotal()
        ));
    }

//=================MENÚ REPORTES Y SUS OPERACIONES ==================//

    private static void menuReportes() {
        int opcion =-1;
        do {
            System.out.println("\n=== MENU DE REPORTES ===");
            System.out.println("1. Productos por categoria");
            System.out.println("2. Pedidos por usuario");
            System.out.println("3. Pedidos por estado");
            System.out.println("4. Total facturado");
            System.out.println("0. Volver");
            System.out.print("Opcion: ");
            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
            } catch (NoSuchElementException e) {
                System.out.println("Error: entrada no valida.");
                scanner.nextLine();
                continue;
            }
            switch (opcion) {
                case 1 -> listarProductosPorCategoria();
                case 2 -> pedidosPorUsuario();
                case 3 -> pedidosPorEstado();
                case 4 -> totalFacturado();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opcion invalida");
            }
        } while (opcion != 0);
    }

    private static void listarProductosPorCategoria() {
        System.out.println("=== Productos por Categoria ===");
        Categoria cat = seleccionarCategoria();
        if (cat == null) { System.out.println("La categoria seleccionada no existe."); return; }
        List<Producto> productos = categoriaRepo.buscarProductosPorCategoria(cat.getId());
        if (productos.isEmpty()) {
            System.out.println("No hay productos activos en la categoria: " + cat.getCategoria());
            return;
        }
        System.out.println("=== Productos de: " + cat.getCategoria() + " ===");
        productos.forEach(p -> System.out.printf(
                "ID: %-5d | Nombre: %-20s | Precio: %10.2f | Stock: %d%n",
                p.getId(), p.getNombre(), p.getPrecio(), p.getStock()
        ));
    }

    private static void totalFacturado() {
        double total = pedidoRepo.buscarPorEstado(Estado.TERMINADO).stream()
                .mapToDouble(p -> p.getTotal() == 0 ? 0 : p.getTotal())
                .sum();
        System.out.println("Total facturado: " + String.format(Locale.US, "$%.2f", total));
    }

    //Fin del main
}