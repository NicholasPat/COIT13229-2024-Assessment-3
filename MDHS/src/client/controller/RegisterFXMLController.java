
package client.controller;

import client.MDHSClient;
import client.Session;
import common.Authenticator;
import common.model.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author lucht
 */
public class RegisterFXMLController implements Initializable, SceneController {

    @FXML
    private AnchorPane contentAnchorPane;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField phonenumberTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private CheckBox adminCheckbox;
    @FXML
    private Button registerButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button dashboardButton;
    @FXML
    private TextField postcodeTextField;

    @Override
    public void handleSceneChange() {
        clear();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void registerButtonHandler(ActionEvent event) {
        Session session = Session.getSession();

       
        String fName = firstNameTextField.getText().trim();
        String lName = lastNameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String pass = passwordTextField.getText().trim();
        String phone = phonenumberTextField.getText().trim();
        String addr = addressTextField.getText().trim();
        String postcode = postcodeTextField.getText().trim();
        Boolean isAdmin = adminCheckbox.isSelected();
        

        try {
            byte[] password = Authenticator.encrypt(session.getPublicKey(), pass);
            Account acc = null;
            if (isAdmin) {
                acc = new Administrator(isAdmin, fName, lName, email, password);
            } else {
                acc = new Customer(Integer.parseInt(phone), addr, Integer.parseInt(postcode), fName, lName, email, password);
            }
            
            session.objOut.writeObject("Register");
            session.objOut.writeObject(acc); // send account info to server
            
            // wait for server response with logged-in account
            Object response = session.objIn.readObject();

            if (response instanceof Account) {
                Account user = (Account) response;
                System.out.println("Registration successful: " + user.getEmailAddress());
                session.setUser(user);  // login (set user to session) & return to dashboard
                MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
            } else if (response == null) {
                System.out.println("Registration Failed.");
                session.setUser(null);
            } else {
                System.out.println("Unexpected response: " + response.getClass());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception during login: " + ex.getMessage());
            session.setUser(null);
        }
    }

    @FXML
    private void clearButtonHandler(ActionEvent event) {
        clear();
    }

    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    private void clear() {
        firstNameTextField.clear();
        lastNameTextField.clear();
        emailTextField.clear();
        passwordTextField.clear();
        phonenumberTextField.clear();
        addressTextField.clear();
        adminCheckbox.setSelected(false);
        postcodeTextField.clear();
    }
    
}
