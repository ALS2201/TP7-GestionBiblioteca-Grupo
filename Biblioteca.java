 

import java.util.ArrayList;

/**
 * Clase que representa a la Biblioteca.
 * Administra las colecciones de libros y socios (estudiantes y docentes),
 * permitiendo registrar nuevos elementos y emitir listados formateados.
 * 
 * @author 
 */
public class Biblioteca {
    
    // Atributos
    
    /** Nombre de la institución o biblioteca */
    private String nombre;
    /** Lista de socios registrados (Estudiantes y Docentes) */
    private ArrayList<Socio> socios;
    /** Lista de libros disponibles en el catálogo */
    private ArrayList<Libro> libros;

    /**
     * Constructor de la clase Biblioteca que inicializa las listas vacías.
     * 
     * @param p_nombre Nombre de la biblioteca
     */
    public Biblioteca(String p_nombre) {
        this.setNombre(p_nombre);
        this.setLibros(new ArrayList<Libro>());
        this.setSocios(new ArrayList<Socio>());
    }

    /**
     * Constructor de la clase Biblioteca con colecciones preexistentes.
     * 
     * @param p_nombre Nombre de la biblioteca
     * @param p_libros Lista de libros inicial
     * @param p_socios Lista de socios inicial
     */
    public Biblioteca(String p_nombre, ArrayList<Libro> p_libros, ArrayList<Socio> p_socios) {
        this.setNombre(p_nombre);
        this.setLibros(p_libros);
        this.setSocios(p_socios);
    }

    // --- Setters y Getters ---

    /**
     * Asigna el nombre a la biblioteca.
     * @param p_nombre Nombre a asignar
     */
    private void setNombre(String p_nombre) {
        this.nombre = p_nombre;
    }
    
    /**
     * Devuelve el nombre de la biblioteca.
     * @return Nombre de la biblioteca
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Asigna la lista de socios.
     * @param p_socios Lista de socios
     */
    private void setSocios(ArrayList<Socio> p_socios) {
        this.socios = p_socios;
    }
    
    /**
     * Devuelve la lista de socios de la biblioteca.
     * @return ArrayList con los socios registrados
     */
    public ArrayList<Socio> getSocios() {
        return this.socios;
    }

    /**
     * Asigna la lista de libros.
     * @param p_libros Lista de libros
     */
    private void setLibros(ArrayList<Libro> p_libros) {
        this.libros = p_libros;
    }
    
    /**
     * Devuelve la lista de libros de la biblioteca.
     * @return ArrayList con los libros registrados
     */
    public ArrayList<Libro> getLibros() {
        return this.libros;
    }

    // --- Métodos de Gestión ---

    /**
     * Registra un nuevo libro y lo agrega a la colección de la biblioteca.
     * 
     * @param p_titulo Título del libro
     * @param p_edicion Número de edición
     * @param p_editorial Nombre de la editorial
     * @param p_anio Año de publicación
     */
    public void nuevoLibro(String p_titulo, int p_edicion, String p_editorial, int p_anio) {
        Libro unLibro = new Libro(p_titulo, p_edicion, p_editorial, p_anio);
        this.getLibros().add(unLibro);
    }

    /**
     * Registra un nuevo socio de tipo Estudiante y lo agrega a la biblioteca.
     * 
     * @param p_dniSocio DNI del estudiante
     * @param p_nombre Nombre completo del estudiante
     * @param p_carrera Carrera que cursa el estudiante
     */
    public void nuevoSocioEstudiante(int p_dniSocio, String p_nombre, String p_carrera) {
        Estudiante nuevoEstudiante = new Estudiante(p_dniSocio, p_nombre, p_carrera);
        this.getSocios().add(nuevoEstudiante);
    }

    /**
     * Busca un socio dentro de la colección utilizando su número de DNI.
     * 
     * @param p_dni DNI del socio a buscar
     * @return El objeto Socio encontrado, o null si no existe en la lista
     */
    public Socio buscarSocio(int p_dni) {
        for (Socio s : this.getSocios()) {
            if (s.getDniSocio() == p_dni) {
                return s; 
            }
        }
        return null; 
    }

    // --- Métodos de Listados ---

    /**
     * Genera un listado en formato texto de todos los socios registrados.
     * 
     * @return String con el detalle enumerado de cada socio y su cantidad de préstamos
     */
    public String listaDeSocios() {
        String listado = "";
        int i = 1;
        for (Socio s : this.getSocios()) {
            listado += " " + i + ")_ D.N.I: " + s.getDniSocio() + " || " + s.getNombre() + " (" + 
                       s.soyDeLaClase() + ") || Libros Prestados: " + s.cantLibrosPrestados() + "\n";
            i++;
        }
        return listado;
    }

    /**
     * Genera un listado en formato texto con los títulos de todos los libros.
     * Requiere que la clase Libro tenga implementado el método getTitulo().
     * 
     * @return String con los títulos separados por saltos de línea
     */
    public String listaDeTitulos() {
        String listado = "";
        for (Libro l : this.getLibros()) {
            listado += l.getTitulo() + "\n"; 
        }
        return listado;
    }

    /**
     * Genera un listado detallado de todos los libros y su estado actual.
     * 
     * @return String con el detalle de cada libro enumerado indicando si está prestado
     */
    public String listaDeLibros() {
        String listado = "";
        int i = 1;
        for (Libro l : this.getLibros()) {
            listado += " " + i + ")_" + l.toString() + " || Prestado: (" + l.prestado() + ")\n";
            i++;
        }
        return listado;
    }

    /**
     * Genera un listado exclusivo de los socios de la clase Docente 
     * que cumplen con la condición de ser responsables (sin préstamos vencidos).
     * 
     * @return String con el detalle enumerado de los docentes responsables
     */
    public String listaDeDocentesResponsables() {
        String listado = "";
        for (Socio s : this.getSocios()) {
            if (s.soyDeLaClase().equalsIgnoreCase("Docente")) {
                Docente d = (Docente) s; 
                if (d.esResponsable()) { 
                    listado += "* D.N.I: " + d.getDniSocio() + " || " + d.getNombre() + " (Docente) || Libros Prestados: " + d.cantLibrosPrestados() + "\n";
                }
            }
        }
        return listado;
    }
}
