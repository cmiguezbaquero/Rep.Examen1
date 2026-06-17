package modelo;

import excepciones.DuracionInvalidaException;
import excepciones.LimitePrestamosException;
import excepciones.RecursoNoDisponibleException;
import interfaces.Prestable;

/**
 * ══════════════════════════════════════════════════════
 *  CLASE HIJA — Pelicula  (segunda subclase de Recurso)
 * ══════════════════════════════════════════════════════
 *
 *  POLIMORFISMO EN ACCIÓN:
 *  Tanto Libro como Pelicula extienden Recurso.
 *  Las dos tienen prestar(), pero cada una lo hace distinto.
 *
 *  Esto permite hacer:
 *      ArrayList<Recurso> recursos = ...
 *      for (Recurso r : recursos) {
 *          r.prestar();  ← llama al método de CADA clase real
 *      }
 *
 *  INSTANCEOF + CAST — PATRÓN MUY PEDIDO:
 *      if (r instanceof Pelicula) {
 *          Pelicula p = (Pelicula) r;
 *          // ahora puedes usar métodos de Pelicula
 *      }
 * ══════════════════════════════════════════════════════
 */
public class Pelicula extends Recurso implements Prestable {

    // ── Atributos propios de Pelicula ────────────────────────
    private String director;
    private int duracionMinutos;    // usado como getTamanyo()
    private String genero;
    private boolean enStreaming;    // ¿está disponible online?
    private int clasificacionEdad;  // +7, +12, +16, +18
    private boolean prestada;

    // ── Constructor ──────────────────────────────────────────
    public Pelicula(String titulo, int anyioPublicacion, String director,
                    int duracionMinutos, String genero)
            throws DuracionInvalidaException {

        super(titulo, anyioPublicacion);    // ← SIEMPRE PRIMERO

        if (duracionMinutos <= 0) {
            throw new DuracionInvalidaException("Duración inválida: " + duracionMinutos);
        }

        this.director          = director;
        this.duracionMinutos   = duracionMinutos;
        this.genero            = genero;
        this.enStreaming       = false;
        this.clasificacionEdad = 0;
        this.prestada          = false;
    }

    // ── @Override del método abstracto ──────────────────────
    /**
     *  La misma firma que en Libro.prestar(),
     *  pero lógica diferente: comprueba si está en streaming.
     *
     *  ESTO ES POLIMORFISMO:
     *  misma interfaz, comportamiento diferente.
     */
    @Override
    public void prestar() throws RecursoNoDisponibleException {

        if (!disponible || prestada) {
            throw new RecursoNoDisponibleException(
                    "Película no disponible: " + titulo);
        }

        System.out.println("🎬 Prestando película: " + titulo);
        prestada   = true;
        disponible = false;
        vecesPrestado++;
    }

    // ── Implementación de Prestable ─────────────────────────
    @Override
    public boolean prestar() throws RecursoNoDisponibleException, LimitePrestamosException {

        if (!disponible || prestada) {
            throw new RecursoNoDisponibleException("Película no disponible: " + titulo);
        }

        prestada   = true;
        disponible = false;
        vecesPrestado++;

        System.out.println("✅ Película prestada: " + titulo);
        return true;
    }

    @Override
    public boolean devolver() {

        if (!prestada) return false;

        prestada   = false;
        disponible = true;

        System.out.println("↩ Película devuelta: " + titulo);
        return true;
    }

    @Override
    public int getTamanyo() {
        return duracionMinutos;     // para película, el tamaño es la duración
    }

    // ── Métodos propios ──────────────────────────────────────
    public void activarStreaming()    { this.enStreaming = true; }
    public void desactivarStreaming() { this.enStreaming = false; }
    public boolean esAptaParaMenores() { return clasificacionEdad <= 12; }

    // ── Getters/Setters ──────────────────────────────────────
    public String getDirector()                    { return director; }
    public void setDirector(String director)       { this.director = director; }
    public int getDuracionMinutos()                { return duracionMinutos; }
    public String getGenero()                      { return genero; }
    public void setGenero(String genero)           { this.genero = genero; }
    public boolean isEnStreaming()                 { return enStreaming; }
    public int getClasificacionEdad()              { return clasificacionEdad; }
    public void setClasificacionEdad(int edad)     { this.clasificacionEdad = edad; }
    public boolean isPrestada()                    { return prestada; }

    @Override
    public String toString() {
        return super.toString()
                + " | Director: " + director
                + " | Duración: " + duracionMinutos + " min";
    }
}
