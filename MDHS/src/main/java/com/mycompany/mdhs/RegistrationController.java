package com.mycompany.mdhs;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.security.PublicKey;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javax.crypto.Cipher;
/**
 * FXML Controller class
 *
 * @author linke
 */
public class RegistrationController implements Initializable {
    
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailAddressField;
    @FXML
    private TextField deliveryAddressField;
    @FXML
    private PasswordField passwordField;
    
    private static PublicKey publicKey ; 
    final String hostName = "localhost" ; 
    final int serverPort = 6464 ; //To connect to server MDHS 
    private final Socket clientSocket = null ; 
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    /** 
     * 
     * @param event 
     */
    @FXML
    private void onClickBackToLogin(ActionEvent event) {
        try {
           App.setRoot("Login");
        } catch (IOException e){
           System.out.println(e); 
        }
    }
    
    /** 
     * 
     * @param event 
     */
    @FXML
    private void onClickRegister(ActionEvent event) {
        /*Error handle if the fields are empty, if they are display error and return*/
        String message = validation() ; 
        if (!message.equals("")) { 
            Alert alert = new Alert(Alert.AlertType.ERROR, message) ; 
            alert.showAndWait() ; 
            return ; 
        }
        
        /*Note - password field will be handled differently!*/
        String dataToSend = firstNameField.getText() + "::" + lastNameField.getText() 
                + "::" + usernameField.getText() + "::" + phoneNumberField.getText() 
                + "::" + emailAddressField.getText() + "::" + deliveryAddressField.getText() 
                + "::" + passwordField.getText() ; 
        
    }
    
    /** 
     * This method is used to error handle if the text fields are empty. If they 
     * are it will send a not null message which will be used in creating an alert 
     * @return message 
     */
    private String validation() { 
        String message = null ; 
        
        if(firstNameField.getText().equals("")) { 
            message+="First name is a mandatory field\n" ; 
        } 
        if (lastNameField.getText().equals("")) { 
            message+="Last name is a mandatory field\n" ; 
        }
        if (usernameField.getText().equals("")) { 
            message+="Username is a mandatory field\n" ; 
        }
        if (phoneNumberField.getText().equals("")) { 
            message+="Phone number is a mandatory field\n" ; 
        }
        if (emailAddressField.getText().equals("")) { 
            message+="emailAddress is a mandatory field\n" ; 
        }
        if (deliveryAddressField.getText().equals("")) { 
            message+="Delivery address is a mandatory field\n" ; 
        }
        if (passwordField.getText().equals("")) { 
            message+="Password is a mandatory field\n" ; 
        }
        
        return message ; 
    }
    
    /** 
     * Source: Week 6 Lab Question 2 Solution 
     * WHAT IS THIS METHOD DOING? 
     * 
     * 
     * @param message
     * @return
     * @throws Exception 
     */
    private byte[] encrypt(String message) throws Exception { 
        Cipher cipher = Cipher.getInstance("RSA") ; 
        cipher.init(Cipher.ENCRYPT_MODE, publicKey) ; 
        
        byte[] cipherData = cipher.doFinal(message.getBytes("UTF-8")) ; 
        return cipherData ; 
    }

}
