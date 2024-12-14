package com.example.cincuentazo.model;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The CZ class implements the ICZ interface and represents the game model for the card game.
 * It contains the game logic, including the creation of the deck, managing the players' hands,
 * and the internal representation of cards and players.
 */
public class CZ implements ICZ {

    /**
     * Represents a card in the deck with a suit, value, and points.
     */
    public static class Carta {
        private final String palo;  // The suit of the card (e.g., Hearts, Spades)
        private final String valor; // The value of the card (e.g., "A" for Ace, "2", "3", etc.)
        private final int puntos;   // The points the card represents in the game

        /**
         * Constructs a new Carta object.
         *
         * @param palo   The suit of the card.
         * @param valor  The value of the card.
         * @param puntos The points of the card.
         */
        public Carta(String palo, String valor, int puntos) {
            this.palo = palo;
            this.valor = valor;
            this.puntos = puntos;
        }

        // Getters for the card's attributes
        public String getPalo() { return palo; }
        public String getValor() { return valor; }
        public int getPuntos() { return puntos; }

        /**
         * Returns a string representation of the card.
         *
         * @return The card's value, suit, and points.
         */
        @Override
        public String toString() {
            return valor + " de " + palo + " (" + puntos + " puntos)";
        }
    }

    /**
     * Represents a player in the game, either a human or AI player.
     */
    public static class Jugador {
        private final String nombre; // The name of the player
        private final String tipo;   // The type of the player (e.g., "Humano" or "IA")
        private final List<Carta> mano; // The player's hand (a list of cards)

        /**
         * Constructs a new Jugador object.
         *
         * @param nombre The player's name.
         * @param tipo   The type of the player (either "Humano" or "IA").
         * @param mano   The player's hand (a list of cards).
         */
        public Jugador(String nombre, String tipo, List<Carta> mano) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.mano = mano;
        }

        // Getter for the player's name and hand
        public String getNombre() { return nombre; }
        public List<Carta> getMano() { return mano; }

        /**
         * Checks if the player is a human player.
         *
         * @return True if the player is human, false if the player is AI.
         */
        public boolean esHumano() { return "Humano".equals(tipo); }

