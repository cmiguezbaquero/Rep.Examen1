package modelo;

import excepciones.DuracionInvalidaException;
import excepciones.RecursoNoDisponibleException;
import excepciones.ValoracionInvalidaException;
import interfaces.Valorable;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * ══════════════════════════════════════════════════════
 *  CLASE ABSTRACTA — Recurso
 * ══════════════════════════════════════════════════════
 *
 *  CONCEPTO CLAVE:
 *  Una clase abstracta NO se puede instanciar directamente.
 *  Sirve de BASE para otras clases (Libro, Pelicula...).
 *
 *  Puede tener:
 *  ✔ atributos normales (protected → heredables)
 *  ✔ métodos concretos (con código)
 *  ✔ métodos abstractos (sin código → OBLIGATORIOS en hijas)
 *
 *  TRUCO DE EXAMEN:
 *  Si ves "abstract" en la clase padre, busca los métodos
 *  "abstract" y asegúrate de hacer @Override en la hija.
 * ══════════════════════════════════════════════════════
 */
public abstract class Recurso implements Valorable {

    // ── Atributos: protected para que las hijas los hereden ──
    protected String id;
    protected String titulo;
    protected int anyioPublicacion;
    protected boolean disponible;
    protected int vecesPrestado;        // contador de popularidad
    protected ArrayList<String> tags;
    protected Date fechaAlta;

    // Para implementar Valorable:
    protected ArrayList<Integer> valoraciones;  // lista de puntuaciones recibidas

    // ── Constructor ──────────────────────────────────────────
    /**
     *  PATRÓN DE EXAMEN:
     *  El constructor de la clase abstracta valida los datos
     *  y lanza excepción si algo falla.
     *
     *  Las clases hijas DEBEN llamar a super(...) como primera
     *  instrucción de su constructor.
     */
    public Recurso(String titulo, int anyioPublicacion) throws DuracionInvalidaException {

        // Validación → si falla, lanzamos excepción
        if (anyioPublicacion < 0 || anyioPublicacion > 2100) {
            throw new DuracionInvalidaException(
                    "Año de publicación inválido: " + anyioPublicacion);
        }

        // Inicialización de atributos
        this.id               = UUID.randomUUID().toString().substring(0, 8);
        this.titulo           = titulo;
        this.anyioPublicacion = anyioPublicacion;
        this.disponible       = true;   // disponible por defecto
        this.vecesPrestado    = 0;
        this.fechaAlta        = new Date();

        // ⚠ LISTAS: inicializar siempre en el constructor
        this.tags         = new ArrayList<>();
        this.valoraciones = new ArrayList<>();
    }

    // ── Método ABSTRACTO ─────────────────────────────────────
    /**
     *  CONCEPTO CLAVE:
     *  Este método NO tiene cuerpo aquí.
     *  Cada clase hija lo implementará a su manera.
     *
     *  Libro.prestar()  → comprueba copias disponibles
     *  Pelicula.prestar() → comprueba si no está en streaming
     */
    public abstract void prestar() throws RecursoNoDisponibleException;

    // ── Métodos CONCRETOS (heredables tal cual) ───────────────

    /**
     *  PATRÓN: marcar disponible / no disponible
     */
    public void marcarNoDisponible() {
        this.disponible = false;
    }

    public void marcarDisponible() {
        this.disponible = true;
    }

    /**
     *  PATRÓN: incrementar contador
     */
    public void incrementarPrestamos() {
        this.vecesPrestado++;
    }

    /**
     *  PATRÓN: añadir tag evitando duplicados
     *
     *  TRUCO:
     *  contains() → devuelve true si ya está
     *  add()      → añade si no está
     */
    public void agregarTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public boolean tieneTag(String tag) {
        return tags.contains(tag);
    }

    public boolean esPopular() {
        return vecesPrestado > 50;
    }

    // ── Implementación de Valorable ───────────────────────────
    /**
     *  PATRÓN: implementar interfaz
     *
     *  Si ves "implements Valorable" en la clase,
     *  DEBES tener puntuar() y getMediaValoraciones().
     *
     *  La validación sigue el patrón:
     *  if (condición mala) → throw
     */
    @Override
    public void puntuar(int puntuacion) throws ValoracionInvalidaException {

        // 1. Comprobar error
        if (puntuacion < 1 || puntuacion > 5) {
            throw new ValoracionInvalidaException(
                    "Puntuación inválida: " + puntuacion + ". Debe estar entre 1 y 5.");
        }

        // 2. Acción si todo va bien
        valoraciones.add(puntuacion);
    }

    @Override
    public double getMediaValoraciones() {

        // ⚠ Caso especial: lista vacía → devolver 0 (no dividir)
        if (valoraciones.isEmpty()) {
            return 0.0;
        }

        // PATRÓN SUMA:
        // int total = 0;
        // for (Tipo t : lista) { total += t; }
        int total = 0;
        for (int v : valoraciones) {
            total += v;
        }

        return (double) total / valoraciones.size();
    }

    // ── Getters y Setters ────────────────────────────────────
    public String getId()              { return id; }
    public String getTitulo()          { return titulo; }
    public void setTitulo(String t)    { this.titulo = t; }
    public int getAnyioPublicacion()   { return anyioPublicacion; }
    public boolean isDisponible()      { return disponible; }
    public int getVecesPrestado()      { return vecesPrestado; }
    public ArrayList<String> getTags() { return new ArrayList<>(tags); } // copia defensiva
    public Date getFechaAlta()         { return fechaAlta; }

    // ── toString ─────────────────────────────────────────────
    @Override
    public String toString() {
        return "[" + id + "] " + titulo + " (" + anyioPublicacion + ")"
                + " | Disponible: " + disponible
                + " | Valoración: " + String.format("%.1f", getMediaValoraciones());
    }

    // ── equals y hashCode ────────────────────────────────────
    /**
     *  PATRÓN: comparar por ID
     *  Siempre igual en todos los ejercicios.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recurso otro = (Recurso) obj;
        return this.id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}