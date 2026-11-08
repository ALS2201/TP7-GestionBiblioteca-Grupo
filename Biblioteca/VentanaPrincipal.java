package biblioteca;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Ventana principal del trabajo integrador.
 * Es la interfaz gráfica de la biblioteca: tiene un menú lateral para
 * cambiar de sección y una tabla en cada una para ver los datos.
 * Para ejecutarla se usa el main() que está al final.
 */
public class VentanaPrincipal extends JFrame {

    // Interfaz para guardar "lo que hace un botón". Permite que la acción
    // lance excepciones (por ejemplo LibroNoPrestadoException) y se manejen en un solo lugar.
    private interface Accion { void run() throws Exception; }

    // ---------- Colores y fuente que se usan en toda la ventana ----------
    private static final Color COLOR_MENU = new Color(0x3B2418);        // madera oscura (nogal) del menú lateral
    private static final Color COLOR_MENU_ACTIVO = new Color(0x6B4226); // opción seleccionada (cuero)
    private static final Color COLOR_MENU_HOVER = new Color(0x4F3322);  // opción bajo el mouse
    private static final Color COLOR_PRINCIPAL = new Color(0x7B2D26);   // bordó de las tapas de cuero (botones principales)
    private static final Color COLOR_FONDO = new Color(0xF5EEDC);       // color pergamino de las páginas
    private static final Color PAPEL = new Color(0xFFFDF7);             // papel de las tablas y tarjetas
    private static final Color PAPEL_ALT = new Color(0xFAF4E4);         // filas alternadas de las tablas
    private static final Color BORDE = new Color(0xD9C7A0);             // bordes color papel viejo
    private static final Color ENCABEZADO = new Color(0xE8D9B8);        // encabezado de las tablas
    private static final Color SELECCION = new Color(0xEFDDB4);         // fila seleccionada
    private static final Color MARRON_TEXTO = new Color(0x5A3A22);      // texto de encabezados y botones claros
    private static final Color DORADO = new Color(0xC9A66B);            // detalles dorados
    private static final Color COLOR_TEXTO = new Color(0x2B1D14);
    private static final Color COLOR_TEXTO_SUAVE = new Color(0x7A6552);
    private static final Color VERDE = new Color(0x2F6B3A);             // verde de lámpara de biblioteca
    private static final Color ROJO = new Color(0xA4262C);
    private static final Color AMARILLO = new Color(0xB7791F);
    private static final String FUENTE = "Segoe UI";
    private static final String FUENTE_TITULO = "Georgia";               // letra con serif, como en los libros

    // ---------- Datos ----------
    // Archivo donde se guarda todo lo cargado en la interfaz (en la carpeta del usuario, para que sea siempre el mismo)
    private static final java.io.File ARCHIVO = new java.io.File(System.getProperty("user.home"), "biblioteca.dat");
    // La biblioteca (clase del modelo): se carga del archivo o se crea con datos de ejemplo
    private Biblioteca biblioteca;
    // Para mostrar las fechas como dd/MM/aaaa
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    // ---------- Modelos de las tablas (guardan las filas que se muestran) ----------
    private final DefaultTableModel modeloLibros = crearModelo("Título", "Edición", "Editorial", "Año", "Estado");
    private final DefaultTableModel modeloSocios = crearModelo("DNI", "Nombre", "Tipo", "Área / Carrera", "Días de préstamo", "Libros prestados");
    private final DefaultTableModel modeloPrestamos = crearModelo("Libro", "Socio", "Retiro", "Devolución", "Estado");
    private final DefaultTableModel modeloResponsables = crearModelo("DNI", "Nombre", "Área", "Días de préstamo");
    private final DefaultTableModel modeloVencidos = crearModelo("Libro", "Socio", "Retiro", "Devolución");

    // ---------- Tablas (el número es la columna "Estado" que va con color, -1 si no tiene) ----------
    private final JTable tablaLibros = crearTabla(modeloLibros, 4);
    private final JTable tablaSocios = crearTabla(modeloSocios, -1);
    private final JTable tablaPrestamos = crearTabla(modeloPrestamos, 4);
    private final JTable tablaResponsables = crearTabla(modeloResponsables, -1);
    private final JTable tablaVencidos = crearTabla(modeloVencidos, -1);

    // ---------- Otros componentes que hay que poder modificar después ----------
    private final JCheckBox chkSoloVencidos = new JCheckBox("Solo vencidos");
    private final JLabel[] lblNumeros = {new JLabel(), new JLabel(), new JLabel(), new JLabel()}; // números de las 4 tarjetas
    private final JLabel lblMensaje = new JLabel(" ");   // mensaje de confirmación que aparece abajo
    private final CardLayout paginas = new CardLayout(); // permite mostrar una página a la vez
    private final JPanel panelPaginas = new JPanel(paginas);
    private final java.util.List<BotonMenu> botonesMenu = new ArrayList<>();

