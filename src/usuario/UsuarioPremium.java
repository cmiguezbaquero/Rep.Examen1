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
//  USUARIO PREMIUM — sin límite, con descargas
// ════════════════════════════════════════════════════════
class UsuarioPremium extends Usuario {

    // ── Atributos propios ────────────────────────────────────
    private boolean descargasOffline;
    private int maxDescargas;
    private java.util.ArrayList<Recurso> descargados;
    private String calidadPreferida;    // "ALTA", "MEDIA", "BAJA"

    private static final int MAX_DESCARGAS_DEFAULT = 50;

    // ── Constructor ──────────────────────────────────────────
    public UsuarioPremium(String nombre, String email, String password)
            throws EmailInvalidoException, PasswordDebilException {

        super(nombre, email, password);     // ← SIEMPRE PRIMERO

        this.descargasOffline = true;
        this.maxDescargas     = MAX_DESCARGAS_DEFAULT;
        this.descargados      = new java.util.ArrayList<>();
        this.calidadPreferida = "ALTA";
    }

    // ── @Override del método abstracto ──────────────────────
    /**
     *  UsuarioPremium NO tiene límite de préstamos activos.
     *  Solo comprueba que el recurso esté disponible.
     */
    @Override
    public void pedirPrestado(Recurso recurso)
            throws RecursoNoDisponibleException, LimitePrestamosException {

        // Sin límite de usuario, solo comprobamos el recurso
        recurso.prestar();

        prestamosActivos.add(recurso);

        System.out.println("⭐ [PREMIUM] " + nombre + " tomó prestado: " + recurso.getTitulo()
                + " (sin límite)");
    }

    // ── Métodos propios ──────────────────────────────────────
    public void descargarOffline(Recurso recurso)
            throws excepciones.LimitePrestamosException {

        if (descargados.size() >= maxDescargas) {
            throw new excepciones.LimitePrestamosException(
                    "Límite de descargas alcanzado: " + maxDescargas);
        }

        if (descargados.contains(recurso)) {
            System.out.println("⚠ Ya tienes descargado: " + recurso.getTitulo());
            return;
        }

        descargados.add(recurso);
        System.out.println("⬇ Descargado offline: " + recurso.getTitulo());
    }

    public boolean eliminarDescarga(Recurso recurso) {
        return descargados.remove(recurso);
    }

    public int getDescargasRestantes() {
        return maxDescargas - descargados.size();
    }

    // ── Getters ──────────────────────────────────────────────
    public boolean isDescargasOffline()     { return descargasOffline; }
    public int getMaxDescargas()            { return maxDescargas; }
    public int getNumDescargados()          { return descargados.size(); }
    public java.util.ArrayList<Recurso> getDescargados() {
        return new java.util.ArrayList<>(descargados);
    }
    public String getCalidadPreferida()     { return calidadPreferida; }
    public void setCalidadPreferida(String c) { this.calidadPreferida = c; }

    @Override
    public String toString() {
        return "Premium · " + super.toString()
                + " | Descargas: " + descargados.size() + "/" + maxDescargas;
    }
}
