package com.example.cincuentazo.controller;

import com.example.cincuentazo.model.CZ;
import com.example.cincuentazo.exception.JuegoTerminadoException;
import com.example.cincuentazo.exception.JugadorEliminadoException;
import java.util.concurrent.*;

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
    private List<Integer> jugadoresActivos;

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

        jugadoresActivos = new ArrayList<>();
        for (int i = 0; i < cantidadJugadores; i++) {
            jugadoresActivos.add(i); // Agregar cada jugador a la lista de jugadores activos
        }

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

    public class JugadorHumanoThread extends Thread {
        private CZController controller;

        public JugadorHumanoThread(CZController controller) {
            this.controller = controller;
        }

        @Override
        public void run() {
            // Esta lógica maneja el turno del jugador humano
            controller.turnoJugadorHumano();
        }
    }

    public void turnoJugadorHumano() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Selecciona el índice de la carta que deseas jugar: ");

        // Esperar a que el jugador ingrese un índice válido
        while (true) {
            try {
                int indiceCarta = Integer.parseInt(scanner.nextLine());
                if (indiceCarta >= 0 && indiceCarta < manosJugadores.get(0).size()) {
                    turnoJugador(0, indiceCarta); // Llamar al método turnoJugador para realizar la jugada
                    break; // Salir del bucle una vez que la jugada sea válida
                } else {
                    System.out.println("Índice inválido. Intenta nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Intenta nuevamente.");
            }
        }
    }

    public class JugadorIAThread extends Thread {
        private CZController controller;
        private int jugador;

        public JugadorIAThread(CZController controller, int jugador) {
            this.controller = controller;
            this.jugador = jugador;
        }

        @Override
        public void run() {
            // Esta lógica maneja el turno de la IA completamente dentro del hilo
            controller.turnoIA(jugador); // Se asume que `turnoIA` maneja todo el flujo del turno del jugador IA
        }
    }

    public void jugar() {
        Scanner scanner = new Scanner(System.in);
        int turno = 0;

        // ExecutorService para manejar los hilos de los jugadores
        ExecutorService executor = Executors.newFixedThreadPool(2);

        while (jugadoresActivos.size() > 1) { // El juego continúa mientras haya más de un jugador
            jugadorActual = jugadoresActivos.get(turno % jugadoresActivos.size()); // Jugador actual

            // Verificar si el jugador actual está activo
            if (jugadorActual >= manosJugadores.size() || !jugadoresActivos.contains(jugadorActual)) {
                turno++; // Si el jugador ya no está activo, pasamos al siguiente turno
                continue;
            }

            // Usamos un CountDownLatch para sincronizar la espera entre los hilos
            CountDownLatch latch = new CountDownLatch(1); // Se usa para esperar a que ambos hilos terminen su ejecución

            if (jugadorActual == 0) {
                // Turno del jugador humano
                System.out.println("\nTu turno. Suma actual en la mesa: " + sumaMesa);
                System.out.println("Cartas disponibles en el mazo: " + modelo.mostrarCartasDisponibles());
                System.out.println("Tu mano: " + manosJugadores.get(0));

                // Crear y ejecutar el hilo para el jugador humano
                executor.submit(() -> {
                    turnoJugadorHumano();
                    latch.countDown(); // Indica que el hilo ha terminado
                });
            } else {
                // Turno de la IA
                System.out.println("\n===============================");
                System.out.println("Turno del jugador " + (jugadorActual + 1));
                System.out.println("===============================\n");

                // Crear y ejecutar el hilo para el jugador IA
                executor.submit(() -> {
                    new JugadorIAThread(this, jugadorActual).run();
                    latch.countDown(); // Indica que el hilo ha terminado
                });
            }

            // Esperar a que ambos hilos terminen antes de continuar con el siguiente turno
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            turno++; // Pasar al siguiente turno
        }

        // Apagar el executor después de que termine el juego
        executor.shutdown();
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
     * @param jugador indice del jugador en la lista de manos.
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
            eliminarJugador(jugador); // Llamar al método para manejar la eliminación
            verificarFinDelJuego();
            return;
        }

        CZ.Carta cartaJugada = manosJugadores.get(jugador).get(indiceCarta);

        // Opción para tomar una carta del mazo antes de jugar una carta
        preguntarSiTomarCarta(jugador);

        // Si la carta es un As, pedir el valor solo si es el turno del jugador humano
        if ("A".equals(cartaJugada.getValor())) {
            if (jugador == 0) { // Solo pedir al jugador humano (jugador 0)
                int valorAs = pedirValorAs(); // Pedir el valor del As al jugador
                cartaJugada = new CZ.Carta(cartaJugada.getPalo(), "A", valorAs); // Actualizar el valor de la carta "A"
            } else {
                // Lógica para la IA (selección automática del valor del As)
                int valorAs = seleccionarValorAsIA(sumaMesa, manosJugadores.get(jugador).size());
                cartaJugada = new CZ.Carta(cartaJugada.getPalo(), "A", valorAs); // Actualizar el valor de la carta "A"
            }
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
            jugadoresActivos.remove(Integer.valueOf(jugador)); // Eliminar al jugador de la lista activa
            verificarFinDelJuego(); // Verificar si queda un solo jugador
            return; // Finalizar turno si el jugador pierde
        }

        modelo.tomarCarta(jugador);
        mostrarManos();
        verificarFinDelJuego(); // Verificar si queda un solo jugador
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
        if (jugadoresActivos.size() == 1) {
            int ganador = jugadoresActivos.get(0);
            System.out.println("¡El jugador " + (ganador + 1) + " es el ganador!");
            modelo.WinAlert();
            System.exit(0);
        }

        if (manosJugadores.size() == 1) {
            System.out.println("¡El jugador " + (manosJugadores.get(0).equals(manosJugadores.get(0)) ? 1 : 2) + " es el ganador!");
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

    private void eliminarJugador(int jugador) {
        System.out.println("Jugador " + (jugador + 1) + " eliminado.");
        manosJugadores.remove(jugador);  // Eliminar las cartas del jugador

        // Eliminar de la lista de jugadores activos
        jugadoresActivos.remove(Integer.valueOf(jugador));

        // Actualizar las manos después de eliminar al jugador
        for (int i = jugador; i < manosJugadores.size(); i++) {
            // Desplazar a los jugadores hacia la izquierda en la lista de manos
            List<CZ.Carta> mano = manosJugadores.get(i);
            manosJugadores.set(i, mano);
        }
    }

    public void turnoIA(int jugador) {
        if (jugador >= manosJugadores.size()) {
            return;
        }

        // Verificar movimientos válidos ANTES de intentar jugar
        if (!tieneMovimientosValidos(jugador)) {
            System.out.println("Jugador " + (jugador + 1) + " no tiene movimientos válidos. Eliminado.");
            eliminarJugador(jugador); // Llamar al método para manejar la eliminación
            verificarFinDelJuego();
            return;
        }

        List<CZ.Carta> mano = manosJugadores.get(jugador);
        for (int i = 0; i < mano.size(); i++) {
            CZ.Carta carta = mano.get(i);
            if (modelo.puedeJugarCarta(carta, sumaMesa)) {
                // Si la carta es un As, la IA decide el valor automáticamente
                if ("A".equals(carta.getValor())) {
                    // Decidir el valor del As según la lógica de la IA
                    int puntosAs = seleccionarValorAsIA(sumaMesa, mano.size());
                    // Reemplazar la carta directamente con el valor elegido
                    mano.set(i, new CZ.Carta(carta.getPalo(), carta.getValor(), puntosAs));
                }

                // Realizar la jugada de la IA sin mostrar sus cartas
                turnoJugador(jugador, i);
                return;
            }
        }
    }

    // Método que permite a la IA seleccionar el valor de un As (1 o 10) de manera más inteligente
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