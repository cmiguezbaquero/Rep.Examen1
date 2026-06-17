package modelo;

import excepciones.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ══════════════════════════════════════════════════════
 *  SINGLETON — Biblioteca
 * ══════════════════════════════════════════════════════
 *
 *  PATRÓN SINGLETON — Memorízalo completo:
 *
 *  public class Biblioteca {
 *
 *      // 1. Instancia estática privada
 *      private static Biblioteca instancia;
 *
 *      // 2. Constructor PRIVADO (nadie puede hacer new Biblioteca())
 *      private Biblioteca(String nombre) { ... }
 *
 *      // 3. Método estático getInstancia()
 *      public static synchronized Biblioteca getInstancia(String nombre) {
 *          if (instancia == null) {
 *              instancia = new Biblioteca(nombre);
 *          }
 *          return instancia;
 *      }
 *  }
 *
 *  → synchronized: evita problemas si varios hilos lo llaman a la vez
 *  → Una sola instancia en todo el programa
 *
 *  ═══════════════════════════════════════════════════
 *  HASHMAP — TODOS LOS PATRONES:
 *
 *  HashMap<String, Usuario> usuarios:
 *    clave = email (String)
 *    valor = Usuario
 *
 *  ✔ insertar sin duplicado:
 *      if (map.containsKey(key)) throw ...
 *      map.put(key, value);
 *
 *  ✔ buscar:
 *      if (!map.containsKey(key)) throw ...
 *      return map.get(key);
 *
 *  ✔ recorrer valores:
 *      for (Usuario u : map.values()) { ... }
 * ══════════════════════════════════════════════════════
 */
public class Biblioteca {

    // ── SINGLETON: instancia estática privada ────────────────
    private static Biblioteca instancia;

    // ── Atributos ────────────────────────────────────────────
    private String nombre;
    private HashMap<String, Usuario>  usuarios;     // clave: email
    private HashMap<String, Recurso>  catalogo;     // clave: id del recurso
    private HashMap<String, Autor>    autores;      // clave: nombre
    private ArrayList<Coleccion>      coleccionesPublicas;
    private int totalPrestamosHistoricos;

    // ── SINGLETON: constructor PRIVADO ───────────────────────
    private Biblioteca(String nombre) {
        this.nombre                   = nombre;
        this.usuarios                 = new HashMap<>();
        this.catalogo                 = new HashMap<>();
        this.autores                  = new HashMap<>();
        this.coleccionesPublicas      = new ArrayList<>();
        this.totalPrestamosHistoricos = 0;

        System.out.println("📚 Biblioteca '" + nombre + "' inicializada.");
    }

    // ── SINGLETON: getInstancia ──────────────────────────────
    public static synchronized Biblioteca getInstancia(String nombre) {
        if (instancia == null) {
            instancia = new Biblioteca(nombre);
        }
        return instancia;
    }

    public static synchronized Biblioteca getInstancia() {
        return getInstancia("BiblioTech");
    }

    // Solo para tests: reiniciar
    public static synchronized void reiniciarInstancia() {
        instancia = null;
    }

    // ══════════════════════════════════════════════════════════
    //  GESTIÓN DE USUARIOS — patrones HashMap
    // ══════════════════════════════════════════════════════════

    public UsuarioBasico registrarUsuarioBasico(String nombre, String email, String password)
            throws UsuarioYaExisteException, EmailInvalidoException, PasswordDebilException {

        // PATRÓN: comprobar duplicado en HashMap
        if (usuarios.containsKey(email)) {
            throw new UsuarioYaExisteException(
                    "Ya existe un usuario con email: " + email);
        }

        // Crear (el constructor ya valida email y password)
        UsuarioBasico usuario = new UsuarioBasico(nombre, email, password);

        // Guardar en el HashMap
        usuarios.put(email, usuario);

        System.out.println("✅ Usuario básico registrado: " + nombre);
        return usuario;
    }

    public UsuarioPremium registrarUsuarioPremium(String nombre, String email, String password)
            throws UsuarioYaExisteException, EmailInvalidoException, PasswordDebilException {

        if (usuarios.containsKey(email)) {
            throw new UsuarioYaExisteException("Ya existe: " + email);
        }

        UsuarioPremium usuario = new UsuarioPremium(nombre, email, password);
        usuarios.put(email, usuario);

        System.out.println("⭐ Usuario premium registrado: " + nombre);
        return usuario;
    }

    // PATRÓN buscar en HashMap
    public Usuario buscarUsuarioPorEmail(String email) throws RecursoNoEncontradoException {

        if (!usuarios.containsKey(email)) {
            throw new RecursoNoEncontradoException("No existe usuario: " + email);
        }

        return usuarios.get(email);
    }

