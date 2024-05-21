/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package client.controller;

import client.MDHSClient;
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
        System.exit(0);
    }

    @FXML
    private void loginButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.LOGIN);
    }

    @FXML
    private void registerButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.REGISTER);
    }

    @FXML
    private void viewScheduleButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.VIEW_SCHEDULE);
    }

    @FXML
    private void viewProductsButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.VIEW_PRODUCTS);
    }

    @FXML
    private void placeOrderButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.PLACE_ORDER);
    }

    @FXML
    private void manageProductsButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.MANAGE_PRODUCTS);
    }

    @FXML
    private void manageScheduleButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.MANAGE_SCHEDULE);
    }

    @FXML
    private void customerRecordsButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.VIEW_CUSTOMERS);
    }

    @FXML
    private void orderRecordsButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.VIEW_ORDERS);
    }
    
}
