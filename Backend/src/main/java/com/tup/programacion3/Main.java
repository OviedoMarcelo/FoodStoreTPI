package com.tup.programacion3;

import com.tup.programacion3.entities.Categoria;
import com.tup.programacion3.entities.Producto;
import com.tup.programacion3.repository.CategoriaRepository;
import com.tup.programacion3.repository.ProductoRepository;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CategoriaRepository categoriaRepo = new CategoriaRepository();
    private static final ProductoRepository productoRepo = new ProductoRepository();

    public static void main(String[] args) throws SQLException, UnsupportedEncodingException {
        //Encoding para poder ver palabras con tildes y otros ascii
        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        //Server H2 console
        org.h2.tools.Server webServer = org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
        webServer.start();
        System.out.println("H2 Console: http://localhost:8082");

        int opcion;
        do {
            System.out.println("\n=== BIENVENIDO AL PROGRAMA ===");
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Categorias");
            System.out.println("2. Productos");
            System.out.println("3. Reportes");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar buffer

            switch (opcion) {
                case 1 -> menuCategorias();
                case 2 -> menuProductos();
                case 3 -> menuReportes();
                case 0 -> System.out.println("Saliendo...");
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


//=================MENÚ REPORTES Y SUS OPERACIONES ==================//

    private static void menuReportes() {
        int opcion = -1;
        do {
            System.out.println("\n=== MENU DE REPORTES ===");
            System.out.println("1. Listar productos por categoría");
            System.out.println("0. Volver...");
            System.out.print("Opcion: ");
            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
            } catch (NoSuchElementException e) {
                System.out.println("Error: entrada no válida.");
                scanner.nextLine(); // limpiar buffer
                continue;
            }
            switch (opcion) {
                case 1 -> listarProductosPorCategoria();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opcion invalida");
            }
        } while (opcion != 0);
    }

    private static void listarProductosPorCategoria() {
        System.out.println("=== Listar productos por categoria ===");
        Categoria categoriaSeleccionada = seleccionarCategoria();
        if (categoriaSeleccionada == null) {
            System.out.println("La categoria seleccionada no existe.");
            return;
        }
        List<Producto>  productos = productoRepo.buscarPorCategoria(categoriaSeleccionada.getId());
        if (productos.isEmpty()) {
            System.out.println("No hay productos activos para la categoría: " + categoriaSeleccionada.getCategoria());
            return;
        }
        System.out.println("=== Productos de categoria: " + categoriaSeleccionada.getCategoria() + " ===");
        productos.forEach(p -> System.out.printf(
                "ID: %-5d | Nombre: %-20s | Precio: %10.2f | Stock: %d%n",
                p.getId(), p.getNombre(), p.getPrecio(), p.getStock()
        ));

    }


    //Fin del main
}