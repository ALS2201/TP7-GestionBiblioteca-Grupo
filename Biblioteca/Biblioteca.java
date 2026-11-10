package biblioteca;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Clase que representa a la Biblioteca.
 * Administra las colecciones de libros y socios (estudiantes y docentes),
 * permitiendo registrar nuevos elementos y emitir listados formateados.
 * 
 * @author Grupo
 */
// Serializable: permite guardar toda la biblioteca (libros, socios y préstamos) en un archivo
public class Biblioteca implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    
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
     * NOTA: se eliminaron los constructores (String, ArrayList<Libro>) y
     * (String, ArrayList<Socio>) porque en Java ambos tienen la misma
     * "erasure" (ArrayList) y provocan el error "name clash".
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
     * Quita un libro del catálogo de la biblioteca.
     * No se puede quitar un libro que está prestado en este momento.
     * 
     * @param p_libro Libro a quitar
     * @return true si se quitó, false si el libro está prestado o no pertenece a la biblioteca
     */
    public boolean quitarLibro(Libro p_libro) {
        if (p_libro.prestado()) {
            return false;
        }
        return this.getLibros().remove(p_libro);
    }

    /**
     * Quita un socio de la biblioteca.
     * No se puede quitar un socio que todavía tiene libros sin devolver.
     * 
     * @param p_socio Socio a quitar
     * @return true si se quitó, false si tiene libros prestados o no pertenece a la biblioteca
     */
    public boolean quitarSocio(Socio p_socio) {
        if (p_socio.cantLibrosPrestados() > 0) {
            return false;
        }
        return this.getSocios().remove(p_socio);
    }

    /**
     * Registra un nuevo socio de tipo Estudiante y lo agrega a la biblioteca.
     * 
     * @param p_dniSocio DNI del estudiante
     * @param p_nombre Nombre completo del estudiante
     * @param p_carrera Carrera que cursa el estudiante
     */
    public void nuevoSocioEstudiante(int p_dniSocio, String p_nombre, String p_carrera) {
        if (this.buscarSocio(p_dniSocio) != null) {
            throw new IllegalArgumentException("Ya existe un socio con el D.N.I. " + p_dniSocio);
        }
        Estudiante nuevoEstudiante = new Estudiante(p_dniSocio, p_nombre, p_carrera);
        this.getSocios().add(nuevoEstudiante);
    }

    /**
     * Registra un nuevo socio docente en la biblioteca.
     *
     * @param p_dniSocio DNI del docente.
     * @param p_nombre Nombre y apellido del docente.
     * @param p_area Área de trabajo del docente.
     */
    public void nuevoSocioDocente(int p_dniSocio, String p_nombre, String p_area) {
        if (this.buscarSocio(p_dniSocio) != null) {
            throw new IllegalArgumentException("Ya existe un socio con el D.N.I. " + p_dniSocio);
        }
        Docente nuevoDocente = new Docente(p_dniSocio, p_nombre, p_area);
        this.getSocios().add(nuevoDocente);
    }

    /**
     * Realiza el préstamo de un libro a un socio.
     * El préstamo se registra tanto en el socio como en el libro.
     *
     * @param p_fechaRetiro Fecha en que se retira el libro.
     * @param p_socio Socio que solicita el préstamo.
     * @param p_libro Libro que se desea prestar.
     * @return true si se realizó el préstamo, false en caso contrario.
     */
    public boolean prestarLibro(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro) {
        if (p_socio.puedePedir() && !p_libro.prestado()) {
            Prestamo unPrestamo = new Prestamo(p_fechaRetiro, p_socio, p_libro);
            p_socio.agregarPrestamo(unPrestamo);
            p_libro.agregarPrestamo(unPrestamo);
            return true;
        }
        return false;
    } 

    /**
     * Registra la devolución de un libro con la fecha actual.
     * 
     * @param p_libro Libro a devolver
     * @throws LibroNoPrestadoException si el libro no se encuentra prestado
     */
    public void devolverLibro(Libro p_libro) throws LibroNoPrestadoException {
        if (!p_libro.prestado()) {
            throw new LibroNoPrestadoException("El libro " + p_libro.getTitulo() 
                    + " no se puede devolver ya que se encuentra en la biblioteca");
        }
        // Socio y libro comparten el mismo objeto Prestamo, por lo que
        // la fecha queda registrada en ambas fichas.
        p_libro.ultimoPrestamo().registrarFechaDevolucion(new GregorianCalendar());
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

    /**
     * Devuelve la cantidad de socios del tipo indicado ("Docente" o "Estudiante").
     * 
     * @param p_objeto Tipo de socio
     * @return Cantidad de socios de ese tipo
     */
    public int cantidadDeSociosPorTipo(String p_objeto) {
        int total = 0;
        for (Socio socio : this.getSocios()) {
            if (socio.soyDeLaClase().equalsIgnoreCase(p_objeto)) {
                total++;
            }
        }
        return total;
    }

    /**
     * Devuelve los préstamos vencidos al día de la fecha. Sólo se consideran
     * los libros que siguen prestados (sin fecha de devolución).
     * 
     * @return Colección de préstamos vencidos
     */
    public ArrayList<Prestamo> prestamosVencidos() {
        ArrayList<Prestamo> listaPrestamosVencidos = new ArrayList<Prestamo>();
        Calendar fechaActual = new GregorianCalendar();
    
        for (Socio socio : this.getSocios()) {
            for (Prestamo prestamo : socio.getPrestamos()) {
                if (prestamo.getFechaDevolucion() == null && prestamo.vencido(fechaActual)) {
                    listaPrestamosVencidos.add(prestamo);
                }
            }
        }
        return listaPrestamosVencidos;
    }

    /**
     * Devuelve la colección de docentes responsables.
     * 
     * @return Colección de docentes responsables
     */
    public ArrayList<Docente> docentesResponsables() {
        ArrayList<Docente> listaDocentesResponsables = new ArrayList<Docente>();
        for (Socio socio : this.getSocios()) {
            if (socio.soyDeLaClase().equalsIgnoreCase("Docente")) {
                Docente docente = (Docente) socio;
                if (docente.esResponsable()) {
                    listaDocentesResponsables.add(docente);
                }
            }
        }
        return listaDocentesResponsables;
    }

    /**
     * Devuelve el nombre del socio que tiene en su poder el libro.
     * 
     * @param p_libro Libro a consultar
     * @return Nombre del socio que lo tiene prestado
     * @throws LibroNoPrestadoException si el libro se encuentra en la biblioteca
     */
    public String quienTieneElLibro(Libro p_libro) throws LibroNoPrestadoException {
        // CORREGIDO: antes la condición estaba invertida (if prestado -> throw)
        if (!p_libro.prestado()) {
            throw new LibroNoPrestadoException("El libro se encuentra en la biblioteca");
        }
        return p_libro.ultimoPrestamo().getSocio().getNombre();
    }

    // --- Métodos de Listados ---

    /**
     * Genera el listado de socios según Formato1, con el total por tipo al final.
     * 
     * @return String con el listado de socios
     */
    public String listaDeSocios() {
        String listado = "Lista de Socios:\n\n";
        int i = 1;
        for (Socio s : this.getSocios()) {
            listado += i + ") D.N.I.: " + s.getDniSocio() + " || " + s.getNombre() 
                     + " (" + s.soyDeLaClase() + ") || Libros Prestados: " 
                     + s.cantLibrosPrestados() + "\n";
            i++;
        }
        listado += "**************************************\n";
        listado += " Cantidad de Socios del tipo Estudiante: " 
                 + this.cantidadDeSociosPorTipo("Estudiante") + "\n";
        listado += " Cantidad de Socios del tipo Docente: " 
                 + this.cantidadDeSociosPorTipo("Docente") + "\n";
        listado += "**************************************\n";
        return listado;
    }

    /**
     * Genera un listado con los títulos de la biblioteca, sin repetir
     * en caso de haber más de un ejemplar.
     * 
     * @return String con los títulos separados por saltos de línea
     */
    public String listaDeTitulos() {
        ArrayList<String> titulosVistos = new ArrayList<String>();
        String listado = "";
        for (Libro l : this.getLibros()) {
            if (!titulosVistos.contains(l.getTitulo())) {
                titulosVistos.add(l.getTitulo());
                listado += l.getTitulo() + "\n"; 
            }
        }
        return listado;
    }

    /**
     * Genera el listado de libros según Formato2 (Prestado: (Si|No)).
     * 
     * @return String con el listado de libros
     */
    public String listaDeLibros() {
        String listado = "Lista de Libros:\n\n";
        int i = 1;
        for (Libro l : this.getLibros()) {
            String estado = l.prestado() ? "Si" : "No";
            listado += i + ") " + l.toString() + " || Prestado: (" + estado + ")\n";
            i++;
        }
        return listado;
    }

    /**
     * Genera el listado de docentes responsables según Formato3.
     * 
     * @return String con el listado de docentes responsables
     */
    public String listaDeDocentesResponsables() {
        String listado = "Lista de Docentes Responsables:\n\n";
        for (Docente d : this.docentesResponsables()) {
            listado += "* D.N.I.: " + d.getDniSocio() + " || " + d.getNombre() 
                     + " (" + d.soyDeLaClase() + ") || Libros Prestados: " 
                     + d.cantLibrosPrestados() + "\n";
        }
        return listado;
    }
}