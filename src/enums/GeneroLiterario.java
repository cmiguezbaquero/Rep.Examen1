package enums;

/**
 * ══════════════════════════════════════════════════════
 *  ENUMS — Todo en un fichero para tenerlos a mano
 * ══════════════════════════════════════════════════════
 *
 *  CONCEPTO CLAVE:
 *  Un enum es una clase especial con valores FIJOS.
 *  Puede tener atributos, constructor y métodos.
 *
 *  TRUCO DE EXAMEN:
 *  El constructor del enum es siempre package-private
 *  (sin modificador de acceso).
 *  Los valores se declaran ANTES que los atributos.
 * ══════════════════════════════════════════════════════
 */

// ── Categorías de los recursos ──────────────────────
class GeneroLiterario {
    // (vacío, ver abajo — los ponemos separados para claridad)
}

// Lo ponemos en ficheros individuales para que Java los vea bien
// ⚠ En un proyecto real cada enum va en su propio .java
// Aquí los mostramos todos juntos como REFERENCIA DE ESTUDIO

/**
 * ══════════════════════════════════════════════════════
 * ENUM con atributos y métodos — PATRÓN COMPLETO
 * ══════════════════════════════════════════════════════
 *
 *  Archivo real → enums/GeneroLiterario.java
 *
 *  public enum GeneroLiterario {
 *
 *      // 1. VALORES  (siempre primero, separados por coma)
 *      NOVELA("Novela", "Ficción en prosa"),
 *      CIENCIA_FICCION("Ciencia Ficción", "Ficción especulativa"),
 *      TERROR("Terror", "Literatura de miedo"),
 *      BIOGRAFIA("Biografía", "Vida real"),
 *      INFANTIL("Infantil", "Para niños");
 *
 *      // 2. ATRIBUTOS privados
 *      private final String nombre;
 *      private final String descripcion;
 *
 *      // 3. CONSTRUCTOR  (sin public/private → package-private)
 *      GeneroLiterario(String nombre, String descripcion) {
 *          this.nombre = nombre;
 *          this.descripcion = descripcion;
 *      }
 *
 *      // 4. GETTERS
 *      public String getNombre() { return nombre; }
 *      public String getDescripcion() { return descripcion; }
 *
 *      // 5. toString opcional
 *      @Override
 *      public String toString() { return nombre; }
 *  }
 * ══════════════════════════════════════════════════════
 *
 *  Lo mismo para los demás enums:
 *
 *  TipoSuscripcion:
 *      GRATUITO(0.0, 3, false),
 *      BASICO(4.99, 10, false),
 *      PREMIUM(9.99, -1, true);   // -1 = ilimitado
 *
 *      → atributos: double precio, int limitePrestamosMes, boolean sinLimite
 *
 *  EstadoRecurso:
 *      DISPONIBLE, PRESTADO, RESERVADO, BAJA
 *      → enum simple, sin atributos
 *
 *  CriterioOrden:
 *      TITULO, AUTOR, ANYIO_PUBLICACION, POPULARIDAD, VALORACION
 *      → enum simple
 * ══════════════════════════════════════════════════════
 */