    /**
     * Constructor: arma toda la ventana paso a paso.
     */
    public VentanaPrincipal() {
        super("Gestión de Biblioteca");

        // Paso 1: cargar lo guardado; si no hay archivo (o falla), usar los datos de ejemplo
        biblioteca = leerArchivo();
        if (biblioteca == null) {
            biblioteca = new Biblioteca("Biblioteca Central");
            cargarDatos();
        }

        // Paso 2: el panel raíz tiene el menú a la izquierda y el contenido en el centro
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(crearMenuLateral(), BorderLayout.WEST);

        // Paso 3: crear cada una de las páginas y agregarlas al CardLayout
        panelPaginas.setBackground(COLOR_FONDO);
        panelPaginas.add(crearPaginaInicio(), "Inicio");

        panelPaginas.add(crearPagina("Catálogo de libros", "Ejemplares de la biblioteca. Seleccioná uno para prestarlo, devolverlo o quitarlo.", tablaLibros,
            crearBoton("Agregar libro", true, this::agregarLibro),
            crearBoton("Prestar", false, this::prestarLibro),
            crearBoton("Devolver", false, this::devolverLibro),
            crearBoton("¿Quién lo tiene?", false, this::consultarQuienTiene),
            crearBoton("Quitar libro", false, this::quitarLibro)), "Libros");

        panelPaginas.add(crearPagina("Socios lectores", "Docentes y estudiantes registrados en la biblioteca.", tablaSocios,
            crearBoton("Nuevo docente", true, this::agregarDocente),
            crearBoton("Nuevo estudiante", false, this::agregarEstudiante)), "Socios");

        // El checkbox vuelve a cargar la tabla cada vez que se tilda o destilda
        chkSoloVencidos.setOpaque(false);
        chkSoloVencidos.setFont(new Font(FUENTE, Font.BOLD, 13));
        chkSoloVencidos.addActionListener(e -> actualizarDatos());
        panelPaginas.add(crearPagina("Registro de préstamos", "Fichas de todos los préstamos realizados.", tablaPrestamos, chkSoloVencidos), "Préstamos");

        panelPaginas.add(crearPagina("Docentes responsables", "Docentes sin préstamos vencidos. Podés cambiar sus días de préstamo.", tablaResponsables,
            crearBoton("Cambiar días de préstamo", true, this::cambiarDiasPrestamo)), "Docentes");

        // Paso 4: debajo de las páginas va la línea donde se muestran los mensajes
        lblMensaje.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblMensaje.setForeground(COLOR_TEXTO_SUAVE);
        lblMensaje.setBorder(new EmptyBorder(6, 24, 8, 24));
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBackground(COLOR_FONDO);
        derecha.add(panelPaginas, BorderLayout.CENTER);
        derecha.add(lblMensaje, BorderLayout.SOUTH);
        raiz.add(derecha, BorderLayout.CENTER);
        setContentPane(raiz);

        // Paso 5: llenar las tablas y mostrar la página de inicio
        actualizarDatos();
        irAPagina("Inicio");

        // Paso 6: configuración de la ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setSize(1100, 680);
        setLocationRelativeTo(null); // centrada en la pantalla
    }

    // =====================================================================
    //  DATOS INICIALES (son los mismos que usa GestionBiblioteca)
    // =====================================================================

    private void cargarDatos() {
        // Dos libros
        biblioteca.nuevoLibro("Programando con JAVA", 1, "Fuente", 2008);
        biblioteca.nuevoLibro("POO", 2, "TechBooks", 2009);

        // Un docente y dos estudiantes
        biblioteca.nuevoSocioDocente(1111, "Lucas", "Filosofia");
        biblioteca.nuevoSocioEstudiante(2222, "Mateo", "Biologia");
        biblioteca.nuevoSocioEstudiante(3333, "Fabricio", "Lic. En Sistemas");

        // Préstamo viejo (febrero de 2024) que NO se devuelve, así queda un préstamo vencido de ejemplo
        biblioteca.prestarLibro(new GregorianCalendar(2024, 1, 10), biblioteca.getSocios().get(0), biblioteca.getLibros().get(1));
    }

    // =====================================================================
    //  PERSISTENCIA (guardar y leer el archivo)
    // =====================================================================

    /** Lee la biblioteca guardada. Devuelve null si no hay archivo o no se pudo leer. */
    private Biblioteca leerArchivo() {
        if (!ARCHIVO.exists()) return null;
        // el FileInputStream va como recurso propio: si el encabezado es inválido hay que cerrarlo para poder renombrar en Windows
        try (java.io.FileInputStream fis = new java.io.FileInputStream(ARCHIVO);
             java.io.ObjectInputStream in = new java.io.ObjectInputStream(fis)) {
            return (Biblioteca) in.readObject();
        } catch (Exception e) {
            // Archivo dañado o de una versión vieja: se guarda como .bak para no pisarlo y se arranca de cero
            ARCHIVO.renameTo(new java.io.File(ARCHIVO.getPath() + ".bak"));
            return null;
        }
    }

