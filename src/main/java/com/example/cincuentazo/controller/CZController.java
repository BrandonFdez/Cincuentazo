package com.example.cincuentazo.controller;

import com.example.cincuentazo.model.CZ;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CZController {

    private CZ modelo;
    private List<List<CZ.Carta>> manosJugadores;
    private List<CZ.Carta> cartasEnMesa;
    private int sumaMesa;
    private int cantidadJugadores;

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

    private void mostrarManos() {
        for (int i = 0; i < manosJugadores.size(); i++) {
            System.out.println("Mano del jugador " + (i + 1) + ": " + manosJugadores.get(i));
        }
    }

    private void jugar() {
        Scanner scanner = new Scanner(System.in);
        int turno = 0;

        while (true) {
            int jugadorActual = turno % cantidadJugadores;

            if (manosJugadores.size() <= jugadorActual) {
                turno++;
                continue;
            }

            if (jugadorActual == 0) {
                // Turno del jugador humano
                System.out.println("\nTu turno. Suma actual en la mesa: " + sumaMesa);
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
                System.out.println("\nTurno del jugador " + (jugadorActual + 1));
                turnoIA(jugadorActual);
            }

            turno++;
        }
    }

    public void turnoJugador(int jugador, int indiceCarta) {
        if (jugador >= manosJugadores.size() || indiceCarta >= manosJugadores.get(jugador).size()) {
            System.out.println("Acción inválida. Verifica tu jugada.");
            return;
        }

        CZ.Carta cartaJugada = manosJugadores.get(jugador).get(indiceCarta);

        if (!modelo.puedeJugarCarta(cartaJugada, sumaMesa)) {
            System.out.println("No puedes jugar esta carta. Superarías 50.");
            return;
        }

        manosJugadores.get(jugador).remove(indiceCarta);
        cartasEnMesa.add(cartaJugada);
        sumaMesa += modelo.calcularPuntosCarta(cartaJugada, sumaMesa);

        System.out.println("Jugador " + (jugador + 1) + " jugó: " + cartaJugada);
        System.out.println("Suma actual en la mesa: " + sumaMesa);

        if (sumaMesa > 50) {
            System.out.println("Jugador " + (jugador + 1) + " eliminado. Suma excedió 50.");
            manosJugadores.remove(jugador);
        } else {
            modelo.tomarCarta(jugador);
            System.out.println("Nueva mano del jugador " + (jugador + 1) + ": " + manosJugadores.get(jugador));
        }

        verificarFinDelJuego();
    }

    private void verificarFinDelJuego() {
        if (manosJugadores.size() == 1) {
            System.out.println("¡El jugador " + (manosJugadores.size()) + " es el ganador!");
            System.exit(0); // Fin del juego
        }
    }

    public void turnoIA(int jugador) {
        if (jugador >= manosJugadores.size()) {
            return;
        }

        List<CZ.Carta> mano = manosJugadores.get(jugador);
        for (int i = 0; i < mano.size(); i++) {
            CZ.Carta carta = mano.get(i);
            if (modelo.puedeJugarCarta(carta, sumaMesa)) {
                turnoJugador(jugador, i);
                return;
            }
        }

        System.out.println("Jugador " + (jugador + 1) + " no pudo jugar una carta válida.");
    }
}
