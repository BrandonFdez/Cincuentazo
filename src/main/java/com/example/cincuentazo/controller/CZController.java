package com.example.cincuentazo.controller;

import com.example.cincuentazo.model.CZ;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CZController {

    private CZ modelo;
    private List<List<CZ.Carta>> manosJugadores;
    private List<CZ.Carta> cartasEnMesa;
    private int sumaMesa;
    private int cantidadJugadores;
    private int jugadorActual;

    public CZController() {
        this.modelo = new CZ();
        this.cartasEnMesa = new ArrayList<>();
    }

    public void iniciarJuegoConsola() {
        solicitarCantidadJugadores();
        modelo.repartirCartas(cantidadJugadores);
        manosJugadores = modelo.getManosJugadores();

        CZ.Carta cartaInicial = modelo.tomarCartaDelMazo();
        cartasEnMesa.add(cartaInicial);
        sumaMesa = cartaInicial.getPuntos();

        System.out.println("Juego iniciado con " + cantidadJugadores + " jugadores.");
        System.out.println("Carta inicial en la mesa: " + cartaInicial);
        System.out.println("Cartas disponibles en el mazo: " + modelo.mostrarCartasDisponibles());
        mostrarManos();

        jugar();
    }

    private void solicitarCantidadJugadores() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingresa la cantidad de jugadores (2-4): ");
        try {
            cantidadJugadores = Integer.parseInt(scanner.nextLine());
            if (cantidadJugadores < 2 || cantidadJugadores > 4) {
                System.out.println("Cantidad inválida. Se usará el valor por defecto: 2.");
                cantidadJugadores = 2;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Se usará el valor por defecto: 2.");
            cantidadJugadores = 2;
        }
    }

    public void jugar() {
        Scanner scanner = new Scanner(System.in);
        int turno = 0;

        while (manosJugadores.size() > 1) { // El juego continúa mientras haya más de un jugador
            jugadorActual = turno % manosJugadores.size(); // Actualiza el turno de manera correcta

            if (jugadorActual == 0) {
                // Turno del jugador humano
                System.out.println("\nTu turno. Suma actual en la mesa: " + sumaMesa);
                System.out.println("Cartas disponibles en el mazo: " + modelo.mostrarCartasDisponibles());
                System.out.println("Tu mano: " + manosJugadores.get(0));
                System.out.print("Selecciona el índice de la carta que deseas jugar (0-" + (manosJugadores.get(0).size() - 1) + "): ");

                try {
                    int indiceCarta = Integer.parseInt(scanner.nextLine());
                    turnoJugador(jugadorActual, indiceCarta);
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Intenta de nuevo.");
                }
            } else {
                // Turno de la IA
                System.out.println("\n===============================");
                System.out.println("Turno del jugador " + (jugadorActual + 1));
                System.out.println("===============================\n");
                turnoIA(jugadorActual);
            }

            turno++;
        }

        // Fin del juego, solo queda un jugador
        System.out.println("¡El jugador " + (jugadorActual + 1) + " es el ganador!");
        modelo.WinAlert();
        System.exit(0); // Finalizar el juego
    }

    public int pedirValorAs() {
        Scanner scanner = new Scanner(System.in);
        int valorAs;
        System.out.print("El As vale 1 o 10 puntos? Elige (1/10): ");

        valorAs = scanner.nextInt();
        while (valorAs != 1 && valorAs != 10) {
            System.out.print("Opción inválida. Elige 1 o 10: ");
            valorAs = scanner.nextInt();
        }

        return valorAs;
    }


    /**
     * Verifica si el jugador tiene movimientos válidos en su mano.
     * @param jugador Índice del jugador en la lista de manos.
     * @return true si el jugador puede jugar al menos una carta válida; false en caso contrario.
     */
    private boolean tieneMovimientosValidos(int jugador) {
        for (CZ.Carta carta : manosJugadores.get(jugador)) {
            if (modelo.puedeJugarCarta(carta, sumaMesa)) {
                return true;
            }
        }
        return false;
    }

    public void turnoJugador(int jugador, int indiceCarta) {
        if (!tieneMovimientosValidos(jugador)) {
            System.out.println("Jugador " + (jugador + 1) + " no tiene movimientos válidos. Eliminado.");
            modelo.DefeatAlert();
            manosJugadores.remove(jugador); // Eliminar al jugador
            verificarFinDelJuego(); // Verificar si el juego ha terminado
            return; // Finalizar turno sin jugar
        }

        // Validar índices del jugador y carta
        if (jugador >= manosJugadores.size() || indiceCarta >= manosJugadores.get(jugador).size()) {
            modelo.ErrorAlert();
            return;
        }

        CZ.Carta cartaJugada = manosJugadores.get(jugador).get(indiceCarta);

        // Verificar si la carta jugada es un As
        if ("A".equals(cartaJugada.getValor())) {
            int valorAs = pedirValorAs(); // Pedir al jugador el valor del As
            cartaJugada = new CZ.Carta(cartaJugada.getPalo(), "A", valorAs); // Actualizar el valor de la carta "A"
        }

        // Jugar la carta directamente y calcular la nueva suma
        manosJugadores.get(jugador).remove(indiceCarta);
        cartasEnMesa.add(cartaJugada);
        sumaMesa += modelo.calcularPuntosCarta(cartaJugada, sumaMesa);

        System.out.println("Jugador " + (jugador + 1) + " jugó: " + cartaJugada);
        System.out.println("Suma actual en la mesa: " + sumaMesa);

        if (sumaMesa > 50) {
            System.out.println("Jugador " + (jugador + 1) + " perdió. Suma excedió 50.");
            modelo.DefeatAlert();
            manosJugadores.remove(jugador); // Eliminar al jugador
            verificarFinDelJuego();
            return; // Finalizar turno si el jugador pierde
        }

        modelo.tomarCarta(jugador);
        mostrarManos();
        verificarFinDelJuego();
    }

    private void mostrarManos() {
        for (int i = 0; i < manosJugadores.size(); i++) {
            // Mostrar las cartas del jugador actual (jugador humano o IA)
            if (i == 0) { // Jugador 1 (humano)
                System.out.println("Mano del jugador " + (i + 1) + ": " + manosJugadores.get(i));
            } else { // Jugadores IA
                System.out.print("Mano del jugador " + (i + 1) + ": ");
                for (int j = 0; j < manosJugadores.get(i).size(); j++) {
                    System.out.print("*** "); // Mostrar cartas ocultas para IA
                }
                System.out.println();
            }
        }
    }

    private void verificarFinDelJuego() {
        if (manosJugadores.size() == 1) {
            int jugadorGanador = manosJugadores.get(0).equals(manosJugadores.get(0)) ? 2 : 1; // Si solo queda un jugador, el otro es el ganador
            System.out.println("¡El jugador " + jugadorGanador + " es el ganador!");
            modelo.WinAlert();
            System.exit(0); // Finalizar el juego
        }

        if (modelo.mostrarCartasDisponibles() == 0) {
            System.out.println("El mazo está vacío. Intentando reciclar cartas...");
            modelo.reciclarCartas(cartasEnMesa);

            if (modelo.mostrarCartasDisponibles() == 0) {
                System.out.println("No hay más cartas para jugar. ¡Empate!");
                modelo.mostrarAlerta("Juego Finalizado", "No hay más cartas para jugar. ¡Empate!", Alert.AlertType.INFORMATION);
                System.exit(0);
            }
        }
    }

    public void turnoIA(int jugador) {
        if (jugador >= manosJugadores.size()) {
            return;
        }
        // Verificar movimientos válidos ANTES de intentar jugar
        if (!tieneMovimientosValidos(jugador)) {
            System.out.println("Jugador " + (jugador + 1) + " no tiene movimientos válidos. Eliminado.");
            manosJugadores.remove(jugador);
            verificarFinDelJuego(); // Esto declarará al otro jugador como ganador
            return;
        }
        List<CZ.Carta> mano = manosJugadores.get(jugador);
        for (int i = 0; i < mano.size(); i++) {
            CZ.Carta carta = mano.get(i);
            if (modelo.puedeJugarCarta(carta, sumaMesa)) {
                // Realizar la jugada de la IA sin mostrar sus cartas
                turnoJugador(jugador, i);
                return;
            }
        }
    }
}