    // PATRÓN filtrar con instanceof
    public ArrayList<UsuarioPremium> getUsuariosPremium() {

        ArrayList<UsuarioPremium> resultado = new ArrayList<>();

        for (Usuario u : usuarios.values()) {       // .values() para recorrer HashMap
            if (u instanceof UsuarioPremium) {
                resultado.add((UsuarioPremium) u);
            }
        }

        return resultado;
    }

    public ArrayList<Usuario> getTodosLosUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    // ══════════════════════════════════════════════════════════
    //  GESTIÓN DEL CATÁLOGO
    // ══════════════════════════════════════════════════════════

    public void agregarRecurso(Recurso recurso) {
        catalogo.put(recurso.getId(), recurso);
        System.out.println("📥 Recurso añadido al catálogo: " + recurso.getTitulo());
    }

    public Recurso buscarPorId(String id) throws RecursoNoEncontradoException {

        if (!catalogo.containsKey(id)) {
            throw new RecursoNoEncontradoException("No existe recurso con id: " + id);
        }

        return catalogo.get(id);
    }

    // PATRÓN: buscar en lista por campo
    public ArrayList<Recurso> buscarPorTitulo(String termino)
            throws RecursoNoEncontradoException {

        ArrayList<Recurso> resultados = new ArrayList<>();

        for (Recurso r : catalogo.values()) {
            if (r.getTitulo().toLowerCase().contains(termino.toLowerCase())) {
                resultados.add(r);
            }
        }

        if (resultados.isEmpty()) {
            throw new RecursoNoEncontradoException("Sin resultados para: " + termino);
        }

        return resultados;
    }

    // PATRÓN: filtrar por tipo
    public ArrayList<Libro> getTodosLosLibros() {

        ArrayList<Libro> libros = new ArrayList<>();

        for (Recurso r : catalogo.values()) {
            if (r instanceof Libro) {
                libros.add((Libro) r);
            }
        }

        return libros;
    }

    public ArrayList<Pelicula> getTodasLasPeliculas() {

        ArrayList<Pelicula> peliculas = new ArrayList<>();

        for (Recurso r : catalogo.values()) {
            if (r instanceof Pelicula) {
                peliculas.add((Pelicula) r);
            }
        }

        return peliculas;
    }

    // PATRÓN: top N más populares
    public ArrayList<Recurso> obtenerTopRecursos(int cantidad) {

        ArrayList<Recurso> lista = new ArrayList<>(catalogo.values());

        // Ordenar por veces prestado (descendente)
        lista.sort((a, b) -> b.getVecesPrestado() - a.getVecesPrestado());

        // Devolver solo los primeros N
        int limite = Math.min(cantidad, lista.size());
        return new ArrayList<>(lista.subList(0, limite));
    }

    // ══════════════════════════════════════════════════════════
    //  GESTIÓN DE AUTORES
    // ══════════════════════════════════════════════════════════

    public Autor registrarAutor(String nombre, String pais) {
        Autor autor = new Autor(nombre, pais);
        autores.put(nombre, autor);
        return autor;
    }

    public Autor buscarAutor(String nombre) throws AutorNoEncontradoException {

        if (!autores.containsKey(nombre)) {
            throw new AutorNoEncontradoException("Autor no encontrado: " + nombre);
        }

        return autores.get(nombre);
    }

    // ══════════════════════════════════════════════════════════
    //  ESTADÍSTICAS — patrón resumen
    // ══════════════════════════════════════════════════════════

    public String obtenerEstadisticasGenerales() {

        // PATRÓN SUMA para contar premium
        int numPremium  = 0;
        int numBasicos  = 0;

        for (Usuario u : usuarios.values()) {
            if (u instanceof UsuarioPremium) numPremium++;
            else numBasicos++;
        }

        int numLibros    = getTodosLosLibros().size();
        int numPeliculas = getTodasLasPeliculas().size();

        return "\n══════════════════════════════\n"
                + "  ESTADÍSTICAS — " + nombre + "\n"
                + "══════════════════════════════\n"
                + "👥 Usuarios: " + usuarios.size()
                +    " (Premium: " + numPremium + ", Básicos: " + numBasicos + ")\n"
                + "📚 Libros en catálogo: " + numLibros + "\n"
                + "🎬 Películas en catálogo: " + numPeliculas + "\n"
                + "🔄 Total préstamos históricos: " + totalPrestamosHistoricos + "\n"
                + "══════════════════════════════";
    }

    // ── Getters ──────────────────────────────────────────────
    public String getNombre()          { return nombre; }
    public int getTotalUsuarios()      { return usuarios.size(); }
    public int getTotalRecursos()      { return catalogo.size(); }
    public int getTotalPrestamos()     { return totalPrestamosHistoricos; }
    public void incrementarPrestamos() { totalPrestamosHistoricos++; }

    @Override
    public String toString() {
        return "Biblioteca '" + nombre + "' | "
                + usuarios.size() + " usuarios | "
                + catalogo.size() + " recursos";
    }
}
