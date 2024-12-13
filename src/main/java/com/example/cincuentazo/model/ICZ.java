package com.example.cincuentazo.model;

import javafx.scene.control.Alert;

import java.util.List;

public interface ICZ {
    void crearMazo();
    void barajarMazo();
    void repartirCartas(int cantidadJugadores);
    List<List<CZ.Carta>> getManosJugadores();
    CZ.Carta tomarCartaDelMazo();
    void tomarCarta(int jugador);
    void reciclarCartas(List<CZ.Carta> cartasEnMesa);
    boolean puedeJugarCarta(CZ.Carta carta, int sumaActual);
    int calcularPuntosCarta(CZ.Carta carta, int sumaActual);
    void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo);
    void DefeatAlert();
    void ErrorAlert();
    void WinAlert();
    void ShowInstructions();
    void DevolverCartasAlMazo(List<CZ.Carta> cartas);
    int mostrarCartasDisponibles();

    // Método que debes agregar
    void eliminarJugador(int jugadorIndex, List<CZ.Carta> cartasEnMesa);
}
}