package com.example.cincuentazo;

import com.example.cincuentazo.controller.CZController;
import com.example.cincuentazo.view.CZFirstStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    /**
     * The main method to launch the JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        CZController controller = new CZController();
        controller.iniciarJuegoConsola(); // Inicia el juego en la consola
    }

    /**
     * The start method is called when the application is launched.
     * It opens the first stage (window) of the Battleship game.
     *
     * @param primaryStage The primary stage provided by JavaFX, which can be used to set the main application window.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        new CZFirstStage();
    }
}