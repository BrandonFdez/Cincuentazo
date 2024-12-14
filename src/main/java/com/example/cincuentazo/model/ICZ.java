package com.example.cincuentazo.model;

import javafx.scene.control.Alert;
import java.util.List;

/**
 * Interface representing the main actions and behaviors of the Cincuentazo game model.
 * The implementing class (e.g., CZ) will define how the methods are executed.
 */
public interface ICZ {

    /**
     * Creates a new deck of cards, initializing it with the standard set of cards for the game.
     */
    void crearMazo();

    /**
     * Shuffles the deck of cards to randomize the order of the cards.
     */
    void barajarMazo();

    /**
     * Distributes the cards to the players at the start of the game.
     * Each player will receive a specific number of cards.
     * @param cantidadJugadores The number of players participating in the game.
     */
    void repartirCartas(int cantidadJugadores);

    /**
     * Retrieves the hands of all players.
     * @return A list of lists, where each sublist represents the cards in a player's hand.
     */
    List<List<CZ.Carta>> getManosJugadores();

    /**
     * Draws a card from the deck and returns it to the player.
     * @return The card drawn from the deck, or null if the deck is empty.
     */
    CZ.Carta tomarCartaDelMazo();

    /**
     * Makes a player draw a card from the deck and adds it to their hand.
     * @param jugador The index of the player who is drawing the card.
     */
    void tomarCarta(int jugador);

    /**
     * Recyles the cards from the table back into the deck if the deck is empty.
     * Shuffles the deck and clears the table.
     * @param cartasEnMesa The cards currently on the table.
     */
    void reciclarCartas(List<CZ.Carta> cartasEnMesa);

    /**
     * Checks if a card can be played based on the current sum of points on the table.
     * @param carta The card the player wants to play.
     * @param sumaActual The current sum of points on the table.
     * @return True if the card can be played, false otherwise.
     */
    boolean puedeJugarCarta(CZ.Carta carta, int sumaActual);

    /**
     * Calculates the points of a card considering the current sum on the table.
     * @param carta The card whose points are to be calculated.
     * @param sumaActual The current sum of points on the table.
     * @return The points of the card.
     */
    int calcularPuntosCarta(CZ.Carta carta, int sumaActual);

    /**
     * Displays an alert with a message to the user.
     * @param titulo The title of the alert.
     * @param mensaje The message to display in the alert.
     * @param tipo The type of alert (information, error, etc.).
     */
    void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo);

    /**
     * Displays an alert notifying the player of their defeat.
     */
    void DefeatAlert();

    /**
     * Displays an alert notifying the player of an invalid action.
     */
    void ErrorAlert();

    /**
     * Displays an alert notifying the player of their victory.
     */
    void WinAlert();

    /**
     * Shows the instructions for the game in an alert.
     */
    void ShowInstructions();

    /**
     * Returns the cards of a player to the deck after they are eliminated.
     * @param cartas The cards to be returned to the deck.
     */
    void DevolverCartasAlMazo(List<CZ.Carta> cartas);

    /**
     * Returns the number of cards remaining in the deck.
     * @return The number of cards available in the deck.
     */
    int mostrarCartasDisponibles();

    /**
     * Removes a player from the game and recycles their cards back into the deck.
     * @param jugadorIndex The index of the player to be removed.
     * @param cartasEnMesa The cards that the player has, which will be added back to the deck.
     */
    void eliminarJugador(int jugadorIndex, List<CZ.Carta> cartasEnMesa);
}
