import modelo.*;
import excepciones.*;

/**
 * ══════════════════════════════════════════════════════
 *  MAIN — Demostración de todos los patrones
 * ══════════════════════════════════════════════════════
 *
 *  CÓMO LEER ESTE FICHERO EN EL EXAMEN:
 *  Cada escenario muestra un patrón distinto.
 *  Busca el escenario que corresponde a lo que te piden.
 * ══════════════════════════════════════════════════════
 */
public class Main {

    private static Biblioteca biblioteca;

    public static void main(String[] args) {

        System.out.println("\n══════════════════════════════════");
        System.out.println("  BIBLIOTECH — Demo de patrones   ");
        System.out.println("══════════════════════════════════\n");

        escenario1_Singleton_y_Registro();
        escenario2_Herencia_y_Polimorfismo();
        escenario3_Composicion();
        escenario4_ArrayListPatrones();
        escenario5_HashMapPatrones();
        escenario6_Excepciones();
        escenario7_InterfacesYValoracion();
        escenario8_Instanceof_y_Cast();
        escenario9_OrdenarYFiltrar();
        escenario10_Estadisticas();
    }

    // ════════════════════════════════════════════════════════
    //  ESCENARIO 1 — Singleton
    // ════════════════════════════════════════════════════════
    static void escenario1_Singleton_y_Registro() {
        System.out.println("[1] SINGLETON Y REGISTRO");
        System.out.println("------------------------");

        // SINGLETON: siempre a través de getInstancia()
        biblioteca = Biblioteca.getInstancia("BiblioTech Central");

        // Verificar que es la MISMA instancia
        Biblioteca otra = Biblioteca.getInstancia("Otra nombre");   // ignorará el nombre
        System.out.println("¿Misma instancia? " + (biblioteca == otra));  // true

        // Registrar usuarios con validaciones
        try {
            // ✗ Email inválido
            biblioteca.registrarUsuarioBasico("Test", "emailsinArroba", "password123");
        } catch (EmailInvalidoException e) {
            System.out.println("✗ Error esperado (email): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }

        try {
            // ✗ Password débil
            biblioteca.registrarUsuarioBasico("Test", "test@test.com", "123");
        } catch (PasswordDebilException e) {
            System.out.println("✗ Error esperado (pass): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }

        try {
            // ✔ Registro correcto
            biblioteca.registrarUsuarioBasico("Laura García", "laura@gmail.com", "password123");
            biblioteca.registrarUsuarioPremium("Carlos Ruiz", "carlos@gmail.com", "securepass456");
            biblioteca.registrarUsuarioBasico("Ana Martínez", "ana@gmail.com", "anapass789");
            System.out.println("✔ 3 usuarios registrados");

            // ✗ Email duplicado
            biblioteca.registrarUsuarioBasico("Duplicado", "laura@gmail.com", "otrapass123");
        } catch (UsuarioYaExisteException e) {
            System.out.println("✗ Error esperado (duplicado): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }

        System.out.println("Total usuarios: " + biblioteca.getTotalUsuarios());
        System.out.println();
    }

    // ════════════════════════════════════════════════════════
    //  ESCENARIO 2 — Herencia y Polimorfismo
    // ════════════════════════════════════════════════════════
    static void escenario2_Herencia_y_Polimorfismo() {
        System.out.println("[2] HERENCIA Y POLIMORFISMO");
        System.out.println("---------------------------");

        try {
            // Crear recursos de tipos diferentes
            Libro libro1     = new Libro("Cien años de soledad", 1967, "García Márquez", 496);
            Libro libro2     = new Libro("El Quijote", 1605, "Cervantes", 1048);
            Pelicula peli1   = new Pelicula("El laberinto del fauno", 2006, "Del Toro", 118, "Fantasia");
            Pelicula peli2   = new Pelicula("Volver", 2006, "Almodóvar", 121, "Drama");

            biblioteca.agregarRecurso(libro1);
            biblioteca.agregarRecurso(libro2);
            biblioteca.agregarRecurso(peli1);
            biblioteca.agregarRecurso(peli2);

            System.out.println("✔ 4 recursos añadidos al catálogo");

            // POLIMORFISMO: tratamos todos como Recurso
            // Cada uno llama a SU implementación de prestar()
            java.util.ArrayList<Recurso> todos = biblioteca.getTodosLosLibros()
                    .stream().collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

            // Forma sin streams (más típica en examen):
            // El toString() de cada uno es diferente → polimorfismo
            System.out.println("Libro: " + libro1.toString());
            System.out.println("Pelicula: " + peli1.toString());

        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }

        System.out.println();
    }

    // ════════════════════════════════════════════════════════
    //  ESCENARIO 3 — Composición
    // ════════════════════════════════════════════════════════
    static void escenario3_Composicion() {
        System.out.println("[3] COMPOSICIÓN (Autor crea sus Libros)");
        System.out.println("----------------------------------------");

        try {
            // El Autor CREA sus propios libros (composición)
            Autor autor = biblioteca.registrarAutor("Isabel Allende", "Chile");

            Libro libro1 = autor.crearLibro("La casa de los espíritus", 1982, 448, "Realismo mágico");
            Libro libro2 = autor.crearLibro("Eva Luna", 1987, 368, "Novela");
            Libro libro3 = autor.crearLibro("Paula", 1994, 352, "Autobiografía");

            // Añadir al catálogo de la biblioteca también
            biblioteca.agregarRecurso(libro1);
            biblioteca.agregarRecurso(libro2);
            biblioteca.agregarRecurso(libro3);

            System.out.println("✔ Autor con " + autor.getNumObras() + " obras creadas");

            // Intentar pasar del límite
            // (aquí MAX_OBRAS = 50, así que no fallaría con 3, pero el patrón está)

            // Eliminar obra por posición
            autor.eliminarObra(2);  // elimina "Eva Luna"
            System.out.println("✔ Obra eliminada. Quedan: " + autor.getNumObras());

            // Eliminar posición inválida
            autor.eliminarObra(99);

        } catch (AutorNoEncontradoException e) {
            System.out.println("✗ Error esperado (posición): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }

        System.out.println();
    }

    // ════════════════════════════════════════════════════════
    //  ESCENARIO 4 — ArrayList (todos los patrones)
    // ════════════════════════════════════════════════════════
    static void escenario4_ArrayListPatrones() {
        System.out.println("[4] ARRAYLIST — TODOS LOS PATRONES");
        System.out.println("------------------------------------");

        try {
            Usuario laura = biblioteca.buscarUsuarioPorEmail("laura@gmail.com");

            // Crear colección
            Coleccion coleccion = laura.crearColeccion("Mis lecturas favoritas");

            // Añadir recursos
            java.util.ArrayList<Recurso> libros = biblioteca.getTodosLosLibros();
            for (Recurso r : libros) {
                coleccion.agregarRecurso(r);
            }
            System.out.println("✔ Recursos añadidos: " + coleccion.getNumRecursos());

            // DUPLICADO
            if (!libros.isEmpty()) {
                coleccion.agregarRecurso(libros.get(0));    // ← lanzará excepción
            }

        } catch (RecursoDuplicadoException e) {
            System.out.println("✗ Error esperado (duplicado): " + e.getMessage());
        } catch (ColeccionLlenaException e) {
            System.out.println("✗ Colección llena: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }

        System.out.println();
    }

    // ════════════════════════════════════════════════════════
    //  ESCENARIO 5 — HashMap
    // ════════════════════════════════════════════════════════
    static void escenario5_HashMapPatrones() {
        System.out.println("[5] HASHMAP — BUSCAR, INSERTAR");
        System.out.println("-------------------------------");

        // Buscar usuario existente
        try {
            Usuario u = biblioteca.buscarUsuarioPorEmail("carlos@gmail.com");
            System.out.println("✔ Usuario encontrado: " + u.getNombre());
        } catch (RecursoNoEncontradoException e) {
            System.out.println("✗ " + e.getMessage());
        }

        // Buscar usuario inexistente
        try {
            biblioteca.buscarUsuarioPorEmail("noexiste@mail.com");
        } catch (RecursoNoEncontradoException e) {
            System.out.println("✗ Error esperado: " + e.getMessage());
        }

        // Buscar autor
        try {
            Autor autor = biblioteca.buscarAutor("Isabel Allende");
            System.out.println("✔ Autor encontrado: " + autor.getNombre());
        } catch (AutorNoEncontradoException e) {
            System.out.println("✗ " + e.getMessage());
        }

        System.out.println();
    }

    // ════════════════════════════════════════════════════════
    //  ESCENARIO 6 — Excepciones
    // ════════════════════════════════════════════════════════
    static void escenario6_Excepciones() {
        System.out.println("[6] EXCEPCIONES");
        System.out.println("---------------");

        try {
            // Buscar título existente
            java.util.ArrayList<Recurso> resultados =
                    biblioteca.buscarPorTitulo("espíritus");
            System.out.println("✔ Búsqueda encontró: " + resultados.size() + " resultado(s)");
        } catch (RecursoNoEncontradoException e) {
            System.out.println("✗ " + e.getMessage());
        }

        // Buscar título inexistente
        try {
            biblioteca.buscarPorTitulo("xyznoexiste999");
        } catch (RecursoNoEncontradoException e) {
            System.out.println("✗ Error esperado: " + e.getMessage());
        }

        // Recurso no disponible
        try {
            java.util.ArrayList<Libro> libros = biblioteca.getTodosLosLibros();
            if (!libros.isEmpty()) {
                Libro libro = libros.get(0);
                libro.marcarNoDisponible();
                libro.prestar();    // ← lanzará excepción
            }
        } catch (RecursoNoDisponibleException e) {
            System.out.println("✗ Error esperado (no disponible): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }

        System.out.println();
    }

    // ════════════════════════════════════════════════════════
    //  ESCENARIO 7 — Interfaces y Valoración
    // ════════════════════════════════════════════════════════
    static void escenario7_InterfacesYValoracion() {
        System.out.println("[7] INTERFACES Y VALORACIÓN");
        System.out.println("----------------------------");

        try {
            java.util.ArrayList<Libro> libros = biblioteca.getTodosLosLibros();
            if (!libros.isEmpty()) {
                Libro libro = libros.get(0);
                libro.marcarDisponible();   // restaurar disponibilidad

                // Valorar (Valorable.puntuar)
                libro.puntuar(5);
                libro.puntuar(4);
                libro.puntuar(5);
                System.out.println("✔ Media de '" + libro.getTitulo() + "': "
                        + libro.getMediaValoraciones());

                // Valoración inválida
                libro.puntuar(6);   // ← lanzará excepción
            }
        } catch (ValoracionInvalidaException e) {
            System.out.println("✗ Error esperado (valoración): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }

        System.out.println();
    }

    // ════════════════════════════════════════════════════════
    //  ESCENARIO 8 — instanceof y cast
    // ════════════════════════════════════════════════════════
    static void escenario8_Instanceof_y_Cast() {
        System.out.println("[8] INSTANCEOF Y CAST");
        System.out.println("---------------------");

        // Tenemos una lista de Recurso (padre)
        // Queremos actuar diferente según el tipo real
        java.util.ArrayList<Recurso> todos = new java.util.ArrayList<>();
        todos.addAll(biblioteca.getTodosLosLibros());
        todos.addAll(biblioteca.getTodasLasPeliculas());

        int libros    = 0;
        int peliculas = 0;

        for (Recurso r : todos) {

            if (r instanceof Libro) {
                Libro libro = (Libro) r;        // cast
                libros++;
                System.out.println("  📖 Libro: " + libro.getTitulo()
                        + " (" + libro.getNumeroPaginas() + " pág)");

            } else if (r instanceof Pelicula) {
                Pelicula peli = (Pelicula) r;   // cast
                peliculas++;
                System.out.println("  🎬 Película: " + peli.getTitulo()
                        + " (" + peli.getDuracionMinutos() + " min)");
            }
        }

        System.out.println("✔ Contados: " + libros + " libros, " + peliculas + " películas");
        System.out.println();
    }

    // ════════════════════════════════════════════════════════
    //  ESCENARIO 9 — Ordenar y filtrar
    // ════════════════════════════════════════════════════════
    static void escenario9_OrdenarYFiltrar() {
        System.out.println("[9] ORDENAR Y FILTRAR");
        System.out.println("---------------------");

        try {
            Usuario laura = biblioteca.buscarUsuarioPorEmail("laura@gmail.com");
            java.util.ArrayList<Coleccion> cols = laura.getMisColecciones();

            if (!cols.isEmpty()) {
                Coleccion col = cols.get(0);

                // Ordenar alfabético
                col.ordenarPorTitulo();
                System.out.println("✔ Ordenado por título");
                if (col.getNumRecursos() > 0) {
                    System.out.println("   Primero: " + col.getRecurso(0).getTitulo());
                }

                // Ordenar por popularidad
                col.ordenarPorPopularidad();
                System.out.println("✔ Ordenado por popularidad");

                // Mezclar
                col.mezclarAleatorio();
                System.out.println("✔ Mezclado aleatoriamente");

                // Filtrar libros dentro de la colección
                java.util.ArrayList<Libro> soloLibros = col.obtenerLibros();
                System.out.println("✔ Solo libros en colección: " + soloLibros.size());
            }

        } catch (Exception e) {
            System.out.println("✗ " + e.getMessage());
        }

        System.out.println();
    }

    // ════════════════════════════════════════════════════════
    //  ESCENARIO 10 — Estadísticas finales
    // ════════════════════════════════════════════════════════
    static void escenario10_Estadisticas() {
        System.out.println("[10] ESTADÍSTICAS FINALES");
        System.out.println("-------------------------");

        System.out.println(biblioteca.obtenerEstadisticasGenerales());

        // Top recursos
        java.util.ArrayList<Recurso> top = biblioteca.obtenerTopRecursos(3);
        System.out.println("\n🏆 Top 3 recursos:");
        for (int i = 0; i < top.size(); i++) {
            System.out.println("  " + (i+1) + ". " + top.get(i).getTitulo()
                    + " (" + top.get(i).getVecesPrestado() + " préstamos)");
        }

        // Premium vs básicos
        System.out.println("\n⭐ Usuarios premium: " + biblioteca.getUsuariosPremium().size());
        System.out.println("👥 Total usuarios: " + biblioteca.getTotalUsuarios());
    }
}