    /**
     * Guarda toda la biblioteca (libros, socios y préstamos) en el archivo.
     * Se llama después de cada cambio hecho desde la interfaz, así nada se pierde al cerrar.
     * Se escribe primero en un archivo temporal y recién al terminar se lo renombra:
     * de esa forma, si el programa se cierra a mitad de la escritura, el archivo anterior queda intacto.
     */
    private void guardarArchivo() {
        java.io.File temporal = new java.io.File(ARCHIVO.getPath() + ".tmp");
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(temporal))) {
            out.writeObject(biblioteca);
        } catch (java.io.IOException e) {
            // Si no se puede guardar se avisa, pero el programa sigue funcionando
            mostrarError("No se pudieron guardar los datos: " + e.getMessage());
            return;
        }
        try {
            // El temporal ya está cerrado (try-with-resources): se reemplaza el archivo real de una sola vez
            java.nio.file.Files.move(temporal.toPath(), ARCHIVO.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (java.io.IOException e) {
            mostrarError("No se pudieron guardar los datos: " + e.getMessage());
        }
    }

    // =====================================================================
    //  ACCIONES DE LOS BOTONES
    // =====================================================================

    /** Pide los datos de un libro nuevo y lo agrega a la biblioteca. */
    private void agregarLibro() {
        String[] datos = pedirDatos("Agregar libro", "Título", "Edición", "Editorial", "Año");
        if (datos == null) return; // el usuario canceló o dejó campos vacíos

        // Edición y año son números: si no lo son, salta NumberFormatException y se avisa en crearBoton
        biblioteca.nuevoLibro(datos[0], Integer.parseInt(datos[1]), datos[2], Integer.parseInt(datos[3]));
        actualizarDatos();
        mostrarMensaje("Libro \"" + datos[0] + "\" agregado.");
    }

    /** Quita del catálogo el libro seleccionado (pide confirmación; no permite quitar uno prestado). */
    private void quitarLibro() {
        Libro libro = libroSeleccionado();
        if (libro == null) return;

        if (libro.prestado()) {
            mostrarError("No se puede quitar \"" + libro.getTitulo() + "\" porque está prestado. Primero hay que devolverlo.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Seguro que querés quitar \"" + libro.getTitulo() + "\" del catálogo?",
                "Quitar libro", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) return;

        if (biblioteca.quitarLibro(libro)) {
            actualizarDatos();
            mostrarMensaje("Libro \"" + libro.getTitulo() + "\" quitado.");
        }
    }

    /** Pide los datos de un docente nuevo y lo agrega. */
    private void agregarDocente() {
        String[] datos = pedirDatos("Nuevo docente", "DNI", "Nombre", "Área");
        if (datos == null) return;

        biblioteca.nuevoSocioDocente(Integer.parseInt(datos[0]), datos[1], datos[2]);
        actualizarDatos();
        mostrarMensaje("Docente " + datos[1] + " agregado.");
    }

    /** Pide los datos de un estudiante nuevo y lo agrega. */
    private void agregarEstudiante() {
        String[] datos = pedirDatos("Nuevo estudiante", "DNI", "Nombre", "Carrera");
        if (datos == null) return;

        biblioteca.nuevoSocioEstudiante(Integer.parseInt(datos[0]), datos[1], datos[2]);
        actualizarDatos();
        mostrarMensaje("Estudiante " + datos[1] + " agregado.");
    }

    /** Presta el libro seleccionado en la tabla al socio que se elija. */
    private void prestarLibro() {
        // 1) Ver qué libro está seleccionado
        Libro libro = libroSeleccionado();
        if (libro == null) return;
        if (biblioteca.getSocios().isEmpty()) {
            mostrarError("No hay socios cargados.");
            return;
        }

        // 2) Armar un desplegable con todos los socios
        JComboBox<String> comboSocios = new JComboBox<>();
        for (Socio s : biblioteca.getSocios()) {
            comboSocios.addItem(s.getNombre() + "  -  " + s.soyDeLaClase());
        }
        int respuesta = JOptionPane.showConfirmDialog(this, comboSocios, "Prestar \"" + libro.getTitulo() + "\" a:",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) return;

        // 3) Intentar el préstamo con la fecha de hoy
        Socio socio = biblioteca.getSocios().get(comboSocios.getSelectedIndex());
        if (biblioteca.prestarLibro(Calendar.getInstance(), socio, libro)) {
            actualizarDatos();
            mostrarMensaje("Préstamo de \"" + libro.getTitulo() + "\" a " + socio.getNombre() + " registrado.");
        } else {
            mostrarError("No se pudo prestar: el libro ya está prestado o el socio alcanzó su límite.");
        }
    }

    /** Registra la devolución del libro seleccionado. */
    private void devolverLibro() throws LibroNoPrestadoException {
        Libro libro = libroSeleccionado();
        if (libro == null) return;

        // Si el libro no estaba prestado lanza la excepción, que se muestra en crearBoton
        biblioteca.devolverLibro(libro);
        actualizarDatos();
        mostrarMensaje("\"" + libro.getTitulo() + "\" devuelto.");
    }

    /** Muestra qué socio tiene en su poder el libro seleccionado. */
    private void consultarQuienTiene() throws LibroNoPrestadoException {
        Libro libro = libroSeleccionado();
        if (libro == null) return;

        String socio = biblioteca.quienTieneElLibro(libro);
        JOptionPane.showMessageDialog(this, "En poder de:\n" + socio, "Consulta", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Cambia los días de préstamo del docente seleccionado en la tabla de responsables. */
    private void cambiarDiasPrestamo() {
        int fila = tablaResponsables.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccioná un docente de la tabla.");
            return;
        }
        String texto = JOptionPane.showInputDialog(this, "Nuevos días de préstamo:", "Cambiar días", JOptionPane.PLAIN_MESSAGE);
        if (texto == null) return; // canceló

        int dias = Integer.parseInt(texto.trim());
        if (dias <= 0) {
            mostrarError("Los días deben ser mayores a 0.");
            return;
        }
        // La fila de la tabla coincide con la posición en la lista de docentes responsables
        Docente docente = (Docente) biblioteca.docentesResponsables().get(fila);
        docente.cambiarDiasDePrestamos(dias);
        actualizarDatos();
        mostrarMensaje("Días de préstamo actualizados a " + dias + ".");
    }

    // =====================================================================
    //  ACTUALIZAR LOS DATOS QUE SE VEN EN PANTALLA
    // =====================================================================

    /** Vuelve a llenar todas las tablas y los números del tablero con los datos actuales. */
    private void actualizarDatos() {
        // Cada vez que cambia algo se refresca la pantalla, así que también se guarda en el archivo
        guardarArchivo();

        // --- Libros ---
        modeloLibros.setRowCount(0); // se borran las filas viejas
        int cantPrestados = 0;
        for (Libro libro : biblioteca.getLibros()) {
            if (libro.prestado()) cantPrestados++;
            String estado = libro.prestado() ? "Prestado" : "Disponible";
            modeloLibros.addRow(new Object[]{libro.getTitulo(), libro.getEdicion(), libro.getEditorial(), libro.getAnio(), estado});
        }

        // --- Socios ---
        modeloSocios.setRowCount(0);
        for (Socio socio : biblioteca.getSocios()) {
            // Los docentes tienen área y los estudiantes carrera
            String areaOCarrera;
            if (socio instanceof Docente) {
                areaOCarrera = ((Docente) socio).getArea();
            } else {
                areaOCarrera = ((Estudiante) socio).getCarrera();
            }
            modeloSocios.addRow(new Object[]{socio.getDniSocio(), socio.getNombre(), socio.soyDeLaClase(),
                areaOCarrera, socio.getDiasPrestamo(), socio.cantLibrosPrestados()});
        }

        // --- Préstamos (todos) y préstamos vencidos (para el tablero) ---
        modeloPrestamos.setRowCount(0);
        modeloVencidos.setRowCount(0);
        ArrayList<Prestamo> vencidos = biblioteca.prestamosVencidos();
        for (Libro libro : biblioteca.getLibros()) {
            for (Prestamo prestamo : libro.getPrestamos()) {
                boolean estaVencido = vencidos.contains(prestamo);
                Calendar devolucion = prestamo.getFechaDevolucion();

                String fechaRetiro = formatoFecha.format(prestamo.getFechaRetiro().getTime());
                String fechaDevolucion = (devolucion == null) ? "-" : formatoFecha.format(devolucion.getTime());

                // Los vencidos van también a la tabla del tablero de inicio
                if (estaVencido) {
                    modeloVencidos.addRow(new Object[]{libro.getTitulo(), prestamo.getSocio().getNombre(), fechaRetiro, fechaDevolucion});
                }
                // Si está tildado "Solo vencidos" se saltean los que no lo son
                if (chkSoloVencidos.isSelected() && !estaVencido) continue;

                String estado;
                if (estaVencido) estado = "Vencido";
                else if (devolucion == null) estado = "En curso";
                else estado = "Devuelto";
                modeloPrestamos.addRow(new Object[]{libro.getTitulo(), prestamo.getSocio().getNombre(), fechaRetiro, fechaDevolucion, estado});
            }
        }

        // --- Docentes responsables ---
        modeloResponsables.setRowCount(0);
        for (Socio socio : biblioteca.docentesResponsables()) {
            modeloResponsables.addRow(new Object[]{socio.getDniSocio(), socio.getNombre(), ((Docente) socio).getArea(), socio.getDiasPrestamo()});
        }

        // --- Números de las tarjetas del tablero ---
        lblNumeros[0].setText("" + biblioteca.getLibros().size());
        lblNumeros[1].setText("" + (biblioteca.getLibros().size() - cantPrestados));
        lblNumeros[2].setText("" + biblioteca.getSocios().size());
        lblNumeros[3].setText("" + vencidos.size());
    }

    // =====================================================================
    //  ARMADO DE LAS PÁGINAS
    // =====================================================================

    /** Crea el menú de la izquierda con el logo y los botones de navegación. */
    private JPanel crearMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS)); // todo en vertical
        menu.setBackground(COLOR_MENU);
        menu.setBorder(new EmptyBorder(24, 14, 16, 14));
        menu.setPreferredSize(new Dimension(230, 0));

        // Logo: un cuadrado redondeado con una "B" dibujada a mano, y el nombre al lado
        JLabel logo = new JLabel("Biblioteca") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = activarSuavizado(g);
                g2.setColor(COLOR_PRINCIPAL);
                g2.fillRoundRect(0, 4, 36, 36, 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(FUENTE_TITULO, Font.BOLD, 22));
                g2.drawString("B", 11, 31);
                g2.dispose();
                super.paintComponent(g); // dibuja el texto "Biblioteca"
            }
        };
        logo.setFont(new Font(FUENTE_TITULO, Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(0, 48, 0, 0)); // deja lugar para el cuadrado
        logo.setPreferredSize(new Dimension(200, 44));
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        logo.setAlignmentX(LEFT_ALIGNMENT);
        menu.add(logo);

        JLabel subtitulo = new JLabel("Catálogo · Préstamos · Socios");
        subtitulo.setFont(new Font(FUENTE, Font.PLAIN, 12));
        subtitulo.setForeground(new Color(0xD9B98A));
        subtitulo.setBorder(new EmptyBorder(0, 48, 0, 0));
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);
        menu.add(subtitulo);
        menu.add(Box.createVerticalStrut(28));

        // Un botón por cada página: {nombre de la página, texto del botón}
        String[][] opciones = {{"Inicio", "Inicio"}, {"Libros", "Catálogo de libros"}, {"Socios", "Socios"},
                               {"Préstamos", "Préstamos"}, {"Docentes", "Docentes responsables"}};
        for (String[] opcion : opciones) {
            BotonMenu boton = new BotonMenu(opcion[1], opcion[0]);
            botonesMenu.add(boton);
            menu.add(boton);
            menu.add(Box.createVerticalStrut(6));
        }

        // El espacio flexible empuja el botón Salir hacia abajo de todo
        menu.add(Box.createVerticalGlue());
        menu.add(crearEstanteria());
        menu.add(Box.createVerticalStrut(14));
        BotonMenu salir = new BotonMenu("Salir", null);
        salir.addActionListener(e -> System.exit(0));
        menu.add(salir);
        return menu;
    }

    /** Dibuja una pequeña estantería con libros de colores para decorar el menú. */
    private JPanel crearEstanteria() {
        JPanel estanteria = new JPanel() {
            // {ancho, alto} de cada lomo y su color
            private final int[][] medidas = {{16, 54}, {12, 64}, {18, 48}, {14, 60}, {20, 52}, {12, 68}, {16, 56}, {14, 46}};
            private final Color[] colores = {new Color(0x7B2D26), new Color(0x2F6B3A), new Color(0xB7791F), new Color(0x2F5D8A),
                                             new Color(0x5B3A6B), new Color(0x8C4A2F), new Color(0x3F6F6B), new Color(0xA4262C)};

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = activarSuavizado(g);
                int tabla = getHeight() - 10; // y donde apoyan los libros
                int x = 8;
                for (int i = 0; i < medidas.length; i++) {
                    int ancho = medidas[i][0], alto = medidas[i][1];
                    g2.setColor(colores[i]);
                    g2.fillRoundRect(x, tabla - alto, ancho, alto, 4, 4);
                    g2.setColor(new Color(255, 255, 255, 70)); // franja dorada del lomo
                    g2.fillRect(x, tabla - alto + 8, ancho, 2);
                    g2.fillRect(x, tabla - 12, ancho, 2);
                    x += ancho + 2;
                }
                g2.setColor(new Color(0xA9743F)); // tabla de madera
                g2.fillRoundRect(0, tabla, getWidth(), 8, 6, 6);
                g2.dispose();
            }
        };
        estanteria.setOpaque(false);
        estanteria.setPreferredSize(new Dimension(200, 84));
        estanteria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        estanteria.setAlignmentX(LEFT_ALIGNMENT);
        return estanteria;
    }

    /** Muestra la página pedida y marca como activo el botón del menú correspondiente. */
    private void irAPagina(String nombre) {
        paginas.show(panelPaginas, nombre);
        for (BotonMenu boton : botonesMenu) {
            boton.activo = boton.pagina.equals(nombre);
            boton.repaint();
        }
    }

    /** Crea la página de inicio: cuatro tarjetas con números y la lista de vencidos. */
    private JPanel crearPaginaInicio() {
        JPanel pagina = new JPanel(new BorderLayout(0, 20));
        pagina.setBackground(COLOR_FONDO);
        pagina.setBorder(new EmptyBorder(24, 24, 8, 24));
        pagina.add(conFiligrana(crearTitulo("Mostrador", "Resumen de la " + biblioteca.getNombre() + ".")), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 20));
        centro.setOpaque(false);

        // Fila con las 4 tarjetas (1 fila, 4 columnas)
        JPanel tarjetas = new JPanel(new GridLayout(1, 4, 16, 0));
        tarjetas.setOpaque(false);
        String[] nombres = {"Ejemplares en total", "En estantería", "Socios lectores", "Préstamos vencidos"};
        Color[] colores = {COLOR_PRINCIPAL, VERDE, new Color(0x2F5D8A), ROJO};
        for (int i = 0; i < 4; i++) {
            tarjetas.add(crearTarjeta(nombres[i], lblNumeros[i], colores[i]));
        }
        tarjetas.setPreferredSize(new Dimension(0, 110));
        centro.add(tarjetas, BorderLayout.NORTH);

        // Debajo de las tarjetas, la tabla de vencidos con su título
        JLabel tituloLista = new JLabel("Préstamos vencidos");
        tituloLista.setFont(new Font(FUENTE_TITULO, Font.BOLD, 17));
        tituloLista.setForeground(COLOR_TEXTO);
        tituloLista.setBorder(new EmptyBorder(0, 2, 8, 0));
        JPanel lista = new JPanel(new BorderLayout());
        lista.setOpaque(false);
        lista.add(tituloLista, BorderLayout.NORTH);
        lista.add(envolverTabla(tablaVencidos), BorderLayout.CENTER);
        centro.add(lista, BorderLayout.CENTER);

        pagina.add(centro, BorderLayout.CENTER);
        return pagina;
    }

    /** Crea una página común: título arriba, botones a la derecha y la tabla abajo. */
    private JPanel crearPagina(String titulo, String subtitulo, JTable tabla, Component... acciones) {
        JPanel pagina = new JPanel(new BorderLayout(0, 16));
        pagina.setBackground(COLOR_FONDO);
        pagina.setBorder(new EmptyBorder(24, 24, 8, 24));

        // Parte de arriba: título a la izquierda y botones a la derecha
        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(crearTitulo(titulo, subtitulo), BorderLayout.CENTER);
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        panelBotones.setOpaque(false);
        for (Component c : acciones) {
            panelBotones.add(c);
        }
        arriba.add(panelBotones, BorderLayout.EAST);

        pagina.add(conFiligrana(arriba), BorderLayout.NORTH);
        pagina.add(envolverTabla(tabla), BorderLayout.CENTER);
        return pagina;
    }

    /** Agrega debajo del componente una doble línea dorada, como el adorno de un libro antiguo. */
    private JPanel conFiligrana(JComponent contenido) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.add(contenido, BorderLayout.CENTER);
        JPanel linea = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(DORADO);
                g.fillRect(0, 0, getWidth(), 2);
                g.setColor(new Color(0xE0C898));
                g.fillRect(0, 5, getWidth(), 1);
            }
        };
        linea.setOpaque(false);
        linea.setPreferredSize(new Dimension(0, 7));
        panel.add(linea, BorderLayout.SOUTH);
        return panel;
    }

    /** Crea un título grande con una línea de descripción debajo. */
    private JPanel crearTitulo(String titulo, String subtitulo) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setOpaque(false);
        JLabel lblTitulo = new JLabel(titulo);
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblTitulo.setFont(new Font(FUENTE_TITULO, Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblSubtitulo.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblSubtitulo.setForeground(COLOR_TEXTO_SUAVE);
        panel.add(lblTitulo);
        panel.add(lblSubtitulo);
        return panel;
    }

    /** Pone la tabla dentro de un scroll y de un recuadro blanco con esquinas redondeadas. */
    private JComponent envolverTabla(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(null);
        scroll.getViewport().setBackground(PAPEL);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel recuadro = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = activarSuavizado(g);
                g2.setColor(BORDE); // borde color papel viejo
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(PAPEL);          // relleno de papel un poco más chico
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 17, 17);
                g2.dispose();
            }
        };
        recuadro.setOpaque(false);
        recuadro.setBorder(new EmptyBorder(6, 6, 6, 6));
        recuadro.add(scroll);
        return recuadro;
    }

    /** Crea una tarjeta del tablero: número grande arriba y descripción abajo. */
    private JPanel crearTarjeta(String nombre, JLabel numero, Color color) {
        JPanel tarjeta = new JPanel(new GridLayout(2, 1)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = activarSuavizado(g);
                g2.setColor(PAPEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.setColor(color); // barrita de color a la izquierda
                g2.fillRoundRect(0, 18, 6, getHeight() - 36, 6, 6);
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(18, 24, 14, 12));

        numero.setFont(new Font(FUENTE_TITULO, Font.BOLD, 38));
        numero.setForeground(color);
        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblNombre.setForeground(COLOR_TEXTO_SUAVE);

        tarjeta.add(numero);
        tarjeta.add(lblNombre);
        return tarjeta;
    }

    // =====================================================================
    //  TABLAS
    // =====================================================================

    /** Crea un modelo de tabla con esas columnas y sin permitir editar las celdas. */
    private static DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    /**
     * Crea una tabla con el estilo de la aplicación.
     * @param modelo el modelo con los datos
     * @param columnaEstado número de la columna que se muestra como etiqueta de color (-1 si no hay)
     */
    private static JTable crearTabla(DefaultTableModel modelo, int columnaEstado) {
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // una fila a la vez
        tabla.setRowHeight(38);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);
        tabla.setFont(new Font(FUENTE, Font.PLAIN, 14));
        tabla.setForeground(COLOR_TEXTO);
        tabla.setBackground(PAPEL);
        tabla.setSelectionBackground(SELECCION);
        tabla.setSelectionForeground(COLOR_TEXTO);

        // Dibujo de las celdas comunes: texto alineado a la izquierda y filas alternadas
        DefaultTableCellRenderer celdaComun = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionada, boolean foco, int fila, int col) {
                super.getTableCellRendererComponent(t, valor, seleccionada, false, fila, col);
                setBorder(new EmptyBorder(0, 14, 0, 8));
                setHorizontalAlignment(LEFT);
                if (seleccionada) setBackground(t.getSelectionBackground());
                else if (fila % 2 == 0) setBackground(PAPEL);
                else setBackground(PAPEL_ALT);
                return this;
            }
        };
        tabla.setDefaultRenderer(Object.class, celdaComun);
        tabla.setDefaultRenderer(Number.class, celdaComun);
        tabla.setDefaultRenderer(Integer.class, celdaComun);

        // La columna de estado usa el dibujo especial con colores
        if (columnaEstado >= 0) {
            tabla.getColumnModel().getColumn(columnaEstado).setCellRenderer(new CeldaEstado());
        }

        // Encabezado de la tabla
        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setReorderingAllowed(false);
        encabezado.setPreferredSize(new Dimension(0, 40));
        encabezado.setDefaultRenderer((t, valor, seleccionada, foco, fila, col) -> {
            JLabel etiqueta = new JLabel(String.valueOf(valor));
            etiqueta.setOpaque(true);
            etiqueta.setBackground(ENCABEZADO);
            etiqueta.setForeground(MARRON_TEXTO);
            etiqueta.setFont(new Font(FUENTE_TITULO, Font.BOLD, 13));
            etiqueta.setBorder(new EmptyBorder(0, 14, 0, 8));
            return etiqueta;
        });
        return tabla;
    }

    /**
     * Dibuja el estado de un libro o préstamo como una etiqueta redondeada de color:
     * verde (Disponible / Devuelto), rojo (Vencido) o amarillo (Prestado / En curso).
     */
    private static class CeldaEstado extends JLabel implements TableCellRenderer {
        private Color colorFondoFila = PAPEL;
        private Color colorEtiqueta = VERDE;

        CeldaEstado() {
            setFont(new Font(FUENTE, Font.BOLD, 12));
        }

        public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionada, boolean foco, int fila, int col) {
            String texto = String.valueOf(valor);
            setText(texto);

            // Elegir el color según el texto
            if (texto.equals("Disponible") || texto.equals("Devuelto")) colorEtiqueta = VERDE;
            else if (texto.equals("Vencido")) colorEtiqueta = ROJO;
            else colorEtiqueta = AMARILLO;

            // El fondo de la fila tiene que coincidir con el de las otras celdas
            if (seleccionada) colorFondoFila = t.getSelectionBackground();
            else if (fila % 2 == 0) colorFondoFila = PAPEL;
            else colorFondoFila = PAPEL_ALT;
            return this;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = activarSuavizado(g);
            // 1) fondo de la fila
            g2.setColor(colorFondoFila);
            g2.fillRect(0, 0, getWidth(), getHeight());
            // 2) etiqueta redondeada con el color un poco transparente
            int ancho = 92, alto = 24, y = (getHeight() - alto) / 2;
            g2.setColor(new Color(colorEtiqueta.getRed(), colorEtiqueta.getGreen(), colorEtiqueta.getBlue(), 32));
            g2.fillRoundRect(10, y, ancho, alto, alto, alto);
            // 3) texto centrado dentro de la etiqueta
            g2.setColor(colorEtiqueta);
            g2.setFont(getFont());
            FontMetrics medidas = g2.getFontMetrics();
            int x = 10 + (ancho - medidas.stringWidth(getText())) / 2;
            int yTexto = y + (alto + medidas.getAscent() - medidas.getDescent()) / 2;
            g2.drawString(getText(), x, yTexto);
            g2.dispose();
        }
    }

    // =====================================================================
    //  BOTONES
    // =====================================================================

    /** Devuelve un Graphics2D con los bordes y el texto suavizados (se ve mejor). */
    private static Graphics2D activarSuavizado(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    /**
     * Crea un botón redondeado que ejecuta una acción.
     * @param principal true = botón violeta (acción principal), false = botón blanco con borde
     * @param accion lo que hace al hacer clic
     */
    private JButton crearBoton(String texto, boolean principal, Accion accion) {
        JButton boton = new JButton(texto) {
            private boolean encima = false; // true cuando el mouse está sobre el botón

            { // este bloque se ejecuta al crear el botón: detecta el mouse
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
                    public void mouseExited(MouseEvent e) { encima = false; repaint(); }
                });
            }

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = activarSuavizado(g);
                // Color de fondo según el tipo de botón y si el mouse está encima
                Color fondo = principal ? COLOR_PRINCIPAL : PAPEL;
                if (encima) fondo = principal ? COLOR_PRINCIPAL.darker() : new Color(0xF3E7CC);
                if (getModel().isPressed()) fondo = fondo.darker();
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                // Los botones blancos llevan un borde suave
                if (!principal) {
                    g2.setColor(new Color(0xD2B98C));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g); // dibuja el texto
            }
        };
        // Se desactiva el dibujo por defecto porque lo hacemos nosotros arriba
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setFont(new Font(FUENTE, Font.BOLD, 13));
        boton.setForeground(principal ? Color.WHITE : MARRON_TEXTO);
        boton.setBorder(new EmptyBorder(9, 18, 9, 18));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Al hacer clic se ejecuta la acción y se capturan los errores posibles
        boton.addActionListener(e -> {
            try {
                accion.run();
            } catch (NumberFormatException ex) {
                mostrarError("Ingresá un número válido.");
            } catch (IllegalArgumentException ex) { // DNI repetido (va después de NumberFormatException, su subclase)
                mostrarError(ex.getMessage());
            } catch (LibroNoPrestadoException ex) {
                mostrarError(ex.getMessage());
            } catch (Exception ex) {
                mostrarError("Error inesperado: " + ex);
            }
        });
        return boton;
    }

    /** Botón del menú lateral. Si pagina es null no cambia de página (se usa para Salir). */
    private class BotonMenu extends JButton {
        final String pagina;   // nombre de la página a la que lleva
        boolean activo = false; // true si es la página que se está viendo
        boolean encima = false; // true si el mouse está sobre el botón

        BotonMenu(String texto, String pagina) {
            super(texto);
            this.pagina = pagina;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setHorizontalAlignment(LEFT);
            setFont(new Font(FUENTE_TITULO, Font.BOLD, 14));
            setForeground(new Color(0xF3E6CF));
            setBorder(new EmptyBorder(11, 18, 11, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            if (pagina != null) {
                addActionListener(e -> irAPagina(pagina));
            }
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
                public void mouseExited(MouseEvent e) { encima = false; repaint(); }
            });
        }

        protected void paintComponent(Graphics g) {
            // Solo se pinta el fondo si está activo o el mouse está encima
            if (activo || encima) {
                Graphics2D g2 = activarSuavizado(g);
                g2.setColor(activo ? COLOR_MENU_ACTIVO : COLOR_MENU_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    // =====================================================================
    //  DIÁLOGOS Y MENSAJES
    // =====================================================================

    /**
     * Muestra un formulario con un campo de texto por cada etiqueta recibida.
     * @return los textos ingresados, o null si se canceló o quedó algún campo vacío
     */
    private String[] pedirDatos(String titulo, String... etiquetas) {
        // Dos columnas: etiqueta a la izquierda, campo a la derecha
        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField[] campos = new JTextField[etiquetas.length];
        for (int i = 0; i < etiquetas.length; i++) {
            formulario.add(new JLabel(etiquetas[i] + ":"));
            campos[i] = new JTextField(16);
            formulario.add(campos[i]);
        }
        formulario.setBorder(new EmptyBorder(6, 6, 6, 6));

        int respuesta = JOptionPane.showConfirmDialog(this, formulario, titulo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) return null;

        // Leer los campos y verificar que ninguno esté vacío
        String[] valores = new String[etiquetas.length];
        for (int i = 0; i < valores.length; i++) {
            valores[i] = campos[i].getText().trim();
            if (valores[i].isEmpty()) {
                mostrarError("Completá todos los campos.");
                return null;
            }
        }
        return valores;
    }

    /** Devuelve el libro seleccionado en la tabla de libros, o null (con aviso) si no hay ninguno. */
    private Libro libroSeleccionado() {
        int fila = tablaLibros.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccioná un libro de la tabla.");
            return null;
        }
        // La fila de la tabla es la misma posición que en la lista de libros
        return biblioteca.getLibros().get(fila);
    }

    /** Muestra un mensaje de confirmación en verde abajo de la ventana. */
    private void mostrarMensaje(String mensaje) {
        lblMensaje.setForeground(VERDE);
        lblMensaje.setText("✔ " + mensaje);
    }

    /** Muestra una ventana de aviso con el error. */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    // =====================================================================
    //  PROGRAMA PRINCIPAL
    // =====================================================================

    public static void main(String[] args) {
        // Usar el aspecto del sistema operativo para los cuadros de diálogo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // si falla se queda con el aspecto por defecto de Java
        }
        // Misma fuente en los diálogos
        String[] elementos = {"OptionPane.messageFont", "OptionPane.buttonFont", "Label.font", "TextField.font", "ComboBox.font"};
        for (String elemento : elementos) {
            UIManager.put(elemento, new Font(FUENTE, Font.PLAIN, 14));
        }
        // Las ventanas de Swing se crean en el hilo de eventos
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}