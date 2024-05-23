/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package client.controller;

import client.*;
import common.Authenticator;
import common.model.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private void backButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }

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
                session.setUser(user);
                MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
            } else if (response == null) {
                System.out.println("Login failed: Invalid credentials");
                session.setUser(null);
                clear();
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
    private void registerButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.REGISTER);
    }
    
    private void clear() {
        emailTextField.clear();
        passwordTextField.clear();
    }
}
