/**
 * Clase que representa a un estudiante, que es un tipo de socio.
 * Los estudiantes tienen una carrera y un préstamo fijo de 20 días.
 */
import java.util.*;

public class Estudiante extends Socio
{
    private String carrera;

    /**
     * Constructor de la clase para cardinalidad 0
     * @param p_dniSocio
     * @param p_nombre
     * @param p_carrera
     */
    public Estudiante(int p_dniSocio, String p_nombre, String p_carrera)
    {
        super(p_dniSocio, p_nombre, 20);
        this.setCarrera(p_carrera);
    }

    /**
     * Constructor de la clase para cardinalidad 1...n
     * @param p_dniSocio
     * @param p_nombre
     * @param p_prestamos
     * @param p_carrera
     */
    public Estudiante(int p_dniSocio, String p_nombre, ArrayList <Prestamo> p_prestamos, String p_carrera)
    {
        super(p_dniSocio, p_nombre, 20, p_prestamos);
        this.setCarrera(p_carrera);
    }

    private void setCarrera(String p_carrera) {
        this.carrera = p_carrera;
    }

    public String getCarrera() {
        return this.carrera;
    }

    /**
     * Método que devuelve la clase del socio
     * @param no recibe parametros
     * @return retorna un String con el nombre de la clase
     */
    public String soyDeLaClase() {
        return "Estudiante";
    }

    /**
     * Método que determina si el estudiante puede pedir un nuevo libro
     * @param no recibe parametros
     * @return retorna true si puede pedir, false si no puede
     */
    public boolean puedePedir() {
        if(super.puedePedir() && (this.cantLibrosPrestados() <= 3)){
            return true;
        }else{
            return false;
        }
    }
}
