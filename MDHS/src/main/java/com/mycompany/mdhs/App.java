package com.mycompany.mdhs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 * 
 * 
 * NOTE! This is the customer side of the application. I will create another file for the 
 * server side 
 */
public class App extends Application {

    private static Scene scene;

    /** 
     * 
     * @param stage
     * @throws IOException 
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Login"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }
    
    /** 
     * 
     * @param fxml
     * @throws IOException 
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    
    /** 
     * 
     * @param fxml
     * @return
     * @throws IOException 
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /** 
     * 
     * @param args 
     */
    public static void main(String[] args) {
        launch();
    }

}