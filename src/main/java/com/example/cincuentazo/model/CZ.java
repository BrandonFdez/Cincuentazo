package com.example.cincuentazo.model;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CZ implements ICZ {

    public static class Carta {
        private final String palo;
        private final String valor;
        private final int puntos;

        public Carta(String palo, String valor, int puntos) {
            this.palo = palo;
            this.valor = valor;
            this.puntos = puntos;
        }

        public String getPalo() {
            return palo;
        }

        public String getValor() {
            return valor;
        }

        public int getPuntos() {
            return puntos;
        }

        @Override
        public String toString() {
            return valor + " de " + palo + " (" + puntos + " puntos)";
        }
    }

    private final List<Carta> mazo = new ArrayList<>();
    private final List<List<Carta>> manosJugadores = new ArrayList<>();

    public CZ() {
        crearMazo();
    }

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

    @Override
    public void barajarMazo() {
        Collections.shuffle(mazo);
    }

    @Override
    public void repartirCartas(int cantidadJugadores) {
        manosJugadores.clear();
        for (int i = 0; i < cantidadJugadores; i++) {
            List<Carta> mano = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                if (!mazo.isEmpty()) {
                    mano.add(mazo.remove(0));
                }
            }
            manosJugadores.add(mano);
        }
    }

    @Override
    public List<List<Carta>> getManosJugadores() {
        return manosJugadores;
    }

    @Override
    public Carta tomarCartaDelMazo() {
        if (!mazo.isEmpty()) {
            return mazo.remove(0);
        }
        return null;
    }

    @Override
    public void tomarCarta(int jugador) {
        if (!mazo.isEmpty() && jugador >= 0 && jugador < manosJugadores.size()) {
            Carta nuevaCarta = mazo.remove(0);
            manosJugadores.get(jugador).add(nuevaCarta);
        }
    }

    @Override
    public void reciclarCartas(List<Carta> cartasEnMesa) {
        if (mazo.isEmpty() && cartasEnMesa.size() > 1) {
            Carta ultimaCarta = cartasEnMesa.remove(cartasEnMesa.size() - 1);
            mazo.addAll(cartasEnMesa);
            barajarMazo();
            cartasEnMesa.clear();
            cartasEnMesa.add(ultimaCarta);

            mostrarAlerta("Mazo Reciclado", "Se han reciclado las cartas del montón para continuar el juego.", Alert.AlertType.INFORMATION);
        }
    }


    @Override
    public boolean puedeJugarCarta(Carta carta, int sumaActual) {
        if ("A".equals(carta.getValor())) {
            return sumaActual + 1 <= 50 || sumaActual + 11 <= 50;
        }
        return sumaActual + carta.getPuntos() <= 50;
    }

    @Override
    public int calcularPuntosCarta(Carta carta, int sumaActual) {
        if ("A".equals(carta.getValor())) {
            return (sumaActual + 11 <= 50) ? 11 : 1;
        }
        return carta.getPuntos();
    }

    @Override
    public void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(mensaje);
            alert.showAndWait();
        });
    }

    @Override
    public void DefeatAlert() {
        Platform.runLater(() -> {
            mostrarAlerta("Derrota", "¡Oh no! Has perdido.", Alert.AlertType.INFORMATION);
        });
    }

    @Override
    public void ErrorAlert() {
        Platform.runLater(() -> {
            mostrarAlerta("Error", "Acción inválida.", Alert.AlertType.ERROR);
        });
    }

    @Override
    public void WinAlert() {
        Platform.runLater(() -> {
            mostrarAlerta("Victoria", "¡Felicidades! Has ganado.", Alert.AlertType.INFORMATION);
        });
    }

    @Override
    public void ShowInstructions() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instrucciones");
        alert.setHeaderText("Instrucciones del juego");

        TextArea textArea = new TextArea();
        textArea.setText("""
                Regla principal: La suma de las cartas en la mesa no debe exceder 50.
                • Cada jugador recibe 4 cartas iniciales.
                • Cartas:
                  - 2-8 y 10 suman su valor.
                  - 9 no suma ni resta.
                  - J, Q, K restan 10.
                  - A suma 1 o 10 según convenga.
                • Turnos: Juega una carta o pasa si no puedes.
                • Ganador: El último jugador en pie gana.
                """);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        alert.getDialogPane().setContent(textArea);
        textArea.setPrefSize(400, 400);
        alert.showAndWait();
    }

    // Método para mostrar cartas disponibles
    @Override
    public int mostrarCartasDisponibles() {
        return mazo.size();
    }
}