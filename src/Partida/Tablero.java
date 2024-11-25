package Partida;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class Tablero implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numJugadores;
    private int jugadorActual;
    private List<int[]> puntuaciones;
    private boolean[][] combinacionesCompletadas;
    private int[] dados;
    private boolean[] dadosBloqueados;
    private transient Scanner scanner; // El Scanner no es serializable, por lo que lo marco como transient
    private boolean turnoActivo; // Nueva variable para controlar el turno activo

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
        this.turnoActivo = true; // El turno empieza activo para el primer jugador

        for (int i = 0; i < numJugadores; i++) {
            puntuaciones.add(new int[combinaciones.length]);
        }
    }

    public void iniciarTurno() {
        if (turnoActivo) {
            resetearDados(); // Asegúrate de que todos los dados estén desbloqueados
            tiraDados(); // Lanza los dados al inicio del turno
            mostrarDados(); // Muestra los dados al jugador
        }
    }

    public boolean turnoTerminado() {
        // Aquí se puede agregar la lógica para verificar si el turno se ha terminado
        for (boolean combinacionCompletada : combinacionesCompletadas[jugadorActual]) {
            if (combinacionCompletada) {
                return true; // Si alguna combinación está completada, el turno terminó
            }
        }
        return false; // Si ninguna casilla está completada, el turno no ha terminado
    }

    public void tiraDados() {
        for (int i = 0; i < dados.length; i++) {
            if (!dadosBloqueados[i]) {
                dados[i] = (int) (Math.random() * 6) + 1;
            }
        }
    }

    public boolean vuelveATirar() {
        System.out.println("¿Quieres volver a tirar los dados restantes? (Sí: 1 / No: 0)");
        int volverATirar = scanner.nextInt();
        return volverATirar == 1;
    }

    public void bloqueaDados() {
        System.out
                .println("Introduce los índices de los dados que deseas bloquear (0 a 4). Introduce -1 para terminar:");

        for (int i = 0; i < dados.length; i++) {
            System.out.print("¿Bloquear dado " + i + " (" + dados[i] + ")? (Sí: 1 / No: 0): ");
            int bloquear = scanner.nextInt();
            dadosBloqueados[i] = (bloquear == 1);
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
        if (turnoActivo) {
            boolean seleccionValida = false;
            while (!seleccionValida) {
                mostrarTablero();
                System.out.println("Elige una combinación (0-" + (combinaciones.length - 1) + "): ");
                int combinacion = scanner.nextInt();

                if (combinacionesCompletadas[jugadorActual][combinacion]) {
                    System.out.println("Combinación ya ocupada. Elige otra.");
                } else {
                    int puntaje = calcularPuntuacion(combinacion);
                    if (registrarPuntuacion(combinacion, puntaje)) {
                        System.out.println("Combinación registrada: " + puntaje + " puntos.");
                        seleccionValida = true;
                        finalizarTurno();
                    }
                }
            }
        }
    }

    public void mostrarDados() {
        if (turnoActivo) {
            System.out.print("Dados: ");
            for (int i = 0; i < dados.length; i++) {
                String estado = dadosBloqueados[i] ? "(Bloqueado)" : "(Libre)";
                System.out.print(dados[i] + " " + estado + " ");
            }
            System.out.println();
        }
    }

    public boolean verificarJuegoTerminado() {
        for (boolean[] combinaciones : combinacionesCompletadas) {
            for (boolean completada : combinaciones) {
                if (!completada)
                    return false;
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

    public void finalizarTurno() {
        System.out.println("Turno del Jugador " + (jugadorActual + 1) + " finalizado.");
        turnoActivo = false; // Desactivar el turno actual
        siguienteJugador(); // Pasamos al siguiente jugador
        turnoActivo = true; // Activamos el turno del siguiente jugador
    }

    public void siguienteJugador() {
        jugadorActual = (jugadorActual + 1) % numJugadores;
        System.out.println("Ahora es el turno del Jugador " + (jugadorActual + 1));
    }

    public boolean esMiTurno() {
        return turnoActivo && jugadorActual == (this.numJugadores == 2 ? 0 : 1); // Verificar si es el turno actual
    }
    

    public int calcularPuntuacion(int combinacion) {
        int[] contadorValores = new int[6];
        for (int dado : dados) {
            contadorValores[dado - 1]++;
        }

        int puntaje = 0;
        switch (combinacion) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5: // Unos a Seises
                puntaje = contadorValores[combinacion] * (combinacion + 1);
                break;
            case 6: // Tríos
                puntaje = Arrays.stream(dados).sum();
                for (int count : contadorValores) {
                    if (count >= 3)
                        return puntaje;
                }
                puntaje = 0;
                break;
            case 7: // Cuartetos
                puntaje = Arrays.stream(dados).sum();
                for (int count : contadorValores) {
                    if (count >= 4)
                        return puntaje;
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
                if (esEscalera(contadorValores))
                    puntaje = 40;
                break;
            case 10: // Perfecto
                if (Arrays.stream(contadorValores).anyMatch(c -> c == 5))
                    puntaje = 50;
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
}
