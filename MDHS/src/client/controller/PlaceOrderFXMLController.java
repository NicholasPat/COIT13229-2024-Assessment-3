
package client.controller;

import client.MDHSClient;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
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
    private TextField totalCostTextField;
    @FXML
    private Button cancelOrderButton;
    @FXML
    private TextField productPriceTextField;
    @FXML
    private ComboBox<?> hourComboBox;
    @FXML
    private ComboBox<?> minuteComboBox;
    @FXML
    private TextField deliveryDayTextField;
    @FXML
    private ChoiceBox<?> productChoiceBox;

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
