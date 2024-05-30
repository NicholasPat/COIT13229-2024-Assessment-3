
package client.controller;

import client.MDHSClient;
import client.Session;
import common.model.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author lucht
 */
public class ViewOrderFXMLController implements Initializable, SceneController {

    @FXML
    private AnchorPane contentAnchorPane;
    @FXML
    private TextField totalCostTextField;
    @FXML
    private TextField taxTextField;
    @FXML
    private TextField subtotalTextField;
    @FXML
    private TextField deliveryCostTextField;
    @FXML
    private TextArea orderItemsTextArea;
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
    private TextField deliveryTimeTextField;
    
    private List<Order> orderList; 
    private List<Account> allAccounts;
    private List<Product> allProducts;
    private List<DeliverySchedule> deliverySchedules;
    private Order currentOrder; 
    private int currentOrderIndex; 
    private int numberOfOrders; 
    @FXML
    private TextField deliveryDayTextField;

    @Override
    public void handleSceneChange() {
        clear();
        loadAllData(); 
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
        currentOrderIndex--;
        if (currentOrderIndex < 0 && numberOfOrders > 0)
            currentOrderIndex = numberOfOrders-1; // if index went below 0 cycle back to end of list
        currentOrder = orderList.get(currentOrderIndex);
        populateForm();
        
    }

    @FXML
    private void nextIndexButtonHandler(ActionEvent event) {
        currentOrderIndex++;
        if ( currentOrderIndex >= numberOfOrders ) // if index went above total number, then cycle back to first entry
            currentOrderIndex = 0;
        currentOrder = orderList.get(currentOrderIndex);
        populateForm();
    }
    
    private void loadAllData() {
        Session session = Session.getSession(); 

        try {
            session.objOut.writeObject("AllOrders");
            orderList = (List<Order>) session.objIn.readObject(); 
            allAccounts = (List<Account>) session.objIn.readObject(); 
            allProducts = (List<Product>) session.objIn.readObject(); 
            deliverySchedules = (List<DeliverySchedule>) session.objIn.readObject(); 
            
            if ( orderList.size() != 0 ) {
                numberOfOrders = orderList.size();
                currentOrderIndex = 0;
                currentOrder = orderList.get(currentOrderIndex);
                populateForm(); // display current order
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while loading order information: " + ex.getMessage());
            session.setUser(null);
        }
    }
    
    private void populateForm() { 
        clear();
        try {
            // order items
            for (OrderItem item: currentOrder.getProductList()) {
                for (Product prod : allProducts) {
                    if (prod.getProductId() == item.getProductId()) {
                        orderItemsTextArea.appendText(prod.toString() + "\n");
                    }
                }
            }
            
            // customer
            for (Account acc : allAccounts) {
                if (acc.getAccountId() == currentOrder.getCustomerId()) {
                    //Finally, populate the tables 
                    firstNameTextField.setText(acc.getFirstName()); 
                    lastNameTextField.setText(acc.getLastName()); 
                    emailTextField.setText(acc.getEmailAddress()); 
                    phonenumberTextField.setText(((Customer) acc).getPhoneNumber()+""); 
                    addressTextField.setText(((Customer) acc).getDeliveryAddress()); 
                    
                    // delivery info
                    for (DeliverySchedule schedule : deliverySchedules) {
                        if (schedule.getPostcode() == ((Customer) acc).getPostcode()) {
                            deliveryCostTextField.setText(schedule.getDeliveryCost()+""); 
                            deliveryDayTextField.setText(schedule.getDeliveryDay()); 
                            deliveryTimeTextField.setText(currentOrder.getDeliveryTime()); 
                        }
                    }
                }
            }
            
            // cost update
            costUpdate();
            
            currentIndexTextField.setText(currentOrderIndex+1+"");
            totalIndexTextField.setText(numberOfOrders+"");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while populating the form: " + ex.getMessage());
        }
    }
    
    private void clear() { 
        firstNameTextField.clear(); 
        lastNameTextField.clear(); 
        emailTextField.clear(); 
        phonenumberTextField.clear(); 
        addressTextField.clear();
        deliveryCostTextField.clear(); 
        deliveryDayTextField.clear(); 
        deliveryTimeTextField.clear();
        orderItemsTextArea.setText("");
        subtotalTextField.clear();
        taxTextField.clear();
        totalCostTextField.clear();
        currentIndexTextField.clear();
        totalIndexTextField.clear();
    }
    
    private double costUpdate() {
        double subtotal = 0;
        double delivery = 0;
        double tax = 0;
        double total = 0;

        // get delivery cost
        for (Account acc : allAccounts) {
            if (acc.getAccountId() == currentOrder.getCustomerId()) {
                for (DeliverySchedule schedule : deliverySchedules) {
                    if (schedule.getPostcode() == ((Customer) acc).getPostcode()) {
                       delivery = schedule.getDeliveryCost();
                    }
                }
            }
        }
        
        // Calculate subtotal
        for (OrderItem item : currentOrder.getProductList()) {
            double itemPrice = 0;
            for (Product product : allProducts) {
                if (product.getProductId() == item.getProductId()) {
                    itemPrice = product.getPrice();
                    break;
                }
            }
            subtotal += itemPrice * item.getQuantity();
        }
        subtotal = Math.round(subtotal * 100.0) / 100.0;

        subtotalTextField.setText("$" + subtotal);

        // Calculate tax
        tax = (subtotal + delivery) * 0.1;
        tax = Math.round(tax * 100.0) / 100.0;
        taxTextField.setText("$" + tax);

        // Calculate total cost
        total = subtotal + delivery + tax;
        total = Math.round(total * 100.0) / 100.0;
        totalCostTextField.setText("$" + total);
        
        return total;
    }
}
