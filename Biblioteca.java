
/**
 * Write a description of class Biblioteca here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Biblioteca
{
    private String nombre;
    private ArrayList <Socio>socios;
    private ArrayList<Libro> libros ;

    /**
     * Constructor for objects of class Biblioteca
     */
  
public Biblioteca(String p_nombre)
    {
        this.setNombre(p_nombre);
        this.setLibro(new ArrayList <Libro> ());
        this.setSocio(new ArrayList<Socio> ());
           }

public Biblioteca(String p_nombre, ArrayList <Libro> p_libros,ArrayList <Socio> p_socios
    {
        this.setNombre(p_nombre);
        this.setSocio(p_socio);
        this.setLibro(p_libro);
           }

  public Biblioteca(String p_nombre, arrayList <Socio> p_socios)
    {
        this.setNombre(p_nombre);
        this.setSocio(p_socio);
        this.setLibro(new ArrayList<Libro> p_libro);
    }

public Biblioteca(String p_nombre, arrayList <Libro> p_libros)
    {
        this.setNombre(p_nombre);
        this.setLibro(p_libro);
        this.setSocio(new ArrayList<Socio> p_socio);
           }

public void nuevoLibro (String p_titulo,int p_edicion,String p_editorial,int p_anio)
{
Libro unLibro= new Libro(p_titulo,p_edicion,p_editorial,p_anio);
this.getLibro().add(unLibro);
}

public void nuevoSocioEstudiante(int p_dniSocio,String p_nombre,String p_carrera){


}


    
}