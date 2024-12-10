package com.example.cincuentazo.view;

import com.example.cincuentazo.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CZFirstStage extends Stage {
    public CZFirstStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        setTitle("Hello!");
        setScene(scene);
        show();
    }
}
