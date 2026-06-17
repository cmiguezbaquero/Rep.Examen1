package modelo;

import excepciones.ColeccionLlenaException;
import excepciones.ColeccionVaciaException;
import excepciones.RecursoDuplicadoException;
import excepciones.RecursoNoEncontradoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * ══════════════════════════════════════════════════════
 *  COLECCIÓN — Agrupa recursos (como Playlist en música)
 * ══════════════════════════════════════════════════════
 *
 *  COMPOSICIÓN vs AGREGACIÓN — La diferencia que te preguntarán:
 *
 *  COMPOSICIÓN (fuerte):
 *    El objeto "hijo" no puede existir sin el padre.
 *    Si eliminas la Coleccion, sus Recursos también se borran.
 *    Ejemplo: Album y Cancion en el proyecto de música.
 *
 *  AGREGACIÓN (débil):
 *    El objeto "hijo" existe independientemente del padre.
 *    Si eliminas la Coleccion, los Recursos siguen existiendo.
 *    ← ESTE ES NUESTRO CASO: los Libros existen aunque borres la colección
 *
 *  ARRAYLIST — TODOS LOS PATRONES aquí:
 *  ✔ añadir con control de duplicados
 *  ✔ eliminar por posición
 *  ✔ eliminar por referencia
 *  ✔ buscar por campo
 *  ✔ filtrar por tipo (instanceof)
 *  ✔ ordenar
 *  ✔ sumar total
 * ══════════════════════════════════════════════════════
 */
public class Coleccion {

    private String id;
    private String nombre;
    private ArrayList<Recurso> recursos;        // ← AGREGACIÓN
    private boolean esPublica;
    private int seguidores;
    private int maxRecursos;

    private static final int MAX_DEFAULT = 100;

    // ── Constructor ──────────────────────────────────────────
    public Coleccion(String nombre) {
        this.id          = UUID.randomUUID().toString().substring(0, 8);
        this.nombre      = nombre;
        this.recursos    = new ArrayList<>();   // ⚠ inicializar siempre
        this.esPublica   = false;
        this.seguidores  = 0;
        this.maxRecursos = MAX_DEFAULT;
    }

    public Coleccion(String nombre, boolean esPublica) {
        this(nombre);                           // llama al constructor anterior
        this.esPublica = esPublica;
    }

    // ══════════════════════════════════════════════════════════
    //  PATRÓN 1: AÑADIR CON CONTROL DE LÍMITE Y DUPLICADOS
    // ══════════════════════════════════════════════════════════
    /**
     *  ORDEN OBLIGATORIO:
     *  1. Comprobar llena
     *  2. Comprobar duplicado
     *  3. Añadir
     */
    public void agregarRecurso(Recurso recurso)
            throws ColeccionLlenaException, RecursoDuplicadoException {

        // 1. Comprobar límite
        if (recursos.size() >= maxRecursos) {
            throw new ColeccionLlenaException(
                    "La colección '" + nombre + "' está llena (" + maxRecursos + " recursos).");
        }

        // 2. Comprobar duplicado
        //    contains() usa el equals() del objeto
        //    → por eso definimos equals() por ID en Recurso
        if (recursos.contains(recurso)) {
            throw new RecursoDuplicadoException(
                    "El recurso '" + recurso.getTitulo() + "' ya está en la colección.");
        }

        // 3. Añadir
        recursos.add(recurso);
        System.out.println("✅ Añadido a '" + nombre + "': " + recurso.getTitulo());
    }

    // ══════════════════════════════════════════════════════════
    //  PATRÓN 2: ELIMINAR POR POSICIÓN (1-based, como en el examen)
    // ══════════════════════════════════════════════════════════
    public Recurso eliminarRecurso(int posicion) throws RecursoNoEncontradoException {

        // Convertir posición 1-based a índice 0-based
        int indice = posicion - 1;

        // Validar rango
        if (indice < 0 || indice >= recursos.size()) {
            throw new RecursoNoEncontradoException(
                    "Posición inválida: " + posicion + ". Total: " + recursos.size());
        }

        Recurso eliminado = recursos.remove(indice);
        System.out.println("🗑 Eliminado de '" + nombre + "': " + eliminado.getTitulo());
        return eliminado;
    }

    // ── Eliminar por referencia ──────────────────────────────
    public boolean eliminarRecurso(Recurso recurso) {
        boolean eliminado = recursos.remove(recurso);   // usa equals()
        if (eliminado) {
            System.out.println("🗑 Eliminado: " + recurso.getTitulo());
        }
        return eliminado;
    }

