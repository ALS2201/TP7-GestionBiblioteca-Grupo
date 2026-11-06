 
import java.util.ArrayList;
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

 /**
     * Constructor que inicializa la biblioteca con una colección de libros.
     *
     * @param p_nombre Nombre de la biblioteca.
     * @param p_libros Colección inicial de libros.
     */
     public Biblioteca(String p_nombre, ArrayList<Libro> p_libros) {
        this.setNombre(p_nombre);
        this.setSocios(new ArrayList<Socio>());
        this.setLibros(p_libros);
    }

 /**
     * Constructor que inicializa la biblioteca con una colección de socios.
     *
     * @param p_nombre Nombre de la biblioteca.
     * @param p_socios Colección inicial de socios.
     */
 public Biblioteca(String p_nombre, ArrayList<Socio> p_socios) {
        this.setNombre(p_nombre);
        this.setSocios(p_socios);
        this.setLibros(new ArrayList<Libro>());
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
     * Registra un nuevo socio docente en la biblioteca.
     *
     * @param p_dniSocio DNI del docente.
     * @param p_nombre Nombre y apellido del docente.
     * @param p_area Área de trabajo del docente.
     */
 public void nuevoSocioDocente(int p_dniSocio,String p_nombre,String p_area) {
        Docente nuevoDocente =new Docente(p_dniSocio, p_nombre, p_area);
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
    public boolean prestarLibro(Calendar p_fechaRetiro,Socio p_socio,Libro p_libro) {
        if (p_socio.puedePedir() && !p_libro.prestado()) {
            Prestamo unPrestamo =new Prestamo(p_fechaRetiro, p_socio, p_libro);
            p_socio.getPrestamos().add(unPrestamo);
            p_libro.getPrestamos().add(unPrestamo);
            return true;
        }
        return false;
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
 // parte nair ordenar 
 public void devolverLibro(Libro p_libro) throws LibroNoPrestadoException{
        if(p_libro.prestado() == false){
            throw new LibroNoPrestadoException("El libro " + p_libro.getTitulo() + " no se puede devolver ya que se encuentra en la biblioteca");
        }
        else{
            //Instanciamos la fecha actual
            Calendar fechaActual = new GregorianCalendar();
            //Actualizamos el estado del prestamo del libro
            p_libro.ultimoPrestamo().registrarFechaDevolucion(fechaActual);
            //Como socio y libro comparten el mismo prestamo, este objeto se actualiza en ambas listas
        }
    }
    
    
    public int cantidadDeSociosPorTipo(String p_objeto){
        //No lo hacemos, pero aca se podria lanzar exception que diga
        //"Parametro no coincide con ningun tipo de socio!"
        int total = 0;
        for (Socio socio : this.getSocios()){
            if (socio.soyDeLaClase().equalsIgnoreCase(p_objeto)){
                total++;
            }
        }
        return total;
    }
    
    
    
    public ArrayList<Prestamo> prestamosVencidos() {
      // Se instancia la lista donde se almacenarán los préstamos vencidos
      ArrayList<Prestamo> listaPrestamosVencidos = new ArrayList<>();
    
      // Se obtiene la fecha actual para realizar la comparación
      Calendar fechaActual = new GregorianCalendar();
    
      // Se recorren los socios registrados y sus préstamos
      for (Socio socio : this.getSocios()) {
        for (Prestamo prestamo : socio.getPrestamos()) {
          // Si el préstamo está vencido, se agrega a la lista
          if (prestamo.vencido(fechaActual)) {
            listaPrestamosVencidos.add(prestamo);
          }
        }
      }
    
      return listaPrestamosVencidos;
    }
    
    
    
    public ArrayList<Docente> docentesResponsables(){
        ArrayList<Docente> listaDocentesResponsables = new ArrayList<>();
        
        //Debemos recorrer la lista de socios, revisar que sean docentes, y si es responsable
        for (Socio socio : this.getSocios()){
            if(socio.soyDeLaClase().equalsIgnoreCase("Docente")){
                //En este IF tambien hay que realizar un casteo, dentro de parentesis obviamente
                if(((Docente)socio).esResponsable()){
                    //Hay que hacer un cast a Docente para poder agregarlo a la lista
                    listaDocentesResponsables.add((Docente) socio);
                }
            }
        }
        
        return listaDocentesResponsables;
    }
    
    
    
    public String quienTieneElLibro(Libro p_libro) throws LibroNoPrestadoException {
      // Si el libro no está prestado, se lanza la excepción LibroNoPrestadoException
      if (p_libro.prestado()) throw new LibroNoPrestadoException("El libro se encuentra en la biblioteca");

      // Se obtiene el último préstamo del libro
      Prestamo ultimoPrestamo = p_libro.ultimoPrestamo();

      // Se devuelve el nombre del socio relacionado al último préstamo
      return ultimoPrestamo.getSocio().getNombre();
    }
    
}
