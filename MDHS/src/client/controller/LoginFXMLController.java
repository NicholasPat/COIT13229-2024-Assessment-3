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
     * 
     * @param url
     * @param rb
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
            /*
            Encrypt the password to byte, and create Account object with pass 
            and email. 
            */
            byte[] password = Authenticator.encrypt(session.getPublicKey(), pass);
            Account usr = new Account(email, password);
            
            //Tell server for login inbound. 
            session.objOut.writeObject("Login");
            session.objOut.writeObject(usr); // send account info to server

            //Wait for server response with logged-in account
            Object response = session.objIn.readObject();
            
            if (response instanceof Account) {
                //Get response, and then simply move back to dash, not outputting information. 
                Account user = (Account) response;
                System.out.println("Login successful: " + user.getEmailAddress());
                session.setUser(user); // login (set user to session) & return to dashboard
                MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
                
            } else if (response == null) {
                String message = "Login failed: Invalid credentials"; 
                alertHandler("Login failed", message, 1); 
                session.setUser(null);
                clear();
                
            } else {
                //Edge case. 
                String message = "unexpected response: " + response.getClass(); 
                alertHandler("Unexpected response from Server!", message, 1); 
            }
            
        } catch (UserInputException e) { 
            String message = "Exception during login:\n" + e.getMessage(); 
            alertHandler("Exception during login!", message, 2);
            
        } catch (Exception ex) {
            String message = "Exception during login: " + ex.getMessage(); 
            alertHandler("Exception!", message, 1); 
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
    
    /** 
     * Generate an alert via following criteria 
     * 
     * @param title     Title and Header of the Alert
     * @param message   Body message of the Alert 
     * @param i         1 - ERROR / 2 - INFORMATION 
     */
    private void alertHandler(String title, String message, int i) { 
        System.out.println(message); 
        Utility.alertGenerator(title, title, message, i); 
    }
}
