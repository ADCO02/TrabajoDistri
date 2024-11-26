package Partida;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;
import java.util.InputMismatchException;

public class Tablero implements Serializable {
    private boolean turnoCompleto;  // Flag para controlar si el turno ha sido completado

    private static final long serialVersionUID = 1L;

    private int numJugadores = 2;
    public int jugadorActual;
    private int[] dados;
    private boolean[] dadosBloqueados;
    private transient Scanner scanner; // El Scanner no es serializable, por lo que lo marco como transient

    private String[] combinaciones = {"Unos", "Doses", "Treses", "Cuatros", "Cincos", "Seises", "Tríos", "Cuartetos", "Full", "Escalera", "Perfecto"};
    private boolean[][] combinacionesCompletadas = new boolean[2][11];  // 2 jugadores, 11 combinaciones
    private List<int[]> puntuaciones = new ArrayList<>();
    
    
    

    public Tablero() {
        this.jugadorActual = 0;
        this.puntuaciones = new ArrayList<>();
        this.combinacionesCompletadas = new boolean[numJugadores][combinaciones.length];
        this.dados = new int[5];
        this.dadosBloqueados = new boolean[5];
        this.scanner = new Scanner(System.in);
        // Inicialización de las puntuaciones para cada jugador
    

        for (int i = 0; i < numJugadores; i++) {
            puntuaciones.add(new int[combinaciones.length]);
        }
    }

    public void iniciarTurno() {
        resetearDados(); // Asegúrate de que todos los dados estén desbloqueados
        tiraDados();   // Lanza los dados al inicio del turno
        turnoCompleto = false; // Resetea el flag al iniciar un nuevo turno

    }

    public boolean turnoTerminado() {
        return turnoCompleto;  // Devuelve true solo si el jugador ha terminado su turno
    }

    public void tiraDados() {
        for (int i = 0; i < dados.length; i++) {
            if (!dadosBloqueados[i]) {
                dados[i] = (int)(Math.random() * 6) + 1;
            }
        }
    }


    public boolean vuelveATirar() {
        int volverATirar = -1;  // Inicializamos con un valor no válido
        Scanner scanner = new Scanner(System.in);  // Aseguramos que el scanner esté presente
    
        while (volverATirar != 0 && volverATirar != 1) {  // Mientras la entrada no sea ni 0 ni 1
            System.out.println("¿Quieres volver a tirar los dados restantes? (Sí: 1 / No: 0)");
            if (scanner.hasNextInt()) {  // Verificamos si la entrada es un número entero
                volverATirar = scanner.nextInt();  // Leemos la opción del jugador
                if (volverATirar != 0 && volverATirar != 1) {
                    System.out.println("Por favor, ingresa 1 para volver a tirar o 0 para no.");
                }
            } else {
                System.out.println("Entrada inválida. Por favor ingresa 1 o 0.");
                scanner.next();  // Limpiar el buffer de entrada en caso de entrada no válida
            }
        }
    
        if (volverATirar == 0) {
            turnoCompleto = true;  // Si el jugador no vuelve a tirar, completa el turno
        }
    
        return volverATirar == 1;
    }
    
    
    
    

    public void bloqueaDados() {
        System.out.println("Introduce los índices de los dados que deseas bloquear (0 a 4). Introduce -1 para terminar:");
    
        for (int i = 0; i < dados.length; i++) {
            int bloquear = -1; // Inicializamos con un valor no válido
    
            while (bloquear != 1 && bloquear != 0) {
                System.out.print("¿Bloquear dado " + i + " (" + dados[i] + ")? (Sí: 1 / No: 0): ");
                if (scanner.hasNextInt()) {  // Verifica que la entrada sea un número
                    bloquear = scanner.nextInt();
                    if (bloquear == 1 || bloquear == 0) {
                        dadosBloqueados[i] = (bloquear == 1);
                    } else {
                        System.out.println("Por favor, ingresa 1 para bloquear o 0 para no bloquear.");
                    }
                } else {
                    System.out.println("Entrada inválida. Por favor ingresa 1 o 0.");
                    scanner.next(); // Limpiar el buffer si la entrada no es un número
                }
            }
        }
    }
    
    

    public void mostrarBloqueados() {
        System.out.print("Estado de los dados: ");
        for (int i = 0; i < dados.length; i++) {
            String estado = dadosBloqueados[i] ? "(Bloqueado)" : "(Libre)";
            System.out.print(dados[i] + " " + estado + " ");
        }
        System.out.println();
    }
    
    public void eligeCasilla() {
        boolean seleccionValida = false;
    
        // Validación previa
        if (combinaciones == null || combinacionesCompletadas == null || combinacionesCompletadas[jugadorActual] == null) {
            throw new IllegalStateException("El estado del tablero no está completamente inicializado.");
        }
    
        while (!seleccionValida) {
            mostrarTablero();
            System.out.println("Elige una combinación (0-" + (combinaciones.length - 1) + "): ");
            
            int combinacion;
            try {
                combinacion = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor ingresa un número.");
                scanner.next(); // Limpiar el buffer del scanner
                continue;
            }
    
            // Validar índice de combinación
            if (combinacion < 0 || combinacion >= combinaciones.length) {
                System.out.println("Combinación no válida. Por favor elige un número entre 0 y " + (combinaciones.length - 1));
                continue;
            }
    
            // Validar si la combinación ya está ocupada
            if (combinacionesCompletadas[jugadorActual][combinacion]) {
                System.out.println("Combinación ya ocupada. Elige otra.");
                continue;
            }
    
            // Calcular puntuación
            int puntaje = calcularPuntuacion(combinacion);
            if (puntaje < 0) {
                System.out.println("Puntuación calculada inválida. Intenta otra combinación.");
                continue;
            }
    
            // Registrar puntuación
            if (registrarPuntuacion(combinacion, puntaje)) {
                System.out.println("Combinación registrada: " + puntaje + " puntos.");
                seleccionValida = true;
                turnoCompleto = true; // Marcar el turno como completo
                terminaTurno();       // Llamar al método para completar el turno
            } else {
                System.out.println("No se pudo registrar la puntuación. Intenta nuevamente.");
            }
        }
        mostrarTablero();
    }
    
    
    
    

