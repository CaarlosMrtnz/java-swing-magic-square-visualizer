import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Aplicación de escritorio desarrollada en Java Swing para la generación de Cuadrados Mágicos.
 * Utiliza un algoritmo de búsqueda aleatoria para encontrar matrices donde
 * la suma de filas, columnas y diagonales sea idéntica.
 *
 * Incluye gestión de hilos para evitar bloqueos en la interfaz durante el cálculo.
 */
public class Main extends JFrame {

    // Componentes de la Interfaz Gráfica (GUI)

    // Campo de texto para que el usuario introduzca el orden de la matriz (n).
    private final JTextField txtN;

    // Botón principal que ejecute la lógica de búsqueda.
    private final JButton btnGenerar;

    // Panel central donde se dibujará la cuadrícula de números.
    private final JPanel panelCuadricula;

    // Etiquetas informativas para mostrar el estado del proceso y resultados finales.
    private final JLabel lblEstado;
    private final JLabel lblResultados;

    /**
     * Constructor principal de la aplicación.
     * Configura las propiedades de la ventana, inicializa los componentes
     * y define la distribución (Layout) de los elementos.
     */
    public Main() {
        // 1. Configuración básica de la ventana (JFrame)
        setTitle("Generador de Cuadrados Mágicos");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en el monitor
        setLayout(new BorderLayout(10, 10)); // Márgenes de 10px entre componentes
        getContentPane().setBackground(new Color(0xE8EEFF));

        // 2. Panel superior: Controles de entrada
        JPanel panelTop = new JPanel();
        panelTop.add(new JLabel("Tamaño (n):"));
        panelTop.setBackground(new Color(0xE8EEFF));
        txtN = new JTextField("3", 5); // Valor por defecto: 3
        btnGenerar = new JButton("¡Buscar Cuadrado!");
        btnGenerar.setBackground(new Color(0x5C6BC0));
        btnGenerar.setForeground(Color.WHITE);

        panelTop.add(txtN);
        panelTop.add(btnGenerar);
        add(panelTop, BorderLayout.NORTH);

        // 3. Panel central: Visualización de la matriz
        panelCuadricula = new JPanel();
        panelCuadricula.setBackground(new Color(0xE8EEFF));
        // Se añade un borde vacío para dar aire a la cuadrícula respecto a los bordes
        panelCuadricula.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(panelCuadricula, BorderLayout.CENTER);

        // 4. Panel inferior: Barra de estado y estadísticas
        JPanel panelInfo = new JPanel(new GridLayout(2, 1));
        panelInfo.setBackground(new Color(0xE8EEFF));
        lblEstado = new JLabel("Listo para buscar...", SwingConstants.CENTER);
        lblResultados = new JLabel("", SwingConstants.CENTER);
        panelInfo.add(lblEstado);
        panelInfo.add(lblResultados);
        add(panelInfo, BorderLayout.SOUTH);

        // 5. Asignación de eventos (Listeners)
        // Expresión lambda para manejar el clic del botón
        btnGenerar.addActionListener(e -> iniciarBusqueda());
    }

    /**
     * Valida la entrada del usuario e inicia el proceso de búsqueda.
     * Gestiona la creación de un hilo secundario para no congelar la interfaz.
     */
    private void iniciarBusqueda() {
        try {
            int n = Integer.parseInt(txtN.getText());

            // Validación básica de reglas de negocio
            if (n < 3) {
                JLabel mensaje = new JLabel("El tamaño debe ser al menos 3");
                mensaje.setForeground(Color.RED);

                JOptionPane.showMessageDialog(
                        this,
                        mensaje,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            // Bloqueamos el botón para evitar múltiples ejecuciones simultáneas
            btnGenerar.setEnabled(false);
            lblEstado.setText("Buscando... (Esto puede tardar)");

            /*
             * Ejecutamos la búsqueda en un Hilo (Thread) separado.
             * Si se hiciera en el hilo principal, la ventana dejaría de responder
             * hasta encontrar la solución.
             */
            new Thread(() -> buscarCuadradoMagico(n)).start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce un número entero válido.");
        }
    }

    /**
     * Parte lógica del programa. Genera matrices aleatorias
     * hasta encontrar una que cumpla las condiciones de un cuadrado mágico.
     *
     * @param n El tamaño (orden) de la matriz a generar.
     */
    private void buscarCuadradoMagico(int n) {
        int intentos = 0;
        int[][] matriz;
        boolean encontrado = false;

        // Bucle que genera, valida y repite hasta obtener una matriz válida.
        do {
            intentos++;
            matriz = generarMatriz(n);
            if (esMagico(matriz)) {
                encontrado = true;
            }
        } while (!encontrado);

        // Variables finales para usar dentro del lambda de Swing
        int[][] resultadoFinal = matriz;
        int intentosFinales = intentos;

        /*
         * Una vez encontrado, volvemos al hilo de la interfaz gráfica
         * para actualizar los componentes visuales de forma segura.
         */
        SwingUtilities.invokeLater(() -> {
            dibujarMatriz(resultadoFinal, n);
            calcularYMostrarStats(resultadoFinal, intentosFinales);
            btnGenerar.setEnabled(true); // Reactivamos el botón
        });
    }

    /**
     * Renderiza la matriz numérica en el panel central creando una cuadrícula de etiquetas.
     *
     * @param matriz La matriz de datos a visualizar.
     * @param n      El tamaño de la cuadrícula.
     */
    private void dibujarMatriz(int[][] matriz, int n) {
        panelCuadricula.removeAll(); // Limpiamos resultados anteriores

        // GridLayout organiza los componentes en una tabla exacta de n x n
        panelCuadricula.setLayout(new GridLayout(n, n, 5, 5));

        // Creación dinámica de celdas visuales
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                JLabel celda = new JLabel(String.valueOf(matriz[i][j]), SwingConstants.CENTER);
                // Estilos visuales para mejorar la legibilidad
                celda.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                celda.setFont(new Font("Arial", Font.BOLD, 20));
                celda.setOpaque(true);
                celda.setBackground(new Color(0xE1E6FA)); // Azul claro
                celda.setForeground(new Color(0x2B2F77));
                panelCuadricula.add(celda);
            }
        }
        // Forzamos el repintado del panel para mostrar los nuevos cambios
        panelCuadricula.revalidate();
        panelCuadricula.repaint();
    }

