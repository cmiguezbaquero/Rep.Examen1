package usuario;

import excepciones.EmailInvalidoException;
import excepciones.LimitePrestamosException;
import excepciones.PasswordDebilException;
import excepciones.RecursoNoDisponibleException;
import modelo.Recurso;

/**
 * ══════════════════════════════════════════════════════
 *  SUBCLASES DE USUARIO — Básico y Premium
 *  (en un proyecto real, ficheros separados)
 * ══════════════════════════════════════════════════════
 *
 *  RECUERDA EL PATRÓN COMPLETO:
 *
 *  public class UsuarioBasico extends Usuario {
 *
 *      // 1. atributos PROPIOS
 *      // 2. constructor → super(...) PRIMERO
 *      // 3. @Override del método abstracto
 *      // 4. métodos propios
 *  }
 * ══════════════════════════════════════════════════════
 */

// ════════════════════════════════════════════════════════
//  USUARIO BÁSICO — con límite de préstamos
// ════════════════════════════════════════════════════════
class UsuarioBasico extends Usuario {

    // ── Atributos propios ────────────────────────────────────
    private int limitePrestamos;
    private int prestamosEsteMes;

    private static final int LIMITE_DEFAULT = 3;

    // ── Constructor ──────────────────────────────────────────
    public UsuarioBasico(String nombre, String email, String password)
            throws EmailInvalidoException, PasswordDebilException {

        super(nombre, email, password);     // ← SIEMPRE PRIMERO

        this.limitePrestamos  = LIMITE_DEFAULT;
        this.prestamosEsteMes = 0;
    }

    // ── @Override del método abstracto ──────────────────────
    /**
     *  PATRÓN: validar → ejecutar → actualizar
     *
     *  UsuarioBasico no puede tener más de 3 préstamos activos.
     */
    @Override
    public void pedirPrestado(Recurso recurso)
            throws RecursoNoDisponibleException, LimitePrestamosException {

        // 1. Comprobar límite del USUARIO
        if (prestamosActivos.size() >= limitePrestamos) {
            throw new LimitePrestamosException(
                    nombre + " ha alcanzado el límite de " + limitePrestamos + " préstamos.");
        }

        // 2. Intentar prestar el RECURSO (puede lanzar RecursoNoDisponibleException)
        recurso.prestar();      // ← llama al prestar() del recurso (Libro o Pelicula)

        // 3. Actualizar estado del usuario
        prestamosActivos.add(recurso);
        prestamosEsteMes++;

        System.out.println("📖 " + nombre + " tomó prestado: " + recurso.getTitulo()
                + " (" + prestamosActivos.size() + "/" + limitePrestamos + ")");
    }

    // ── Métodos propios ──────────────────────────────────────
    public boolean puedeTomarMasPrestados() {
        return prestamosActivos.size() < limitePrestamos;
    }

    public int getPrestamosRestantes() {
        return limitePrestamos - prestamosActivos.size();
    }

    public void reiniciarContadorMensual() {
        prestamosEsteMes = 0;
    }

    // ── Getters ──────────────────────────────────────────────
    public int getLimitePrestamos()    { return limitePrestamos; }
    public int getPrestamosEsteMes()   { return prestamosEsteMes; }

    @Override
    public String toString() {
        return "Básico · " + super.toString()
                + " | Límite: " + prestamosActivos.size() + "/" + limitePrestamos;
    }
}
