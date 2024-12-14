package com.example.cincuentazo.controller;

import com.example.cincuentazo.model.CZ;
import javafx.scene.control.Alert;
import com.example.cincuentazo.exception.MazoVacioException;
import com.example.cincuentazo.exception.IndiceCartaInvalidoException;
import com.example.cincuentazo.exception.ManoVaciaException;
import java.util.ArrayList;
import java.util.InputMismatchException;
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

    private void mostrarManos() {
        System.out.println("\nManos actuales de los jugadores:");
        for (int i = 0; i < manosJugadores.size(); i++) {
            if (manosJugadores.get(i) != null && !manosJugadores.get(i).isEmpty()) {
                // Mostrar la mano del jugador humano (jugador 1)
                if (i == 0) {
                    System.out.println("Jugador " + (i + 1) + " (Tú): " + manosJugadores.get(i));
                } else {
                    // Mostrar manos de los jugadores IA de forma oculta
                    System.out.print("Jugador " + (i + 1) + " (IA): ");
                    for (int j = 0; j < manosJugadores.get(i).size(); j++) {
                        System.out.print("*** "); // Ocultar las cartas de la IA
                    }
                    System.out.println();
                }
            } else {
                System.out.println("Jugador " + (i + 1) + " ha sido eliminado.");
            }
        }
    }

    public void iniciarJuegoConsola() throws MazoVacioException {
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

    public void jugar() throws MazoVacioException {
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
        System.exit(0); // Finalizar el juego.
    }

    public int pedirValorAs() throws IndiceCartaInvalidoException {
        Scanner scanner = new Scanner(System.in);
        int valorAs;

        try {
            System.out.print("El As vale 1 o 10 puntos? Elige (1/10): ");
            valorAs = scanner.nextInt();

            while (valorAs != 1 && valorAs != 10) {
                System.out.print("Opción inválida. Elige 1 o 10: ");
                valorAs = scanner.nextInt();
            }
            return valorAs;

        } catch (InputMismatchException e) {
            throw new IndiceCartaInvalidoException("Valor ingresado inválido. Solo puedes elegir 1 o 10.");
        }
    }


    private boolean tieneMovimientosValidos(int jugador) {
        for (CZ.Carta carta : manosJugadores.get(jugador)) {
            if (modelo.puedeJugarCarta(carta, sumaMesa)) {
                return true;
            }
        }
        return false;
    }

    private void verificarFinDelJuego() throws MazoVacioException {
        // Si solo queda un jugador, declarar ganador
        if (manosJugadores.size() == 1) {
            System.out.println("¡El jugador " + (jugadorActual + 1) + " es el ganador!");
            modelo.WinAlert();
            System.exit(0);
        }

        // Verificar si la baraja está vacía
        if (modelo.mostrarCartasDisponibles() == 0) {
            System.out.println("El mazo está vacío. Intentando reciclar cartas...");
            modelo.reciclarCartas(cartasEnMesa);
            cartasEnMesa.clear();

            if (modelo.mostrarCartasDisponibles() == 0) {
                throw new MazoVacioException("No hay más cartas en el mazo. ¡Juego terminado en empate!");
            }
        }
    }

    public void turnoJugador(int jugador, int indiceCarta) {
        try {
            // Verificar si el jugador tiene movimientos válidos antes de proceder
            if (!tieneMovimientosValidos(jugador)) {
                throw new ManoVaciaException("El jugador no tiene movimientos válidos y ha sido eliminado.");
            }

            // Validar el índice de la carta seleccionada
            if (indiceCarta < 0 || indiceCarta >= manosJugadores.get(jugador).size()) {
                throw new IndiceCartaInvalidoException("El índice de la carta seleccionada no es válido.");
            }

            // Obtener la carta jugada
            CZ.Carta cartaJugada = manosJugadores.get(jugador).get(indiceCarta);

            // Si la carta es un As, pedir el valor al jugador humano o decidirlo automáticamente
            if ("A".equals(cartaJugada.getValor())) {
                int valorAs = (jugador == 0) ? pedirValorAs() : seleccionarValorAsIA(sumaMesa, manosJugadores.get(jugador).size());
                cartaJugada = new CZ.Carta(cartaJugada.getPalo(), "A", valorAs);
            }

            // Actualizar la mesa y remover la carta jugada
            manosJugadores.get(jugador).remove(indiceCarta);
            cartasEnMesa.add(cartaJugada);
            sumaMesa += modelo.calcularPuntosCarta(cartaJugada, sumaMesa);

            System.out.println("Jugador " + (jugador + 1) + " jugó: " + cartaJugada);
            System.out.println("Suma actual en la mesa: " + sumaMesa);

            // Verificar si el jugador pierde
            if (sumaMesa > 50) {
                System.out.println("Jugador " + (jugador + 1) + " perdió. Suma excedió 50.");
                modelo.DefeatAlert();

                // Reciclar cartas y eliminar al jugador
                modelo.reciclarCartas(manosJugadores.get(jugador));
                manosJugadores.remove(jugador);
                verificarFinDelJuego();
                return;
            }

            // Verificar si el jugador humano debe tomar carta
            if (jugador == 0) {
                preguntarSiTomarCarta(jugador);
            } else if (manosJugadores.get(jugador).size() == 3) { // IA decide tomar carta automáticamente
                System.out.println("Jugador " + (jugador + 1) + " (IA) está pensando si tomar una carta...");
                Thread.sleep((int) (2000 + Math.random() * 2000));
                modelo.tomarCarta(jugador);
            }

            // Mostrar las manos restantes
            mostrarManos();
            verificarFinDelJuego();

        } catch (ManoVaciaException | IndiceCartaInvalidoException e) {
            System.out.println("Error: " + e.getMessage());
            modelo.mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);

        } catch (MazoVacioException e) {
            System.out.println("El mazo está vacío: " + e.getMessage());
            modelo.mostrarAlerta("Juego Terminado", e.getMessage(), Alert.AlertType.INFORMATION);
            System.exit(0);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void preguntarSiTomarCarta(int jugador) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("¿Deseas tomar una carta del mazo? (Sí/No): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if (respuesta.equals("sí") || respuesta.equals("si")) {
            CZ.Carta nuevaCarta = modelo.tomarCartaDelMazo();
            if (nuevaCarta != null) {
                manosJugadores.get(jugador).add(nuevaCarta);
                System.out.println("Has tomado: " + nuevaCarta);
            } else {
                System.out.println("No hay cartas disponibles en el mazo.");
            }
        }
    }

    private void turnoIA(int jugador) throws MazoVacioException {
        if (jugador >= manosJugadores.size()) {
            return;
        }

        if (!tieneMovimientosValidos(jugador)) {
            System.out.println("Jugador " + (jugador + 1) + " no tiene movimientos válidos. Eliminado.");
            modelo.DevolverCartasAlMazo(manosJugadores.get(jugador)); // Devolver cartas al mazo
            manosJugadores.remove(jugador);
            verificarFinDelJuego();
            return;
        }

        List<CZ.Carta> mano = manosJugadores.get(jugador);

        // Mostrar mensaje de que la IA está pensando
        System.out.println("Jugador " + (jugador + 1) + " (IA) está pensando...");

        // Simular tiempo de reflexión de la IA (2 a 4 segundos)
        try {
            Thread.sleep((int) (2000 + Math.random() * 2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verificar si la IA puede jugar alguna carta
        for (int i = 0; i < mano.size(); i++) {
            CZ.Carta carta = mano.get(i);
            if (modelo.puedeJugarCarta(carta, sumaMesa)) {
                if ("A".equals(carta.getValor())) {
                    int puntosAs = seleccionarValorAsIA(sumaMesa, mano.size());
                    mano.set(i, new CZ.Carta(carta.getPalo(), carta.getValor(), puntosAs));
                }
                turnoJugador(jugador, i);
                return;
            }
        }

        // Si no puede jugar ninguna carta, eliminar al jugador
        System.out.println("Jugador " + (jugador + 1) + " (IA) no tiene movimientos válidos. Eliminado.");
        modelo.DevolverCartasAlMazo(mano); // Devolver cartas al mazo
        manosJugadores.remove(jugador);
        verificarFinDelJuego();
    }

    private int seleccionarValorAsIA(int sumaActual, int cantidadCartasMano) {
        // Si la IA tiene pocas cartas, puede arriesgar más, eligiendo 10 si no excede 50
        if (cantidadCartasMano > 2) {
            if (sumaActual + 10 <= 50) {
                return 10; // Seleccionar 10 si no se pasa de 50
            } else {
                return 1; // Si se va a pasar, elegir 1
            }
        } else {
            // Si la IA tiene pocas cartas, puede arriesgarse más.
            // Elige 10 si es probable que gane o no se pase
            if (sumaActual + 10 <= 50) {
                return 10;
            } else {
                // Si tiene pocas cartas y la suma es alta, preferirá un 1
                return 1;
            }
        }
    }
}