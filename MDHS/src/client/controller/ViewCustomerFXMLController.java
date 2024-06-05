package client.controller;

import client.MDHSClient;
import client.Session;
import common.Utility;
import common.model.*;
import java.io.IOException;
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
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
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
    
    /** 
     * Upon changing scene to this one, do these things 
     */
    @Override
    public void handleSceneChange() {
        clear();
        loadAccounts(); 
        adminCheckbox.setDisable(true);
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
     * Return to Dashboard. 
     * 
     * @param event 
     */
    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    /** 
     * Cycle back through the Accounts. 
     * 
     * @param event 
     */
    @FXML
    private void previousIndexButtonHandler(ActionEvent event) {
        currentAccountIndex--;
        if (currentAccountIndex < 0 && numberOfAccounts > 0)
            currentAccountIndex = numberOfAccounts-1; // if index went below 0 cycle back to end of list
        currentAccount = accountList.get(currentAccountIndex);
        populateForm();
    }
    
    /**
     * Cycle forward through the Accounts. 
     * 
     * @param event 
     */
    @FXML
    private void nextIndexButtonHandler(ActionEvent event) {
        currentAccountIndex++;
        if ( currentAccountIndex >= numberOfAccounts ) 
            currentAccountIndex = 0;
        currentAccount = accountList.get(currentAccountIndex);
        currentIndexTextField.setText(Integer.toString(currentAccountIndex+1)); 
        populateForm();
    }
    
    /**
     * Load into the program the Accounts from the Server DB. 
     */
    private void loadAccounts() { 
        Session session = Session.getSession(); 
        
        try { 
            //Ping server and then read in the List of Accounts. 
            session.objOut.writeObject("AllCustomers");
            accountList = (List<Account>) session.objIn.readObject(); 
            
            //If not empty, continue on to set the Account information. 
            if (!accountList.isEmpty()) { 
                numberOfAccounts = accountList.size(); 
                currentAccountIndex = 0; 
                currentAccount = accountList.get(currentAccountIndex); 
                populateForm();
            } else { 
                //Information message, but should never be called, just an edge 
                //case since an Account needs to access this menu anyway. 
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            String message = "Exception while loading Accounts: " + ex.getMessage(); 
            Utility.alertGenerator("Exception!", "Exception!", message, 2);
            session.setUser(null);
        }
    }
    
    /**
     * Using current Account, set the information in the form. 
     */
    private void populateForm() { 
        //Clear the form, then set the generic text fields. 
        clear();
        firstNameTextField.setText(currentAccount.getFirstName()+""); 
        lastNameTextField.setText(currentAccount.getLastName()+""); 
        emailTextField.setText(currentAccount.getEmailAddress()+""); 
        
        //Depending on which type of Customer it is, then set specific fields. 
        if (currentAccount instanceof Administrator) { 
            phonenumberTextField.setText("n/a"); 
            addressTextField.setText("n/a"); 
            adminCheckbox.setSelected(true);
        } else if (currentAccount instanceof Customer) { 
            //Unsure if it will work so will see 
            Customer currentCustomer = (Customer) currentAccount;
            phonenumberTextField.setText(currentCustomer.getPhoneNumber()+"");
            addressTextField.setText(currentCustomer.getDeliveryAddress()+""); 
            adminCheckbox.setSelected(false);
        }
        
        totalIndexTextField.setText(numberOfAccounts+"");
        currentIndexTextField.setText(currentAccountIndex+1+"");
    }
    
    /**
     * Clears the form 
     */
    private void clear() { 
        firstNameTextField.clear();
        lastNameTextField.clear();
        emailTextField.clear();
        phonenumberTextField.clear();
        addressTextField.clear(); 
        totalIndexTextField.clear();
        currentIndexTextField.clear();
        adminCheckbox.setSelected(false);
    }
    
}
