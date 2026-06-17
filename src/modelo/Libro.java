package modelo;

import excepciones.DuracionInvalidaException;
import excepciones.LimitePrestamosException;
import excepciones.RecursoNoDisponibleException;
import interfaces.Prestable;

/**
 * ══════════════════════════════════════════════════════
 *  CLASE HIJA — Libro  extends Recurso  implements Prestable
 * ══════════════════════════════════════════════════════
 *
 *  PATRÓN COMPLETO DE CLASE HIJA:
 *
 *  public class Libro extends Recurso implements Prestable {
 *
 *      // 1. atributos PROPIOS (no los del padre)
 *      // 2. constructor → super(...) PRIMERO
 *      // 3. @Override del método abstracto del padre
 *      // 4. implementación de la interfaz
 *      // 5. métodos propios
 *      // 6. getters/setters propios
 *      // 7. toString
 *  }
 *
 *  ¡NUNCA repitas en Libro los atributos que ya tiene Recurso!
 *  (titulo, disponible, etc. ya están en el padre)
 * ══════════════════════════════════════════════════════
 */
public class Libro extends Recurso implements Prestable {

    // ── 1. Atributos PROPIOS de Libro ────────────────────────
    private String autor;
    private String isbn;
    private int numeroPaginas;      // usado como getTamanyo()
    private int copiasDisponibles;
    private int copiasTotales;
    private String genero;          // en proyecto real sería enum GeneroLiterario
    private boolean prestado;       // estado de este ejemplar

    // ── 2. Constructor ───────────────────────────────────────
    /**
     *  REGLA CLAVE:
     *  Primera línea SIEMPRE → super(...)
     *  Luego los atributos propios.
     *
     *  Si el constructor padre lanza excepción,
     *  este también debe declararla con throws.
     */
    public Libro(String titulo, int anyioPublicacion, String autor,
                 int numeroPaginas, int copias, String genero)
            throws DuracionInvalidaException {

        // ✔ Llamada al constructor padre — OBLIGATORIO
        super(titulo, anyioPublicacion);

        // ✔ Validación propia
        if (numeroPaginas <= 0) {
            throw new DuracionInvalidaException("Número de páginas inválido.");
        }

        // ✔ Atributos propios
        this.autor             = autor;
        this.isbn              = generarISBN();     // método propio
        this.numeroPaginas     = numeroPaginas;
        this.copiasTotales     = copias;
        this.copiasDisponibles = copias;
        this.genero            = genero;
        this.prestado          = false;
    }

    // Constructor simplificado (sin copias ni género)
    public Libro(String titulo, int anyioPublicacion, String autor, int numeroPaginas)
            throws DuracionInvalidaException {
        this(titulo, anyioPublicacion, autor, numeroPaginas, 1, "General");
    }

    // ── Método privado auxiliar ──────────────────────────────
    private String generarISBN() {
        return "ISBN-" + (int)(Math.random() * 9000000 + 1000000);
    }

    // ── 3. @Override del método ABSTRACTO del padre ──────────
    /**
     *  PATRÓN reproducir / prestar:
     *
     *  1. Comprobar error → throw si hay problema
     *  2. Hacer la acción
     *  3. Actualizar estado
     */
    @Override
    public void prestar() throws RecursoNoDisponibleException {

        // 1. Comprobar error
        if (!disponible || copiasDisponibles <= 0) {
            throw new RecursoNoDisponibleException(
                    "No hay copias disponibles de: " + titulo);
        }

        // 2. Acción
        System.out.println("📖 Prestando libro: " + titulo);

        // 3. Actualizar estado
        copiasDisponibles--;
        vecesPrestado++;            // atributo del padre (protected)
        prestado = true;

        if (copiasDisponibles == 0) {
            disponible = false;     // atributo del padre (protected)
        }
    }

    // ── 4. Implementación de la interfaz Prestable ───────────
    /**
     *  IMPORTANTE:
     *  La interfaz Prestable define prestar(), devolver(), getTamanyo().
     *  prestar() ya lo implementamos arriba (también es abstracto del padre).
     *  Ahora implementamos los otros dos.
     */
    @Override
    public boolean prestar() throws RecursoNoDisponibleException, LimitePrestamosException {
        // ⚠ NOTA: en un proyecto real, este método recibiría el usuario
        // para comprobar su límite. Aquí lo simplificamos.

        if (!disponible || copiasDisponibles <= 0) {
            throw new RecursoNoDisponibleException("Sin copias: " + titulo);
        }

        copiasDisponibles--;
        vecesPrestado++;
        prestado = true;
        if (copiasDisponibles == 0) disponible = false;

        System.out.println("✅ Libro prestado: " + titulo
                + " | Copias restantes: " + copiasDisponibles);
        return true;
    }

    @Override
    public boolean devolver() {

        // Solo devolver si estaba prestado
        if (!prestado) {
            System.out.println("⚠ El libro no estaba prestado.");
            return false;
        }

        copiasDisponibles++;
        prestado  = false;
        disponible = true;      // al devolver, vuelve a estar disponible

        System.out.println("↩ Libro devuelto: " + titulo
                + " | Copias: " + copiasDisponibles);
        return true;
    }

    @Override
    public int getTamanyo() {
        return numeroPaginas;   // para un libro, el tamaño son las páginas
    }

    // ── 5. Métodos propios ───────────────────────────────────

    public boolean esLargo() {
        return numeroPaginas > 400;
    }

    public String obtenerResumen() {
        return titulo + " de " + autor + " (" + anyioPublicacion + ") — "
                + numeroPaginas + " páginas";
    }

    // ── 6. Getters/Setters ───────────────────────────────────
    public String getAutor()              { return autor; }
    public void setAutor(String autor)    { this.autor = autor; }
    public String getIsbn()               { return isbn; }
    public int getNumeroPaginas()         { return numeroPaginas; }
    public int getCopiasDisponibles()     { return copiasDisponibles; }
    public int getCopiasTotales()         { return copiasTotales; }
    public String getGenero()             { return genero; }
    public void setGenero(String genero)  { this.genero = genero; }
    public boolean isPrestado()           { return prestado; }

    // ── 7. toString ─────────────────────────────────────────
    @Override
    public String toString() {
        // Llamamos al toString del padre y añadimos info propia
        return super.toString()
                + " | Autor: " + autor
                + " | Páginas: " + numeroPaginas
                + " | Copias: " + copiasDisponibles + "/" + copiasTotales;
    }
}
