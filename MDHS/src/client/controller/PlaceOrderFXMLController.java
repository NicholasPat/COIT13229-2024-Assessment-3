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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author lucht
 */
public class PlaceOrderFXMLController implements Initializable, SceneController {

    @FXML
    private AnchorPane contentAnchorPane;
    @FXML
    private Button dashboardButton;
    @FXML
    private ChoiceBox<?> productChoiseBox;
    @FXML
    private TextField quantityTextField;
    @FXML
    private Button previousOrderItemButton;
    @FXML
    private Button nextOrderItemButton;
    @FXML
    private TextField currentOrderItemTextField;
    @FXML
    private TextField totalOrderItemTextField;
    @FXML
    private Button addOrderItemButton;
    @FXML
    private Button removeOrderItemButton;
    @FXML
    private Button placeOrderButton;
    @FXML
    private TextField subtotalTextField;
    @FXML
    private TextField deliveryCostTextField;
    @FXML
    private TextField taxTextField;
    @FXML
    private TextField deliveryTimeTextField;
    @FXML
    private TextField totalCostTextField;
    @FXML
    private Button cancelOrderButton;

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
    private void dashboardButtonHandler(ActionEvent event) {
    }

    @FXML
    private void previousOrderItemButtonHandler(ActionEvent event) {
    }

    @FXML
    private void nextOrderItemButtonHandler(ActionEvent event) {
    }

    @FXML
    private void addOrderItemButtonHandler(ActionEvent event) {
    }

    @FXML
    private void removeOrderItemButtonHandler(ActionEvent event) {
    }

    @FXML
    private void placeOrderButtonHandler(ActionEvent event) {
    }

    @FXML
    private void cancelOrderButtonHandler(ActionEvent event) {
    }
    
}
