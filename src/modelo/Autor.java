package modelo;

import excepciones.AutorNoEncontradoException;
import excepciones.ColeccionLlenaException;
import excepciones.DuracionInvalidaException;

import java.util.ArrayList;
import java.util.UUID;

/**
 * ══════════════════════════════════════════════════════
 *  CLASE Autor — con COMPOSICIÓN hacia Libro
 * ══════════════════════════════════════════════════════
 *
 *  COMPOSICIÓN:
 *  Un Autor ES RESPONSABLE de crear sus Libros.
 *  Si el Autor se elimina, sus libros también.
 *
 *  El autor no recibe libros externos, los crea él mismo:
 *      Libro libro = autor.crearLibro("Título", 2023, 300);
 *
 *  Esto es como Album.crearCancion() en el proyecto de música.
 *
 *  DIFERENCIA con AGREGACIÓN:
 *  En Coleccion.agregarRecurso(recurso) → el recurso ya existe
 *  Aquí autor.crearLibro(...) → el autor lo crea nuevo
 * ══════════════════════════════════════════════════════
 */
public class Autor {

    private String id;
    private String nombre;
    private String paisOrigen;
    private ArrayList<Libro> obras;         // COMPOSICIÓN: el autor gestiona sus libros
    private int anyioNacimiento;
    private String biografia;
    private boolean verificado;

    private static final int MAX_OBRAS = 50;

    // ── Constructor ──────────────────────────────────────────
    public Autor(String nombre, String paisOrigen) {
        this.id             = UUID.randomUUID().toString().substring(0, 8);
        this.nombre         = nombre;
        this.paisOrigen     = paisOrigen;
        this.obras          = new ArrayList<>();    // ⚠ inicializar
        this.verificado     = false;
    }

    public Autor(String nombre, String paisOrigen, int anyioNacimiento, String biografia) {
        this(nombre, paisOrigen);   // llama al anterior
        this.anyioNacimiento = anyioNacimiento;
        this.biografia       = biografia;
    }

    // ══════════════════════════════════════════════════════════
    //  COMPOSICIÓN: el Autor CREA sus propios Libros
    // ══════════════════════════════════════════════════════════
    /**
     *  PATRÓN DE COMPOSICIÓN:
     *
     *  1. Comprobar límite
     *  2. Crear el objeto hijo (new Libro)
     *  3. Añadirlo a la lista
     *  4. Devolverlo
     *
     *  En el examen:
     *  Album.crearCancion(titulo, duracion, genero) → mismo patrón
     */
    public Libro crearLibro(String titulo, int anyio, int paginas, String genero)
            throws ColeccionLlenaException, DuracionInvalidaException {

        // 1. Comprobar límite
        if (obras.size() >= MAX_OBRAS) {
            throw new ColeccionLlenaException(
                    "El autor " + nombre + " ya tiene " + MAX_OBRAS + " obras.");
        }

        // 2. Crear (COMPOSICIÓN: el autor es responsable de crear)
        Libro libro = new Libro(titulo, anyio, nombre, paginas, 1, genero);

        // 3. Añadir a la lista interna
        obras.add(libro);

        System.out.println("📝 Nueva obra de " + nombre + ": " + titulo);

        // 4. Devolver referencia
        return libro;
    }

    // ── Eliminar obra por posición ───────────────────────────
    public void eliminarObra(int posicion) throws AutorNoEncontradoException {

        int indice = posicion - 1;  // 1-based a 0-based

        if (indice < 0 || indice >= obras.size()) {
            throw new AutorNoEncontradoException(
                    "No existe obra en posición: " + posicion);
        }

        Libro eliminado = obras.remove(indice);
        System.out.println("🗑 Obra eliminada de " + nombre + ": " + eliminado.getTitulo());
    }

    // ── Top obras ────────────────────────────────────────────
    /**
     *  PATRÓN: obtener top N
     *
     *  1. Copiar lista (no modificar la original)
     *  2. Ordenar
     *  3. Sublist con los primeros N
     */
    public ArrayList<Libro> obtenerTopObras(int cantidad) {

        ArrayList<Libro> copia = new ArrayList<>(obras);

        // Ordenar por popularidad descendente
        copia.sort((a, b) -> b.getVecesPrestado() - a.getVecesPrestado());

        int limite = Math.min(cantidad, copia.size());
        return new ArrayList<>(copia.subList(0, limite));
    }

    // ── Estadísticas ─────────────────────────────────────────
    public int getTotalReproducciones() {

        int total = 0;
        for (Libro libro : obras) {
            total += libro.getVecesPrestado();
        }
        return total;
    }

    public double calcularPromedioValoracion() {

        if (obras.isEmpty()) return 0.0;

        double total = 0.0;
        for (Libro libro : obras) {
            total += libro.getMediaValoraciones();
        }
        return total / obras.size();
    }

    // ── Getters/Setters ──────────────────────────────────────
    public String getId()               { return id; }
    public String getNombre()           { return nombre; }
    public void setNombre(String n)     { this.nombre = n; }
    public String getPaisOrigen()       { return paisOrigen; }
    public int getNumObras()            { return obras.size(); }
    public boolean isVerificado()       { return verificado; }
    public void verificar()             { this.verificado = true; }
    public String getBiografia()        { return biografia; }
    public void setBiografia(String b)  { this.biografia = b; }

    // Copia defensiva
    public ArrayList<Libro> getObras()  { return new ArrayList<>(obras); }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " (" + paisOrigen + ")"
                + " | Obras: " + obras.size()
                + " | Verificado: " + verificado;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Autor)) return false;
        Autor otro = (Autor) obj;
        return this.id.equals(otro.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}