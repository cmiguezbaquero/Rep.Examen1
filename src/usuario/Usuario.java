package usuario;

import excepciones.AnuncioRequeridoException;
import excepciones.EmailInvalidoException;
import excepciones.LimitePrestamosException;
import excepciones.PasswordDebilException;
import excepciones.RecursoNoDisponibleException;
import modelo.Coleccion;
import modelo.Recurso;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * ══════════════════════════════════════════════════════
 *  CLASE ABSTRACTA — Usuario
 * ══════════════════════════════════════════════════════
 *
 *  PATRÓN:
 *  Una clase abstracta de usuario tiene:
 *  - Atributos protected (heredables)
 *  - Constructor con validaciones → throws
 *  - Un método abstracto (el comportamiento diferente)
 *  - Métodos concretos compartidos por todos los usuarios
 *
 *  VALIDACIONES típicas de examen:
 *  - email: debe contener "@" y "."
 *  - password: mínimo 8 caracteres
 * ══════════════════════════════════════════════════════
 */
public abstract class Usuario {

    // ── Atributos protected ──────────────────────────────────
    protected String id;
    protected String nombre;
    protected String email;
    protected String password;
    protected ArrayList<Recurso> prestamosActivos;
    protected ArrayList<Recurso> historialPrestamos;
    protected ArrayList<Coleccion> misColecciones;
    protected Date fechaRegistro;

    // ── Constructor ──────────────────────────────────────────
    public Usuario(String nombre, String email, String password)
            throws EmailInvalidoException, PasswordDebilException {

        // Validar antes de asignar
        validarEmail(email);       // lanza EmailInvalidoException si falla
        validarPassword(password); // lanza PasswordDebilException si falla

        this.id                 = UUID.randomUUID().toString().substring(0, 8);
        this.nombre             = nombre;
        this.email              = email;
        this.password           = password;
        this.prestamosActivos   = new ArrayList<>();
        this.historialPrestamos = new ArrayList<>();
        this.misColecciones     = new ArrayList<>();
        this.fechaRegistro      = new Date();
    }

    // ── Métodos de validación ────────────────────────────────
    /**
     *  PATRÓN DE VALIDACIÓN:
     *  if (condición mala) throw new Exception("mensaje");
     *
     *  Para email: contiene "@" Y contiene "."
     *  Para password: longitud >= 8
     */
    private void validarEmail(String email) throws EmailInvalidoException {
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new EmailInvalidoException("Email inválido: " + email);
        }
    }

    private void validarPassword(String password) throws PasswordDebilException {
        if (password == null || password.length() < 8) {
            throw new PasswordDebilException(
                    "La contraseña debe tener al menos 8 caracteres.");
        }
    }

    // ── Método ABSTRACTO ─────────────────────────────────────
    /**
     *  Cada tipo de usuario tiene comportamiento diferente al pedir prestado:
     *  - UsuarioBasico: máximo 3 préstamos activos
     *  - UsuarioPremium: sin límite
     */
    public abstract void pedirPrestado(Recurso recurso)
            throws RecursoNoDisponibleException, LimitePrestamosException;

    // ── Métodos CONCRETOS compartidos ────────────────────────

    public void devolverRecurso(Recurso recurso) {

        // Eliminar de préstamos activos
        boolean eliminado = prestamosActivos.remove(recurso);

        if (eliminado) {
            recurso.devolver();                     // llama al devolver del recurso
            historialPrestamos.add(recurso);        // guardar en historial
            System.out.println("📚 " + nombre + " devolvió: " + recurso.getTitulo());
        } else {
            System.out.println("⚠ " + nombre + " no tenía prestado: " + recurso.getTitulo());
        }
    }

    public Coleccion crearColeccion(String nombreColeccion) {
        Coleccion col = new Coleccion(nombreColeccion);
        misColecciones.add(col);
        System.out.println("📂 Colección creada: " + nombreColeccion);
        return col;
    }

    public void agregarAlHistorial(Recurso recurso) {
        // Límite de historial para no crecer infinito
        if (historialPrestamos.size() >= 200) {
            historialPrestamos.remove(0);   // eliminar el más antiguo
        }
        historialPrestamos.add(recurso);
    }

    // ── Getters/Setters ──────────────────────────────────────
    public String getId()                         { return id; }
    public String getNombre()                     { return nombre; }
    public void setNombre(String nombre)          { this.nombre = nombre; }
    public String getEmail()                      { return email; }
    public int getNumPrestamosActivos()           { return prestamosActivos.size(); }
    public ArrayList<Recurso> getPrestamosActivos()   { return new ArrayList<>(prestamosActivos); }
    public ArrayList<Recurso> getHistorialPrestamos() { return new ArrayList<>(historialPrestamos); }
    public ArrayList<Coleccion> getMisColecciones()   { return new ArrayList<>(misColecciones); }
    public Date getFechaRegistro()                { return fechaRegistro; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " <" + email + ">"
                + " | Préstamos activos: " + prestamosActivos.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Usuario)) return false;
        Usuario otro = (Usuario) obj;
        return this.id.equals(otro.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
