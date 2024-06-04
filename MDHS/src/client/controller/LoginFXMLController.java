package client.controller;

import client.*;
import common.Authenticator;
import common.UserInputException;
import common.Utility;
import common.model.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
 */
public class LoginFXMLController implements Initializable, SceneController {

    @FXML
    private Button backButton;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    
    /** 
     * Upon the scene being changed to this page, set text fields to empty
     */
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
    
    /** 
     * On click, returns to the dashboard. 
     * 
     * @param event 
     */
    @FXML
    private void backButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    /** 
     * Attempts to login. Sends information entered to the server which checks the 
     * password in the database. Upon a successful login, will change scene to 
     * the Dashboard and set the user as a Customer or Admin. 
     * 
     * @param event 
     */
    @FXML
    private void loginButtonHandler(ActionEvent event) {
        Session session = Session.getSession();
        String email = emailTextField.getText().trim();
        String pass = passwordTextField.getText().trim();
        
        try {
            byte[] password = Authenticator.encrypt(session.getPublicKey(), pass);
            Account usr = new Account(email, password);

            session.objOut.writeObject("Login");
            session.objOut.writeObject(usr); // send account info to server

            // wait for server response with logged-in account
            Object response = session.objIn.readObject();

            if (response instanceof Account) {
                Account user = (Account) response;
                System.out.println("Login successful: " + user.getEmailAddress());
                session.setUser(user); // login (set user to session) & return to dashboard
                MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
                
            } else if (response == null) {
                System.out.println("Login failed: Invalid credentials");
                Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                    "Login failed: Invalid credentials");
                alert.showAndWait();
                session.setUser(null);
                clear();
                
            } else {
                System.out.println("Unexpected response: " + response.getClass());
                
            }
        } catch (UserInputException e) { 
            String message = "Exception during login: " + e.getMessage(); 
            System.out.println(message); 
            Utility.alertGenerator("Input mismatch occurred!", 
                    "Input mismatch occurred!", message, 1);
            
        } catch (Exception ex) {
            System.out.println("Exception during login: " + ex.getMessage());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                "Exception occured during login:\n" + ex.getMessage()); 
            alert.showAndWait(); 
            
            session.setUser(null);
        } 
    }
    
    /** 
     * Upon clicking goes to the Registration page 
     * 
     * @param event 
     */
    @FXML
    private void registerButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.REGISTER);
    }
    
    /** 
     * Clears both fields 
     */
    private void clear() {
        emailTextField.clear();
        passwordTextField.clear();
    }
}
