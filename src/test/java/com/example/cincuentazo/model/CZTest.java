package com.example.cincuentazo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class CZTest {

    CZ juego;

    @BeforeEach
    void setUp() {
        juego = new CZ(); // Se inicializa una nueva instancia de CZ antes de cada prueba
        juego.crearMazo(); // Asegúrate de que el mazo esté creado antes de cada prueba
    }

    @Test
    void crearMazo() {
        // Verificar que el mazo se ha creado correctamente
        assertTrue(juego.mostrarCartasDisponibles() > 0, "El mazo no está vacío");
    }

    @Test
    void repartirCartas() {
        juego.repartirCartas(3); // Repartir cartas a 3 jugadores
        List<List<CZ.Carta>> manos = juego.getManosJugadores();

        // Verificar que cada jugador tiene 4 cartas
        for (List<CZ.Carta> mano : manos) {
            assertEquals(4, mano.size(), "El jugador no tiene 4 cartas");
        }
    }

    @Test
    void calcularPuntosCarta() {
        CZ.Carta cartaA = new CZ.Carta("Corazones", "A", 1);
        CZ.Carta carta7 = new CZ.Carta("Tréboles", "7", 7);

        // Verificar que el cálculo de puntos es correcto
        assertEquals(10, juego.calcularPuntosCarta(cartaA, 40), "La carta 'A' debería sumar 10 en suma 40");
        assertEquals(1, juego.calcularPuntosCarta(cartaA, 49), "La carta 'A' debería sumar 1 en suma 49");
        assertEquals(7, juego.calcularPuntosCarta(carta7, 30), "La carta '7' debería sumar 7 en suma 30");
    }

    @Test
    void tomarCartaDelMazo() {
        // Verificar que se pueda tomar una carta del mazo
        int cartasIniciales = juego.mostrarCartasDisponibles();
        juego.tomarCartaDelMazo();
        assertEquals(cartasIniciales - 1, juego.mostrarCartasDisponibles(), "No se ha reducido el número de cartas del mazo correctamente");
    }

    @Test
    void reciclarCartas() {
        // Simular que se reciclan cartas
        List<CZ.Carta> cartasEnMesa = List.of(new CZ.Carta("Diamantes", "2", 2), new CZ.Carta("Corazones", "3", 3));
        juego.reciclarCartas(cartasEnMesa);

        // Verificar que el mazo ha sido reciclado
        assertTrue(juego.mostrarCartasDisponibles() > 0, "Las cartas no fueron recicladas correctamente");
    }

    @Test
    void puedeJugarCarta() {
        CZ.Carta carta1 = new CZ.Carta("Corazones", "7", 7);
        CZ.Carta carta2 = new CZ.Carta("Picas", "Q", -10);
        CZ.Carta cartaA = new CZ.Carta("Diamantes", "A", 1);

        // Casos donde la carta se puede jugar
        assertTrue(juego.puedeJugarCarta(carta1, 40), "Carta '7' debería ser jugable en suma 40");
        assertTrue(juego.puedeJugarCarta(carta2, 49), "Carta 'Q' debería ser jugable en suma 49");
        assertTrue(juego.puedeJugarCarta(cartaA, 49), "Carta 'A' debería ser jugable en suma 49 (como 1)");

        // Casos donde la carta no se puede jugar
        assertFalse(juego.puedeJugarCarta(carta1, 44), "Carta '7' no debería ser jugable en suma 44");
        assertFalse(juego.puedeJugarCarta(carta2, 50), "Carta 'Q' no debería ser jugable en suma 50");
    }
}
