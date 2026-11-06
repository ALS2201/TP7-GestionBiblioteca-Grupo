package biblioteca;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class GestionBiblioteca {
    public static void main(String[] args) {
        // 1. Instanciar la Biblioteca principal
        Biblioteca miBiblioteca = new Biblioteca("Biblioteca Central UTN");

        // 2. Cargar Socios (Estudiantes y Docentes)
        miBiblioteca.nuevoSocioEstudiante(31987123, "Cesar Milstein", "Bioingeniería");
        miBiblioteca.nuevoSocioEstudiante(28987498, "Francisco Paenza", "Matemática");
        miBiblioteca.nuevoSocioDocente(14524782, "Juan Perez", "Sistemas");
        miBiblioteca.nuevoSocioDocente(17982110, "Juan Fernández", "Informática");
        miBiblioteca.nuevoSocioDocente(10912002, "María Alegre", "Física");

        // 3. Cargar Libros
        miBiblioteca.nuevoLibro("Programando con JAVA", 3, "Anaya", 2020);
        miBiblioteca.nuevoLibro("Longman. Diccionario Pocket", 2, "Longman", 2015);
        miBiblioteca.nuevoLibro("Vivir para contarla", 1, "Sudamericana", 2002);

        // 4. Registrar préstamos (sin esto no hay datos para responder las consultas)
        Socio juanPerez = miBiblioteca.buscarSocio(14524782);
        Socio juanFernandez = miBiblioteca.buscarSocio(17982110);
        Socio cesar = miBiblioteca.buscarSocio(31987123);

        Libro programando = miBiblioteca.getLibros().get(0);
        Libro longman = miBiblioteca.getLibros().get(1);

        // Juan Perez retira "Programando con JAVA" hoy (vigente)
        miBiblioteca.prestarLibro(new GregorianCalendar(), juanPerez, programando);

        // Cesar Milstein retira el diccionario hoy (vigente)
        miBiblioteca.prestarLibro(new GregorianCalendar(), cesar, longman);

        // Juan Fernández retira "Vivir para contarla" hace 10 días:
        // los docentes tienen 5 días, por lo tanto queda VENCIDO (no es responsable)
        Calendar hace10Dias = new GregorianCalendar();
        hace10Dias.add(Calendar.DAY_OF_YEAR, -10);
        miBiblioteca.prestarLibro(hace10Dias, juanFernandez, miBiblioteca.getLibros().get(2));

        // ====================================================================
        // RESPUESTAS A LAS PREGUNTAS DEL ENUNCIADO
        // ====================================================================
        System.out.println("==================================================");
        System.out.println("   INFORMES Y REPORTES - GESTIÓN DE BIBLIOTECA    ");
        System.out.println("==================================================\n");

        // a) ¿Qué cantidad de socios de tipo Estudiante hay?
        System.out.println("1. Cantidad de socios Estudiantes: " 
                + miBiblioteca.cantidadDeSociosPorTipo("Estudiante"));
        System.out.println("--------------------------------------------------");

        // b) ¿Cuál es la lista de docentes que nunca han adeudado ni adeudan libros?
        System.out.println("2.");
        System.out.println(miBiblioteca.listaDeDocentesResponsables());
        System.out.println("--------------------------------------------------");

        // c) ¿Cuál es la lista de libros? ¿Y la de socios?
        System.out.println("3.a.");
        System.out.println(miBiblioteca.listaDeLibros());
        System.out.println("3.b.");
        System.out.println(miBiblioteca.listaDeSocios());
        System.out.println("--------------------------------------------------");

        // d) ¿Qué socio tiene prestado el libro "Programando con JAVA"?
        // Se usa el libro que ya está en la biblioteca (no uno nuevo con "new Libro"),
        // porque el préstamo está registrado en ese objeto.
        System.out.println("4. Consulta de préstamo de un libro:");
        try {
            String socioConLibro = miBiblioteca.quienTieneElLibro(programando);
            System.out.println("El libro \"" + programando.getTitulo() + "\" lo tiene: " + socioConLibro);
        } catch (LibroNoPrestadoException e) {
            System.out.println("Aviso: " + e.getMessage());
        }
    }
}