        /**
         * Returns a string representation of the player.
         *
         * @return The player's name and hand, with special formatting for human players.
         */
        @Override
        public String toString() {
            return esHumano() ? nombre + " (Tú): " + mano + "\n" : nombre + " (IA): ***\n";
        }
    }

    private final List<Carta> mazo = new ArrayList<>(); // The deck of cards
    private final List<List<Carta>> manosJugadores = new ArrayList<>(); // The hands of all players

    /**
     * Constructs a new CZ game and initializes the deck of cards.
     */
    public CZ() {
        crearMazo();
    }

    /**
     * Creates the deck of cards, initializing each card with a suit, value, and points.
     */
    @Override
    public void crearMazo() {
        String[] palos = {"Tréboles", "Diamantes", "Picas", "Corazones"};
        String[] valores = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        int[] puntos = {2, 3, 4, 5, 6, 7, 8, 0, 10, -10, -10, -10, 1};

        for (String palo : palos) {
            for (int i = 0; i < valores.length; i++) {
                mazo.add(new Carta(palo, valores[i], puntos[i]));
            }
        }
        barajarMazo();
    }

    /**
     * The CZ class implements the game logic for managing cards and players.
     * The methods below handle the deck's shuffling, card dealing, recycling of cards, and player elimination.
     */
    @Override
    public void barajarMazo() {
        // Shuffle the deck to randomize the order of the cards
        Collections.shuffle(mazo);
    }

    /**
     * Deals 4 cards to each player, based on the number of players in the game.
     *
     * @param cantidadJugadores The number of players to deal cards to.
     */
    @Override
    public void repartirCartas(int cantidadJugadores) {
        // Clear any existing hands of the players
        manosJugadores.clear();

        // Deal 4 cards to each player
        for (int i = 0; i < cantidadJugadores; i++) {
            List<Carta> mano = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                // If there are cards remaining in the deck, deal one to the player
                if (!mazo.isEmpty()) {
                    mano.add(mazo.remove(0));
                }
            }
            // Add the player's hand to the list of player hands
            manosJugadores.add(mano);
        }
    }

    /**
     * Gets the hands of all players.
     *
     * @return A list of lists, where each inner list represents the hand of a player.
     */
    @Override
    public List<List<Carta>> getManosJugadores() {
        return manosJugadores;
    }

    /**
     * Takes a card from the deck and returns it.
     *
     * @return The card removed from the deck, or null if the deck is empty.
     */
    @Override
    public Carta tomarCartaDelMazo() {
        // Return the top card from the deck if it's not empty, otherwise return null
        return !mazo.isEmpty() ? mazo.remove(0) : null;
    }

    /**
     * Takes a card from the deck and gives it to the specified player, if possible.
     *
     * @param jugador The index of the player who is taking a card.
     */
    @Override
    public void tomarCarta(int jugador) {
        // Check if the player exists and has less than 4 cards in hand
        if (!mazo.isEmpty() &&
                jugador >= 0 &&
                jugador < manosJugadores.size() &&
                manosJugadores.get(jugador).size() < 4) {
            // Take the top card from the deck and add it to the player's hand
            Carta nuevaCarta = mazo.remove(0);
            manosJugadores.get(jugador).add(nuevaCarta);
        }
    }

    /**
     * Recycles the cards from the table back into the deck, shuffling them if necessary.
     *
     * @param cartasEnMesa The list of cards on the table.
     */
    @Override
    public void reciclarCartas(List<Carta> cartasEnMesa) {
        // If the deck is empty and there are cards on the table, recycle them
        if (mazo.isEmpty() && cartasEnMesa.size() > 1) {
            // Remove the last card from the table to keep it out of the recycled pile
            Carta ultimaCarta = cartasEnMesa.remove(cartasEnMesa.size() - 1);

            // Add all remaining cards from the table to the deck
            mazo.addAll(cartasEnMesa);

            // Shuffle the deck to randomize the order of the recycled cards
            barajarMazo();

            // Clear the table and add back the last card
            cartasEnMesa.clear();
            cartasEnMesa.add(ultimaCarta);

            // Notify the players that the deck has been recycled
            mostrarAlerta("Mazo Reciclado", "Se han reciclado las cartas del montón para continuar el juego.", Alert.AlertType.INFORMATION);
        }
    }

    /**
     * Eliminates a player from the game, recycling their cards back into the deck.
     *
     * @param jugadorIndex The index of the player to eliminate.
     * @param cartasEnMesa The list of cards on the table, used for recycling.
     */
    @Override
    public void eliminarJugador(int jugadorIndex, List<Carta> cartasEnMesa) {
        // Check if the player exists
        if (jugadorIndex >= 0 && jugadorIndex < manosJugadores.size()) {
            // Get the player's cards and remove them from the player's hand
            List<Carta> cartasEliminadas = manosJugadores.get(jugadorIndex);
            manosJugadores.remove(jugadorIndex);

            // Add the eliminated player's cards back into the deck
            mazo.addAll(cartasEliminadas);

            // Shuffle the deck to randomize the order of the recycled cards
            barajarMazo();

            // Notify the players that a player has been eliminated
            mostrarAlerta("Jugador Eliminado", "Las cartas del jugador eliminado han sido recicladas.", Alert.AlertType.INFORMATION);
        }
    }

    /**
     * The methods below implement various game functionalities, such as checking if a card can be played,
     * calculating the points of a card, showing alerts, and handling player actions and game states.
     */
    @Override
    public boolean puedeJugarCarta(Carta carta, int sumaActual) {
        // Check if the card can be played without exceeding 50 points
        if ("A".equals(carta.getValor())) {
            // If the card is an Ace, it can add 1 or 10 points, depending on the current sum
            return sumaActual + 1 <= 50 || sumaActual + 10 <= 50;
        }
        // For other cards, just check if the sum of the card's points doesn't exceed 50
        return sumaActual + carta.getPuntos() <= 50;
    }

    /**
     * Calculates the points of a card considering the current sum of the table.
     *
     * @param carta The card whose points are to be calculated.
     * @param sumaActual The current sum of the points on the table.
     * @return The points of the card, considering the current sum and the value of the card.
     */
    @Override
    public int calcularPuntosCarta(Carta carta, int sumaActual) {
        // If the card is an Ace, decide whether it should add 1 or 10 points based on the current sum
        return "A".equals(carta.getValor()) ? (sumaActual + 10 <= 50 ? 10 : 1) : carta.getPuntos();
    }

    /**
     * Displays an alert to the user with a custom message and title.
     *
     * @param titulo The title of the alert.
     * @param mensaje The message displayed in the alert.
     * @param tipo The type of alert (INFORMATION, ERROR, etc.).
     */
    @Override
    public void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(mensaje);
            alert.showAndWait();
        });
    }

    /**
     * Shows an alert indicating that the player has lost the game.
     */
    @Override
    public void DefeatAlert() {
        mostrarAlerta("Derrota", "¡Oh no! Has perdido.", Alert.AlertType.INFORMATION);
    }

    /**
     * Shows an alert indicating that there has been an error with an invalid action.
     */
    @Override
    public void ErrorAlert() {
        mostrarAlerta("Error", "Acción inválida.", Alert.AlertType.ERROR);
    }

    /**
     * Shows an alert indicating that the player has won the game.
     */
    @Override
    public void WinAlert() {
        mostrarAlerta("Victoria", "¡Felicidades! Has ganado.", Alert.AlertType.INFORMATION);
    }

    /**
     * Displays the game instructions in a new alert window.
     */
    @Override
    public void ShowInstructions() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Instrucciones");
            alert.setHeaderText("Instrucciones del juego");
            TextArea textArea = new TextArea("Regla principal: La suma de las cartas en la mesa no debe exceder 50.\n" +
                    "• Cada jugador recibe 4 cartas iniciales.\n" +
                    "• Cartas: 2-8 y 10 suman su valor. 9 no suma ni resta. J, Q, K restan 10. A suma 1 o 10 según convenga.\n" +
                    "• Ganador: El último jugador en pie gana.");
            textArea.setWrapText(true);
            textArea.setEditable(false);
            alert.getDialogPane().setContent(textArea);
            textArea.setPrefSize(400, 400);
            alert.showAndWait();
        });
    }

    /**
     * Returns the cards of a player back to the deck and shuffles it.
     *
     * @param cartas The list of cards to be returned to the deck.
     */
    @Override
    public void DevolverCartasAlMazo(List<Carta> cartas) {
        mazo.addAll(cartas);
        barajarMazo();
    }

    /**
     * Returns the number of available cards in the deck.
     *
     * @return The number of cards remaining in the deck.
     */
    @Override
    public int mostrarCartasDisponibles() {
        return mazo.size();
    }
}