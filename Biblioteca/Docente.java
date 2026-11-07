package Biblioteca;

/**
 * Clase que representa a un docente, que es un tipo de socio.
 * Los docentes tienen un área de especialización y un préstamo fijo de 5 días.
 */
import java.util.*;

public class Docente extends Socio
{
    private String area;

    /**
     * Constructor de la clase para cardinalidad 0
     * @param p_dniSocio
     * @param p_nombre
     * @param p_area
     */
    public Docente(int p_dniSocio, String p_nombre, String p_area)
    {
        super(p_dniSocio, p_nombre, 5);
        this.setArea(p_area);
    }

    /**
     * Constructor de la clase para cardinalidad 1...n
     * @param p_dniSocio
     * @param p_nombre
     * @param p_prestamos
     * @param p_area
     */
    public Docente(int p_dniSocio, String p_nombre, ArrayList<Prestamo> p_prestamos, String p_area)
    {
        // 1. Llamamos al constructor de Socio con los 3 parametros requeridos (5 dias de prestamo para el Docente)
        super(p_dniSocio, p_nombre, 5);
        
        // 2. Asociamos la lista de prestamos usando el getter heredado
        this.getPrestamos().addAll(p_prestamos);
        
        // 3. Asignamos el area
        this.setArea(p_area);
    }

    private void setArea(String p_area) {
        this.area = p_area;
    }

    public String getArea() {
        return this.area;
    }

    /**
     * Método que devuelve la clase del socio
     * @param no recibe parametros
     * @return retorna un String con el nombre de la clase
     */
    public String soyDeLaClase() {
        return "Docente";
    }

    /**
     * Método que determina si el docente es responsable, en base a si
     * tuvo o tiene algún préstamo vencido.
     * @param no recibe parametros
     * @return
     */
    public boolean esResponsable(){
        boolean vencido = false;
        for(Prestamo p : this.getPrestamos()){
            Calendar fechaEvaluacion = (p.getFechaDevolucion() != null) ? p.getFechaDevolucion() : Calendar.getInstance();
            
            if (p.vencido(fechaEvaluacion)) {
                vencido = true;
            }
        }
        
        return this.puedePedir() && (!vencido);
    }

    /**
     * Método que permite cambiar los días de préstamo si el docente es responsable
     * @param p_dias
     * @return no retorna nada
     */
    public void cambiarDiasDePrestamos(int p_dias) {
        if(this.esResponsable()){
            this.setDiasPrestamo(p_dias);
        }
    }
}   
