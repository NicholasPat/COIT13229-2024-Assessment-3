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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author lucht
 */
public class ManageScheduleFXMLController implements Initializable, SceneController {

    @FXML
    private AnchorPane contentAnchorPane;
    @FXML
    private Button dashboardButton;
    @FXML
    private TextField postcodeTextField;
    @FXML
    private TextField costTextField;
    @FXML
    private ChoiceBox<?> deliveryDayChoiceBox;
    @FXML
    private Button previousIndexButton;
    @FXML
    private Button nextIndexButton;
    @FXML
    private TextField currentIndexTextField;
    @FXML
    private TextField totalIndexTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button newButton;

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
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }

    @FXML
    private void previousIndexButtonHandler(ActionEvent event) {
    }

    @FXML
    private void nextIndexButtonHandler(ActionEvent event) {
    }

    @FXML
    private void addButtonHandler(ActionEvent event) {
    }

    @FXML
    private void updateButtonHandler(ActionEvent event) {
    }

    @FXML
    private void deleteButtonHandler(ActionEvent event) {
    }

    @FXML
    private void searchButtonHandler(ActionEvent event) {
    }

    @FXML
    private void newButtonHandler(ActionEvent event) {
    }
    
}
