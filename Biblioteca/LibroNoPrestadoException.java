package biblioteca;

/**
 * Excepción que se lanza cuando se intenta devolver un libro, o consultar quién lo tiene,
 * y ese libro no está prestado (está en la biblioteca).
 * Es una excepción "chequeada" (extiende Exception): quien llame al método está obligado a atraparla.
 *
 * @author Grupo
 */
public class LibroNoPrestadoException extends Exception {

    /** @param mensaje Texto que explica el motivo; se muestra al usuario en la interfaz */
    public LibroNoPrestadoException(String mensaje) {
        super(mensaje);
    }
}
