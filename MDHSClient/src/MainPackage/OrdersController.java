package MainPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 * 
 * NOTE: using TableView which was utilised in COIT11134
 *
 * @author linke
 */
public class OrdersController implements Initializable { 

    @FXML
    private TableView<OrderInformation> displayDelivery;
    @FXML
    private TableColumn<OrderInformation, String> displayDay;
    @FXML
    private TableColumn<OrderInformation, String> displayTime;
    @FXML
    private TableColumn<OrderInformation, String> displayCost;
    @FXML
    private TableColumn<OrderInformation, String> displayAddress;
    @FXML
    private TableColumn<OrderInformation, String> displayProducts;
    @FXML
    private Label scheduleNameField;
    
    private ArrayList<OrderInformation> orderInformationList = new ArrayList<>() ; 
    private Socket s ; 
    private DataInputStream dataIn ; 
    private DataOutputStream dataOut ; 
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        s = CurrentSession.getS() ; 
        dataIn = CurrentSession.getDataIn() ; 
        dataOut = CurrentSession.getDataOut() ; 
        
        scheduleNameField.setText("Delivery Schedule For: " 
                + CurrentSession.getUsername()) ; 
        System.out.println("Adding orders to tables") ; 
        addToTable() ; 
    } 
    
    private void getOrderInformation() { 
        
    }
    
    private void addToTable() { 
        
    }
    
    /** 
     * CHANGE TO VIEW PRODUCTS PAGE WHEN OVER 
     * @param event 
     */
    @FXML
    private void onClickViewProducts(ActionEvent event) {
        try {MdhsClient.setRoot("Orders") ; 
            } catch (IOException ex) {ex.printStackTrace();}
    }

    @FXML
    private void onClickLogOut(ActionEvent event) {
        
        try {MdhsClient.setRoot("Login") ;
            } catch (IOException ex) {ex.printStackTrace();}
    }
    
}
