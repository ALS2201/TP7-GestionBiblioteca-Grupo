package biblioteca; 
import java.util.*;
/**
 * Clase que representa un libro de la biblioteca.
 * Cada libro tiene una ficha de prestamos donde se asientan
 * todos los prestamos realizados.
 * 
 * @author Grupo 
 * @version 1.0
 */
public class Libro
{
    // Atributos
    /** Titulo del libro */
    private String titulo;
    /** Numero de edicion */
    private int edicion;
    /** Editorial del libro */
    private String editorial;
    /** Anio de publicacion */
    private int anio;
    /** Ficha de prestamos del libro */
    private ArrayList<Prestamo> prestamos;
    
    /**
     * Constructor que crea un libro sin prestamos.
     * 
     * @param p_titulo Titulo del libro
     * @param p_edicion Numero de edicion
     * @param p_editorial Editorial
     * @param p_anio Anio de publicacion
     */
    public Libro(String p_titulo, int p_edicion, String p_editorial, int p_anio){
        this.setTitulo(p_titulo);
        this.setEdicion(p_edicion);
        this.setEditorial(p_editorial);
        this.setAnio(p_anio);
        this.setPrestamos(new ArrayList<Prestamo>());
    }
    
    // Setters
    
    /** Asigna el titulo del libro */
    private void setTitulo(String p_titulo){
        this.titulo = p_titulo;
    }
    
    /** Asigna la edicion del libro */
    private void setEdicion(int p_edicion){
        this.edicion = p_edicion;
    }
    
    /** Asigna la editorial del libro */
    private void setEditorial(String p_editorial){
        this.editorial = p_editorial;
    }
    
    /** Asigna el anio de publicacion */
    private void setAnio(int p_anio){
        this.anio = p_anio;
    }
    
    /** Asigna la ficha de prestamos */
    private void setPrestamos(ArrayList<Prestamo> p_prestamos){
        this.prestamos = p_prestamos;
    }
    
    // Getters
    
    /** Devuelve el titulo del libro */
    public String getTitulo(){
        return this.titulo;
    }
    
    /** Devuelve la edicion del libro */
    public int getEdicion(){
        return this.edicion;
    }
    
    /** Devuelve la editorial del libro */
    public String getEditorial(){
        return this.editorial;
    }
    
    /** Devuelve el anio de publicacion */
    public int getAnio(){
        return this.anio;
    }
    
    /** Devuelve la ficha de prestamos del libro */
    public ArrayList<Prestamo> getPrestamos(){
        return this.prestamos;
    }
    
    // Otros metodos
    
    /**
     * Agrega un prestamo a la ficha del libro.
     * 
     * @param p_prestamo Prestamo a agregar
     * @return true si se agrego correctamente
     */
    public boolean agregarPrestamo(Prestamo p_prestamo){
        return this.getPrestamos().add(p_prestamo);
    }
    
    /**
     * Retorna el ultimo prestamo del libro.
     * 
     * @return El ultimo prestamo, o null si el libro nunca fue prestado
     */
    public Prestamo ultimoPrestamo(){
        Prestamo ultimo = null;
        if(!this.getPrestamos().isEmpty()){
            ultimo = this.getPrestamos().get(this.getPrestamos().size() - 1);
        }
        return ultimo;
    }
    
    /**
     * Indica si el libro se encuentra prestado, es decir, si su
     * ultimo prestamo todavia no tiene fecha de devolucion.
     * 
     * @return true si el libro esta prestado
     */
    public boolean prestado(){
        Prestamo ultimo = this.ultimoPrestamo();
        return ultimo != null && ultimo.getFechaDevolucion() == null;
    }
    
    /**
     * Devuelve una representacion en texto del libro.
     * 
     * @return Cadena con el formato "Titulo: <<titulo>>"
     */
    public String toString(){
        return "Titulo: " + this.getTitulo();
    }
}