    // ═══════════════════════════════════════════════════════════
    //  PATRÓN 3: BUSCAR EN ARRAYLIST — for + if + return/throw
    // ═══════════════════════════════════════════════════════════
    public Recurso buscarPorTitulo(String titulo) throws RecursoNoEncontradoException {

        for (Recurso r : recursos) {
            // ⚠ NUNCA usar == para Strings → usar .equals() o contains()
            if (r.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                return r;
            }
        }

        // Si llegamos aquí, no lo encontramos
        throw new RecursoNoEncontradoException(
                "No se encontró ningún recurso con: '" + titulo + "'");
    }

    // ═══════════════════════════════════════════════════════════
    //  PATRÓN 4: FILTRAR POR TIPO — instanceof + casting
    // ═══════════════════════════════════════════════════════════
    /**
     *  TRUCO:
     *  La lista tiene Recurso (tipo padre).
     *  Queremos solo los Libro (tipo hijo).
     *  → instanceof nos dice si es de ese tipo
     *  → cast nos permite usarlo como Libro
     */
    public ArrayList<Libro> obtenerLibros() {

        ArrayList<Libro> libros = new ArrayList<>();

        for (Recurso r : recursos) {
            if (r instanceof Libro) {               // ← es un Libro?
                Libro libro = (Libro) r;            // ← cast para usarlo como Libro
                libros.add(libro);
            }
        }

        return libros;
    }

    public ArrayList<Pelicula> obtenerPeliculas() {

        ArrayList<Pelicula> peliculas = new ArrayList<>();

        for (Recurso r : recursos) {
            if (r instanceof Pelicula) {
                peliculas.add((Pelicula) r);        // cast directo en una línea
            }
        }

        return peliculas;
    }

    // ═══════════════════════════════════════════════════════════
    //  PATRÓN 5: SUMAR TOTAL — acumulador
    // ═══════════════════════════════════════════════════════════
    public int getDuracionTotal() {

        int total = 0;                          // ← acumulador, siempre empieza en 0

        for (Recurso r : recursos) {
            if (r instanceof Prestable) {
                total += ((Prestable) r).getTamanyo();
            }
        }

        return total;
    }

    // ═══════════════════════════════════════════════════════════
    //  PATRÓN 6: ORDENAR — Collections.sort + Comparator
    // ═══════════════════════════════════════════════════════════
    /**
     *  PATRÓN DE ORDENACIÓN:
     *  Collections.sort(lista, comparador)
     *
     *  El comparador: (a, b) -> campo.compareTo(campo)
     *    positivo  → a va después de b
     *    negativo  → a va antes que b
     *    cero      → igual
     *
     *  Para números: b - a (descendente), a - b (ascendente)
     *  Para Strings: a.compareTo(b)
     */
    public void ordenarPorTitulo() throws ColeccionVaciaException {

        if (recursos.isEmpty()) {
            throw new ColeccionVaciaException("La colección está vacía.");
        }

        Collections.sort(recursos,
                (a, b) -> a.getTitulo().compareTo(b.getTitulo())   // alfabético A→Z
        );
    }

    public void ordenarPorPopularidad() throws ColeccionVaciaException {

        if (recursos.isEmpty()) {
            throw new ColeccionVaciaException("La colección está vacía.");
        }

        Collections.sort(recursos,
                (a, b) -> b.getVecesPrestado() - a.getVecesPrestado()  // más popular primero
        );
    }

    public void mezclarAleatorio() throws ColeccionVaciaException {

        if (recursos.isEmpty()) {
            throw new ColeccionVaciaException("La colección está vacía.");
        }

        Collections.shuffle(recursos);
    }

    // ── Getters básicos ──────────────────────────────────────
    public String getId()             { return id; }
    public String getNombre()         { return nombre; }
    public void setNombre(String n)   { this.nombre = n; }
    public boolean isEsPublica()      { return esPublica; }
    public void setEsPublica(boolean p) { this.esPublica = p; }
    public int getSeguidores()        { return seguidores; }
    public int getNumRecursos()       { return recursos.size(); }
    public boolean estaVacia()        { return recursos.isEmpty(); }

    // ⚠ Copia defensiva: devolvemos copia, no la lista original
    public ArrayList<Recurso> getRecursos() {
        return new ArrayList<>(recursos);
    }

    // Acceso por índice 0-based (interno)
    public Recurso getRecurso(int indice) {
        if (indice < 0 || indice >= recursos.size()) return null;
        return recursos.get(indice);
    }

    public void incrementarSeguidores() { seguidores++; }
    public void decrementarSeguidores() { if (seguidores > 0) seguidores--; }

    @Override
    public String toString() {
        return "Colección '" + nombre + "' [" + recursos.size() + "/" + maxRecursos + "]"
                + " | Pública: " + esPublica
                + " | Seguidores: " + seguidores;
    }
}