    public void mostrarDados() {
        System.out.print("Dados: ");
        for (int i = 0; i < dados.length; i++) {
            String estado = dadosBloqueados[i] ? "(Bloqueado)" : "(Libre)";
            System.out.print(dados[i] + " " + estado + " ");
        }
        System.out.println();
    }

    public boolean verificarJuegoTerminado() {
        for (boolean[] combinaciones : combinacionesCompletadas) {
            for (boolean completada : combinaciones) {
                if (!completada) return false;
            }
        }
        return true;
    }

    public void mostrarResultados() {
        int maxPuntaje = -1;
        int ganador = -1;

        for (int i = 0; i < numJugadores; i++) {
            int puntajeTotal = Arrays.stream(puntuaciones.get(i)).sum();
            System.out.println("Puntaje del Jugador " + (i + 1) + ": " + puntajeTotal);

            if (puntajeTotal > maxPuntaje) {
                maxPuntaje = puntajeTotal;
                ganador = i;
            }
        }

        System.out.println("\nEl ganador es el Jugador " + (ganador + 1) + " con " + maxPuntaje + " puntos.");
    }


  

    public int calcularPuntuacion(int combinacion) {
        int[] contadorValores = new int[6];
        for (int dado : dados) {
            contadorValores[dado - 1]++;
        }

        int puntaje = 0;
        switch (combinacion) {
            case 0: case 1: case 2: case 3: case 4: case 5: // Unos a Seises
                puntaje = contadorValores[combinacion] * (combinacion + 1);
                break;
            case 6: // Tríos
                puntaje = Arrays.stream(dados).sum();
                for (int count : contadorValores) {
                    if (count >= 3) return puntaje;
                }
                puntaje = 0;
                break;
            case 7: // Cuartetos
                puntaje = Arrays.stream(dados).sum();
                for (int count : contadorValores) {
                    if (count >= 4) return puntaje;
                }
                puntaje = 0;
                break;
            case 8: // Full
                if (Arrays.stream(contadorValores).anyMatch(c -> c == 3) && 
                    Arrays.stream(contadorValores).anyMatch(c -> c == 2)) {
                    puntaje = 25;
                }
                break;
            case 9: // Escalera
                if (esEscalera(contadorValores)) puntaje = 40;
                break;
            case 10: // Perfecto
                if (Arrays.stream(contadorValores).anyMatch(c -> c == 5)) puntaje = 50;
                break;
        }
        return puntaje;
    }

    public boolean esEscalera(int[] contadorValores) {
        return (contadorValores[0] >= 1 && contadorValores[1] >= 1 && contadorValores[2] >= 1 && 
                contadorValores[3] >= 1 && contadorValores[4] >= 1) || 
               (contadorValores[1] >= 1 && contadorValores[2] >= 1 && contadorValores[3] >= 1 && 
                contadorValores[4] >= 1 && contadorValores[5] >= 1);
    }

    public boolean registrarPuntuacion(int combinacion, int puntaje) {
        if (combinacion >= 0 && combinacion < combinaciones.length 
            && !combinacionesCompletadas[jugadorActual][combinacion]) {
            
            puntuaciones.get(jugadorActual)[combinacion] = puntaje;
            combinacionesCompletadas[jugadorActual][combinacion] = true;
            return true;
        }
        return false;
    }
    /*public void siguienteJugador() {
        jugadorActual = (jugadorActual + 1) % numJugadores;
        mostrarTablero();  // Actualiza el tablero para el siguiente jugador

    }*/


    public void mostrarTablero() {
        System.out.printf("%-15s %-15s %-15s\n", "Combinación", "Jugador 1", "Jugador 2");
        for (int i = 0; i < combinaciones.length; i++) {
            String estadoJ1 = combinacionesCompletadas[0][i] ? String.valueOf(puntuaciones.get(0)[i]) : "Disponible";
            String estadoJ2 = combinacionesCompletadas[1][i] ? String.valueOf(puntuaciones.get(1)[i]) : "Disponible";
            System.out.printf("%-15s %-15s %-15s\n", combinaciones[i], estadoJ1, estadoJ2);
        }
    }

    public void resetearDados() {
        Arrays.fill(dadosBloqueados, false);
    }

 //  Método que devuelve un booleano si el turno ha terminado
 public void terminaTurno() {
    System.out.println("El jugador " + (jugadorActual + 1) + " ha terminado su turno.");
    //siguienteJugador();  // Cambiar al siguiente jugador.
    jugadorActual = (jugadorActual + 1) % numJugadores;

    resetearDados();
    turnoCompleto = true;  // El turno ha terminado
}



private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
    this.scanner = new Scanner(System.in); // Reinicializar el scanner después de la deserialización
}
}