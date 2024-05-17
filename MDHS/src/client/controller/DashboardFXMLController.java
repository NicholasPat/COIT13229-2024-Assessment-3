/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package client.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author lucht
 */
public class DashboardFXMLController implements Initializable, SceneController {

    @FXML
    private Button exitButton;
    @FXML
    private Button loginButtton;
    @FXML
    private Button registerButton;
    @FXML
    private Button viewScheduleButton;
    @FXML
    private Button viewProductsButton;
    @FXML
    private Button placeOrderButton;
    @FXML
    private Button manageProductsButton;
    @FXML
    private Button manageScheduleButton;
    @FXML
    private Button customerRecordsButton;
    @FXML
    private Button orderRecordsButton;

    @Override
    public void handleSceneChange() {
        // TODO
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void exitButtonHandler(ActionEvent event) {
    }

    @FXML
    private void loginButtonHandler(ActionEvent event) {
    }

    @FXML
    private void registerButtonHandler(ActionEvent event) {
    }

    @FXML
    private void viewScheduleButtonHandler(ActionEvent event) {
    }

    @FXML
    private void viewProductsButtonHandler(ActionEvent event) {
    }

    @FXML
    private void placeOrderButtonHandler(ActionEvent event) {
    }

    @FXML
    private void manageProductsButtonHandler(ActionEvent event) {
    }

    @FXML
    private void manageScheduleButtonHandler(ActionEvent event) {
    }

    @FXML
    private void customerRecordsButtonHandler(ActionEvent event) {
    }

    @FXML
    private void orderRecordsButtonHandler(ActionEvent event) {
    }
    
}
