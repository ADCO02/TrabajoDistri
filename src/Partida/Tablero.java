package Partida;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class Tablero {
    private int numJugadores;
    private int jugadorActual;
    private List<int[]> puntuaciones;
    private boolean[][] combinacionesCompletadas;
    private int[] dados;
    private boolean[] dadosBloqueados;
    private Scanner scanner;

    private final String[] combinaciones = {
        "Unos - 0", "Doses - 1", "Treses - 2", "Cuatros - 3", "Cincos - 4", "Seises - 5",
        "Tríos - 6", "Cuartetos - 7", "Full - 8", "Escalera - 9", "Perfecto - 10"
    };

    public Tablero(int numJugadores) {
        this.numJugadores = numJugadores;
        this.jugadorActual = 0;
        this.puntuaciones = new ArrayList<>();
        this.combinacionesCompletadas = new boolean[numJugadores][combinaciones.length];
        this.dados = new int[5];
        this.dadosBloqueados = new boolean[5];
        this.scanner = new Scanner(System.in);

        for (int i = 0; i < numJugadores; i++) {
            puntuaciones.add(new int[combinaciones.length]);
        }
    }

    public static void main(String[] args) {
        // Crear un tablero para dos jugadores
        Tablero tablero = new Tablero(2);

        // Iniciar el juego
        tablero.jugar();
    }
    // Método principal para jugar la partida
    public void jugar() {
        boolean juegoTerminado = false;

        while (!juegoTerminado) {
            System.out.println("\nTurno del Jugador " + (jugadorActual + 1));
            
            // Mostrar tablero completo antes de lanzar
            mostrarTablero();
            
            for (int lanzamiento = 1; lanzamiento <= 3; lanzamiento++) {
                lanzarDados();
                mostrarDados();

                if (lanzamiento < 3) {
                    System.out.println("¿Quieres volver a tirar los dados restantes? (Sí: 1 / No: 0)");
                    int volverATirar = scanner.nextInt();

                    if (volverATirar == 0) {
                        break; // Saltar a la selección de puntuación
                    }
                }
            }

            // Volver a mostrar el tablero completo antes de elegir combinación
            mostrarTablero();
            int combinacion;
            boolean seleccionValida = false;

            // Repetir hasta que se elija una combinación válida
            while (!seleccionValida) {
                System.out.println("Elige una combinación (0-" + (combinaciones.length - 1) + "): ");
                combinacion = scanner.nextInt();

                // Comprobar si la combinación ya está ocupada
                if (combinacionesCompletadas[jugadorActual][combinacion]) {
                    System.out.println("Combinación ya ocupada. Elige otra.");
                    mostrarTablero();  // Vuelve a mostrar el tablero
                } else {
                    int puntaje = calcularPuntuacion(combinacion);
                    if (registrarPuntuacion(combinacion, puntaje)) {
                        System.out.println("Combinación registrada: " + puntaje + " puntos.");
                        seleccionValida = true;
                    }
                }
            }

            juegoTerminado = verificarJuegoTerminado();
            siguienteJugador();
            resetearDados();
        }

        anunciarGanador();
    }

    private void lanzarDados() {
        for (int i = 0; i < dados.length; i++) {
            if (!dadosBloqueados[i]) {
                dados[i] = (int)(Math.random() * 6) + 1;
            }
        }
    }

    private void mostrarDados() {
        System.out.print("Dados: ");
        for (int i = 0; i < dados.length; i++) {
            String estado = dadosBloqueados[i] ? "(Bloqueado)" : "(Libre)";
            System.out.print(dados[i] + " " + estado + " ");
        }
        System.out.println();
    }

    private void bloquearDados() {
        System.out.println("Introduce los índices de los dados que deseas bloquear (0 a 4). Introduce -1 para terminar:");
        
        for (int i = 0; i < dados.length; i++) {
            System.out.print("¿Bloquear dado " + i + " (" + dados[i] + ")? (Sí: 1 / No: 0): ");
            int bloquear = scanner.nextInt();
            dadosBloqueados[i] = (bloquear == 1);
        }
    }

    // Calcula el puntaje de la combinación seleccionada
    private int calcularPuntuacion(int combinacion) {
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

    private boolean esEscalera(int[] contadorValores) {
        return (contadorValores[0] >= 1 && contadorValores[1] >= 1 && contadorValores[2] >= 1 && 
                contadorValores[3] >= 1 && contadorValores[4] >= 1) || 
               (contadorValores[1] >= 1 && contadorValores[2] >= 1 && contadorValores[3] >= 1 && 
                contadorValores[4] >= 1 && contadorValores[5] >= 1);
    }

    private boolean registrarPuntuacion(int combinacion, int puntaje) {
        if (combinacion >= 0 && combinacion < combinaciones.length && !combinacionesCompletadas[jugadorActual][combinacion]) {
            puntuaciones.get(jugadorActual)[combinacion] = puntaje;
            combinacionesCompletadas[jugadorActual][combinacion] = true;
            return true;
        }
        return false;
    }

    private void siguienteJugador() {
        jugadorActual = (jugadorActual + 1) % numJugadores;
    }

    private boolean verificarJuegoTerminado() {
        for (boolean[] combinaciones : combinacionesCompletadas) {
            for (boolean completada : combinaciones) {
                if (!completada) return false;
            }
        }
        return true;
    }

    private void anunciarGanador() {
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

    private void mostrarTablero() {
        System.out.printf("%-15s %-15s %-15s\n", "Combinación", "Jugador 1", "Jugador 2");
        for (int i = 0; i < combinaciones.length; i++) {
            String estadoJ1 = combinacionesCompletadas[0][i] ? String.valueOf(puntuaciones.get(0)[i]) : "Disponible";
            String estadoJ2 = combinacionesCompletadas[1][i] ? String.valueOf(puntuaciones.get(1)[i]) : "Disponible";
            System.out.printf("%-15s %-15s %-15s\n", combinaciones[i], estadoJ1, estadoJ2);
        }
    }

    private void resetearDados() {
        Arrays.fill(dadosBloqueados, false);
    }
}
