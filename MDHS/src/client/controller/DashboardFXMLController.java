package client.controller;

import client.*;
import common.model.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author Brodie Lucht 
 */
public class DashboardFXMLController implements Initializable, SceneController {

    @FXML
    private Button exitButton;
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
    @FXML
    private Button loginButton;
    @FXML
    private Button logoutButton;
    
    private Session session; 
    
    /**
     * When transitioning to the scene, will hide or show specific buttons depending 
     * on if the Account logged in is an Admin, or is a Customer. 
     */
    @Override
    public void handleSceneChange() {
        session = Session.getSession();
        Account user = session.getUser();
        
        if (user instanceof Customer) {
            loginButton.setDisable(true);
            registerButton.setDisable(true);
            logoutButton.setDisable(false);
            viewScheduleButton.setDisable(false);
            viewProductsButton.setDisable(false);
            placeOrderButton.setDisable(false);
            manageProductsButton.setDisable(true);
            manageScheduleButton.setDisable(true);
            customerRecordsButton.setDisable(true);
            orderRecordsButton.setDisable(true);
        } else if (user instanceof Administrator) {
            loginButton.setDisable(true);
            registerButton.setDisable(true);
            logoutButton.setDisable(false);
            viewScheduleButton.setDisable(true);
            viewProductsButton.setDisable(true);
            placeOrderButton.setDisable(true);
            manageProductsButton.setDisable(false);
            manageScheduleButton.setDisable(false);
            customerRecordsButton.setDisable(false);
            orderRecordsButton.setDisable(false);
        } else {
            loginButton.setDisable(false);
            registerButton.setDisable(false);
            logoutButton.setDisable(true);
            viewScheduleButton.setDisable(true);
            viewProductsButton.setDisable(true);
            placeOrderButton.setDisable(true);
            manageProductsButton.setDisable(true);
            manageScheduleButton.setDisable(true);
            customerRecordsButton.setDisable(true);
            orderRecordsButton.setDisable(true);
        }
    }
    
    /**
     * Initializes the controller class.
     * 
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Disable all but login buttons
        loginButton.setDisable(false);
        registerButton.setDisable(false);
        logoutButton.setDisable(true);
        viewScheduleButton.setDisable(true);
        viewProductsButton.setDisable(true);
        placeOrderButton.setDisable(true);
        manageProductsButton.setDisable(true);
        manageScheduleButton.setDisable(true);
        customerRecordsButton.setDisable(true);
        orderRecordsButton.setDisable(true);
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

    @FXML
    private void logoutButtonHandler(ActionEvent event) {
        Session currentSession = Session.getSession();
        currentSession.setUser(null);
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
}
