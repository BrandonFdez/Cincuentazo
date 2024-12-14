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

/**
 * Controller class for managing the game logic in the CZ (Cincuentazo) game.
 * It handles the interaction between the model (game state) and the user interface.
 */
public class CZController {

    private CZ modelo;  // The game model
    private List<List<CZ.Carta>> manosJugadores;  // List of hands for each player
    private List<CZ.Carta> cartasEnMesa;  // List of cards on the table
    private int sumaMesa;  // Total points of the cards on the table
    private int cantidadJugadores;  // Number of players
    private int jugadorActual;  // Current player

    /**
     * Constructor for the CZController class.
     * Initializes the model and the list of cards on the table.
     */
    public CZController() {
        this.modelo = new CZ();  // Instantiate the game model
        this.cartasEnMesa = new ArrayList<>();  // Initialize the list of cards on the table
    }

    /**
     * Displays the current hands of all players.
     * It shows the human player's hand fully, while hiding the hands of AI players.
     */
    private void mostrarManos() {
        System.out.println("\nManos actuales de los jugadores:");  // Display current hands of players
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

    /**
     * Starts the game in console mode. It includes:
     * - Asking the player if they want to view the game instructions.
     * - Initializing the game state, such as the number of players, card distribution, and the first card.
     * - Displaying the game state including available cards and player hands.
     *
     * @throws MazoVacioException If the deck is empty during the game.
     */
    public void iniciarJuegoConsola() throws MazoVacioException {
        // Preguntar si desea ver las instrucciones
        Scanner scanner = new Scanner(System.in);
        System.out.println("¿Quieres ver las instrucciones del juego? (Sí/No): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();  // Get player's response

        // Muestra las instrucciones si el jugador responde "sí"
        if (respuesta.equals("sí") || respuesta.equals("si")) {
            modelo.ShowInstructions();  // Display instructions
        }

        solicitarCantidadJugadores();  // Ask for the number of players
        modelo.repartirCartas(cantidadJugadores);  // Distribute cards to players
        manosJugadores = modelo.getManosJugadores();  // Retrieve the players' hands

        // Take the first card from the deck and add it to the table
        CZ.Carta cartaInicial = modelo.tomarCartaDelMazo();
        cartasEnMesa.add(cartaInicial);
        sumaMesa = cartaInicial.getPuntos();  // Update the table's total points with the initial card

        System.out.println("Juego iniciado con " + cantidadJugadores + " jugadores.");
        System.out.println("Carta inicial en la mesa: " + cartaInicial);
        System.out.println("Cartas disponibles en el mazo: " + modelo.mostrarCartasDisponibles());
        mostrarManos();  // Show the current hands of all players

        jugar();  // Start the game loop
    }

    /**
     * Requests and validates the number of players for the game.
     * The valid range of players is between 2 and 4.
     * If the input is invalid, the default number of players is set to 2.
     */
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

    /**
     * Starts and runs the game loop.
     * The game continues as long as there are more than one player.
     * The method alternates between the human player and AI players.
     * When only one player remains, the game ends and the winner is announced.
     *
     * @throws MazoVacioException If the deck is empty during the game.
     */
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

    /**
     * Asks the player to choose the value of an Ace card, either 1 or 10 points.
     * It ensures the input is valid by accepting only 1 or 10.
     *
     * @return The value chosen for the Ace card (1 or 10).
     * @throws IndiceCartaInvalidoException If the input is invalid (not 1 or 10).
     */
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

    /**
     * Checks if the specified player has any valid moves available.
     * A valid move is when the player has a card that can be played based on the current sum on the table.
     *
     * @param jugador The index of the player to check.
     * @return true if the player has valid moves; false otherwise.
     */
    private boolean tieneMovimientosValidos(int jugador) {
        for (CZ.Carta carta : manosJugadores.get(jugador)) {
            if (modelo.puedeJugarCarta(carta, sumaMesa)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies the end conditions of the game.
     * If only one player remains, it declares the winner.
     * It also checks if the deck is empty and attempts to recycle the cards.
     * If the deck is still empty, the game ends in a draw.
     *
     * @throws MazoVacioException If there are no cards left in the deck.
     */
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

    /**
     * Executes the player's turn by playing a selected card.
     * The method ensures that the selected card is valid, updates the game state,
     * and checks for game-ending conditions after each turn.
     *
     * @param jugador The index of the player whose turn it is.
     * @param indiceCarta The index of the card selected by the player to play.
     * @throws MazoVacioException If there are no more cards in the deck.
     */
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

    /**
     * Asks the human player if they want to take a card from the deck.
     * If the player agrees, a new card is drawn from the deck and added to their hand.
     *
     * @param jugador The index of the player who is asked if they want to take a card.
     */
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

    /**
     * Executes the turn of an AI player.
     * The AI will think for 2 to 4 seconds, then attempt to play a valid card if possible.
     * If no valid cards are available, the AI player is eliminated.
     *
     * @param jugador The index of the AI player whose turn it is.
     * @throws MazoVacioException If there are no more cards in the deck.
     */
    private void turnoIA(int jugador) throws MazoVacioException {
        if (jugador >= manosJugadores.size()) {
            return;
        }

        if (!tieneMovimientosValidos(jugador)) {
            System.out.println("Jugador " + (jugador + 1) + " no tiene movimientos válidos. Eliminado.");
            modelo.DevolverCartasAlMazo(manosJugadores.get(jugador)); // Return cards to the deck
            manosJugadores.remove(jugador);
            verificarFinDelJuego();
            return;
        }

        List<CZ.Carta> mano = manosJugadores.get(jugador);

        // Display message that the AI is thinking
        System.out.println("Jugador " + (jugador + 1) + " (IA) está pensando...");

        // Simulate the AI's thinking time (2 to 4 seconds)
        try {
            Thread.sleep((int) (2000 + Math.random() * 2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if the AI can play any card
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

        // If no valid card is available, eliminate the player
        System.out.println("Jugador " + (jugador + 1) + " (IA) no tiene movimientos válidos. Eliminado.");
        modelo.DevolverCartasAlMazo(mano); // Return cards to the deck
        manosJugadores.remove(jugador);
        verificarFinDelJuego();
    }

    /**
     * Determines the value of an Ace card for the AI player based on the current sum and the number of cards in their hand.
     * The AI prefers to risk more when it has fewer cards.
     *
     * @param sumaActual The current sum of points on the table.
     * @param cantidadCartasMano The number of cards the AI player has in their hand.
     * @return The value the AI chooses for the Ace (1 or 10).
     */
    private int seleccionarValorAsIA(int sumaActual, int cantidadCartasMano) {
        // If the AI has more than 2 cards, it will be more cautious and avoid exceeding 50.
        if (cantidadCartasMano > 2) {
            if (sumaActual + 10 <= 50) {
                return 10; // Choose 10 if it doesn't exceed 50
            } else {
                return 1; // If it would exceed 50, choose 1
            }
        } else {
            // If the AI has fewer cards, it can afford to take more risks.
            if (sumaActual + 10 <= 50) {
                return 10; // Choose 10 if it doesn't exceed 50
            } else {
                // If the AI has fewer cards and the sum is high, it will prefer 1
                return 1;
            }
        }
    }
}

