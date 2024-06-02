
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
 * @author lucht
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
     * On click, returns to the dashboard 
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
     * @param event 
     */
    @FXML
    private void loginButtonHandler(ActionEvent event) {
        Session session = Session.getSession();
        
        String email = emailTextField.getText().trim();
        String pass = passwordTextField.getText().trim();
        String errorMessage = errorMessageCheck(email, pass);
        
        if (!Utility.isEmpty(errorMessage)) { 
            Alert alert = new Alert(Alert.AlertType.ERROR,errorMessage);
            alert.showAndWait(); 
            
            clear();
            return;
        }
        
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
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Exception during login: " + ex.getMessage());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                "Exception occured during login:\n" + ex.getMessage()); 
            alert.showAndWait(); 
            
            session.setUser(null);
        } 
    }
    
    /** 
     * Upon clicking goes to the Registration page 
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
     * Checks that the email and password fit ideal formats 
     * @param email 
     * @param pass 
     * @return Returns error message, either empty string, or with error 
     */
    private String errorMessageCheck(String email, String pass) { 
        String message = ""; 
        
        if (Utility.isEmpty(email)) 
            message += "Email is a mandatory field.\n";
        
        if (!Utility.isValidString(email, 0, 100, true)) 
            message += "Email address cannot exceed 100 characters.\n";
        
        if (!Utility.isValidEmail(email)) 
            message += "Email address must contain an '@' symbol.\n";
                
        if (!Utility.isValidString(pass, 7, 30, true)) 
            message += "\nInvalid password.\nPassword must be at least 7 characters, and no more than 30 characters.";
        
        return message; 
    }
}