    /**
     * Calcula la constante mágica y actualiza las etiquetas de estado.
     *
     * @param matriz   La matriz ganadora.
     * @param intentos Número total de iteraciones realizadas.
     */
    private void calcularYMostrarStats(int[][] matriz, int intentos) {
        int constante = 0;
        // Calculamos la constante sumando la primera fila
        for (int j = 0; j < matriz.length; j++) constante += matriz[0][j];

        lblEstado.setText("=== CUADRADO MÁGICO ENCONTRADO ===");
        lblEstado.setForeground(new Color(0x2E7D32));
        lblResultados.setText("Constante Mágica: " + constante + " | Intentos realizados: " + intentos);
        lblResultados.setForeground(new Color(0x2E7D32));
    }

    // --- Métodos lógicos ---

    /**
     * Genera una matriz de n x n rellena con números aleatorios únicos.
     * Crea una lista del 1 a n^2, la baraja y rellena la matriz.
     *
     * @param n Tamaño de la matriz.
     * @return Matriz rellena con números aleatorios sin repetición.
     */
    public static int[][] generarMatriz(int n) {
        int[][] matriz = new int[n][n];
        int numeros = n * n;

        // 1. Crea lista ordenada
        ArrayList<Integer> lista = new ArrayList<>();
        for (int i = 1; i <= numeros; i++) lista.add(i);

        // 2. Baraja la lista (Shuffle)
        Collections.shuffle(lista);

        // 3. Vuelca la lista a la matriz bidimensional
        int contador = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matriz[i][j] = lista.get(contador++);
            }
        }
        return matriz;
    }

    /**
     * Verifica si una matriz cumple todas las condiciones de un cuadrado mágico.
     * Condiciones: Suma de filas = Suma de columnas = Suma de diagonales.
     *
     * @param matriz La matriz a verificar.
     * @return true si es mágico, false en caso contrario.
     */
    public static boolean esMagico(int[][] matriz) {
        int n = matriz.length;
        int suma = 0;

        // 1. Calcula la suma de referencia (Primera fila)
        for (int i = 0; i < n; i++) suma += matriz[0][i];

        // 2. Verifica el resto de filas
        for (int i = 1; i < n; i++) {
            int sumaFila = 0;
            for (int j = 0; j < n; j++) sumaFila += matriz[i][j];
            if (sumaFila != suma) return false; // Fallo temprano para optimizar
        }

        // 3. Verifica las columnas
        for (int j = 0; j < n; j++) {
            int sumaColumna = 0;
            // Fijamos columna 'j' y recorremos filas 'i'
            for (int i = 0; i < n; i++) sumaColumna += matriz[i][j];
            if (sumaColumna != suma) return false;
        }

        // 4. Verifica las diagonales (principal y secundaria)
        int d1 = 0, d2 = 0;
        for (int i = 0; i < n; i++) {
            d1 += matriz[i][i];             // Diagonal principal
            d2 += matriz[i][n - 1 - i];     // Diagonal secundaria
        }

        return d1 == suma && d2 == suma; // Si pasa todas las pruebas, es mágico
    }

    /**
     * Punto de entrada de la aplicación.
     * Inicia el hilo de despacho de eventos de Swing (EDT).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}