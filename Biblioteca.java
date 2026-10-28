
/**
 * Write a description of class Biblioteca here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Biblioteca
{
    private String nombre;
    private arrayList <Socio>socios;
    private arrayList<Libro> libros ;

    /**
     * Constructor for objects of class Biblioteca
     */
  
public Biblioteca(String p_nombre)
    {
        this.setNombre(p_nombre);
           }

  public Biblioteca(String p_nombre, arrayList <Socio> p_socios, arrayList <Libro> p_libros)
    {
        this.setNombre(p_nombre);
        this.arrayList <Libro> ();
        this.arrayList<Socio> ();
    }

public Biblioteca(String p_nombre, arrayList <Libro> p_libros)
    {
        this.setNombre(p_nombre);
        this.arrayList <Libro> ();
           }

public Biblioteca(String p_nombre, arrayList <Socio> p_socios )
    {
        this.setNombre(p_nombre);
        this.arrayList <Socio> ();
           }

public void nuevoLibro (String p_titulo,int p_edicion,String p_editorial,int p_anio)
{
return Libro.add(p_titulo,p_edicion,p_editorial,p_anio)

}

public void nuevoSocioEstudiante(int p_dniSocio,String p_nombre,String p_carrera){


}


    
}