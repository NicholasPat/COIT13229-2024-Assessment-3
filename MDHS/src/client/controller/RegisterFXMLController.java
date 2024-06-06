package client.controller;

import client.MDHSClient;
import client.Session;
import common.Authenticator;
import common.UserInputException;
import common.Utility;
import common.model.*;
import java.io.IOException;
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
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
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
    
    /** 
     * On changing the scene to this one, clear the text fields. 
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
     * On click, takes all text fields and attempt to create an Account, if failed, 
     * throw an error to be displayed to the user. 
     * 
     * @param event 
     * @throws IOException 
     * @throws UserInputException 
     * @throws Exception 
     */
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
        
        //Doing password check here because it won't work in the class itself. Due to not passing into it the String of the password 
        if (!Utility.isValidString(pass, 7, 30, true)) { 
            String error = "Invalid input for password, must be between 7 and 30 digits in length"; 
            System.out.println(error); 
            Utility.alertGenerator("Password input error", 
                    "Error inputting password", error, 1);
            return; 
        }
        
        try {
            //Encrypt and send with a null account. 
            byte[] password = Authenticator.encrypt(session.getPublicKey(), pass);
            Account acc;
            if (isAdmin) {
                acc = new Administrator(isAdmin, fName, lName, 
                        email, password);
            } else {
                acc = new Customer(phone, addr, postcode, 
                        fName, lName, email, password);
            }
            
            //Send String tag to the server, and then the object it will be waiting for 
            session.objOut.writeObject("Register");
            session.objOut.writeObject(acc); // send account info to server
            
            //Wait for server response with logged-in account
            Object response = session.objIn.readObject();
            
            if (response instanceof Account user) {
                String successMessage = "Registration successful for: " + user.getEmailAddress() + "!"; 
                System.out.println(successMessage);
                Utility.alertGenerator("Successful registration!", 
                        "Successful registration!", successMessage, 2);
                session.setUser(user);  // login (set user to session) & return to dashboard
                MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
                
            } else if (response == null) {
                String error = "Registration failed.\nOne reason could be because we don't support delivery to your postcode: " + postcode; 
                System.out.println(error);
                
                //Assumption, failure could be because of not supported postcode 
                Utility.alertGenerator("Error occured", "Error adding customer", error, 1);
                session.setUser(null);
                
            } else {
                String error = "Unexpected response: " + response.getClass(); 
                System.out.println(error);
                Utility.alertGenerator("Unexpected response!", "Unexpected response from the server", error, 2);
                
            }
        } catch (IOException ex) { //Exception with input output streams 
            String error = "IOException occured during login: " + ex.getMessage(); 
            System.out.println(error);
            Utility.alertGenerator("IOException has occured", 
                    "IOException occured!", error, 1);
            session.setUser(null);
            
        } catch (UserInputException ex) { //Exception with adding Account 
            String error = "UserInputException occured during login: " + ex.getMessage();
            System.out.println(error);
            Utility.alertGenerator("UserInputException has Occured!", 
                    "Misinput has occured with the following", error, 1);
            session.setUser(null); 
            
        } catch (Exception ex) { //Exception in general 
            String error = "Exception occured during login: " + ex.getMessage(); 
            System.out.println(error);
            Utility.alertGenerator("Exception has occured!", 
                    "Exception occured with the following", error, 1);
            session.setUser(null);
        }
    }
    
    /**
     * Clears all fields. 
     * 
     * @param event 
     */
    @FXML
    private void clearButtonHandler(ActionEvent event) {
        clear();
    }
    
    /**
     * Handles the disabling or re-enabling of textFields when the staff 
     * checkbox is selected. To demonstrate that those fields are not used in that 
     * registration. 
     * 
     * @param event 
     */
    @FXML
    private void staffMemberCheckBoxHandler(ActionEvent event) {
        boolean status; 
        status = adminCheckbox.isSelected();
        phonenumberTextField.setDisable(status); 
        addressTextField.setDisable(status); 
        postcodeTextField.setDisable(status);
    }
    
    /** 
     * Returns to the dashboard. 
     * 
     * @param event 
     */
    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    /** 
     * Clears all entries for re-entry. 
     */
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
