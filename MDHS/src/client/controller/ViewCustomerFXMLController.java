/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package client.controller;

import client.MDHSClient;
import client.Session;
import common.model.Account;
import common.model.Administrator;
import common.model.Customer;
import java.net.URL;
import java.util.List;
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
 * Pretty much the same as ViewProduct, just slightly more to do 
 * @author lucht
 */
public class ViewCustomerFXMLController implements Initializable, SceneController {

    @FXML
    private AnchorPane contentAnchorPane;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField phonenumberTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private CheckBox adminCheckbox;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button previousIndexButton;
    @FXML
    private Button nextIndexButton;
    @FXML
    private TextField currentIndexTextField;
    @FXML
    private TextField totalIndexTextField;
    
    private List<Account> accountList; 
    private Account currentAccount; 
    private int currentAccountIndex; 
    private int numberOfAccounts;

    @Override
    public void handleSceneChange() {
        loadAccounts(); 
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
        currentAccountIndex--;
        if (currentAccountIndex < 0 && numberOfAccounts > 0)
            currentAccountIndex = numberOfAccounts-1; // if index went below 0 cycle back to end of list
        currentAccount = accountList.get(currentAccountIndex);
        populateForm();
    }

    @FXML
    private void nextIndexButtonHandler(ActionEvent event) {
        currentAccountIndex++;
        if ( currentAccountIndex >= numberOfAccounts ) 
            currentAccountIndex = 0;
        currentAccount = accountList.get(currentAccountIndex);
        currentIndexTextField.setText(Integer.toString(currentAccountIndex+1)); 
        populateForm();
    }
    
    private void loadAccounts() { 
        Session session = Session.getSession(); 
        
        try { 
            session.objOut.writeObject("AllCustomers");
            accountList = (List<Account>) session.objIn.readObject(); 
            
            if (!accountList.isEmpty()) { 
                numberOfAccounts = accountList.size(); 
                currentAccountIndex = 0; 
                currentAccount = accountList.get(currentAccountIndex); 
                //Doing here because only needs to be iterated on once 
                totalIndexTextField.setText(Integer.toString(numberOfAccounts));
                currentIndexTextField.setText("1"); //No need to be dynamic, should always be once 
                populateForm();
            } else { 
                //Information message, but should never be called, just an edge 
                //case 
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while loading product list: " + ex.getMessage());
            session.setUser(null);
        }
    }
    
    private void populateForm() { 
        firstNameTextField.setText(currentAccount.getFirstName()+""); 
        lastNameTextField.setText(currentAccount.getLastName()+""); 
        emailTextField.setText(currentAccount.getEmailAddress()+""); 
        
        if (currentAccount instanceof Administrator) { 
            phonenumberTextField.setText("Administrators have no phone number linked"); 
            addressTextField.setText("Administrators have no address linked"); 
            adminCheckbox.selectedProperty();
        } else if (currentAccount instanceof Customer) { 
            //Unsure if it will work so will see 
            Customer currentCustomer = Customer.class.cast(currentAccount);
            phonenumberTextField.setText(currentCustomer.getPhoneNumber()+"");
            addressTextField.setText(currentCustomer.getDeliveryAddress()+""); 
        }
    }
    
    private void clear() { 
        firstNameTextField.clear();
        lastNameTextField.clear();
        emailTextField.clear();
        phonenumberTextField.clear();
        addressTextField.clear(); 
    }
    
}
