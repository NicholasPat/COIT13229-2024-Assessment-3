/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package client.controller;

import client.MDHSClient;
import client.Session;
import common.model.Account;
import common.model.Customer;
import common.model.Order;
import common.model.OrderItem;
import common.model.Product;
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
    private Order currentOrder; 
    private int currentOrderIndex; 
    private int numberOfOrders; 

    @Override
    public void handleSceneChange() {
        loadOrders(); 
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
        
    }

    @FXML
    private void nextIndexButtonHandler(ActionEvent event) {
        
    }
    
    private void loadOrders() {
        Session session = Session.getSession(); 
        List<Integer> customerIdList = null; 
        List<OrderItem> orderItemList = null;  
        
        try {
            session.objOut.writeObject("AllOrders");
            orderList = (List<Order>) session.objIn.readObject(); 
            
            if (!orderList.isEmpty()) {
                //Assigning to a list all the Customer IDs, so that they can be 
                //iterated upon to get them in order 
                for (int i=0; i< orderList.size(); i++) { 
                    customerIdList.add(orderList.get(i).getCustomerId()); 
                    List<OrderItem> listOfOrderItems = orderList.get(i).getProductList(); 
                    //orderItemList.add(listOfOrderItems); 
                }
                
                //Assigning to a list all the Product Ids, also OrderIds so that 
                //Can be matched up 
                
                numberOfOrders = orderList.size(); 
                currentOrderIndex = 0; 
                currentOrder = orderList.get(currentOrderIndex); 
                
                //Iterate on this once 
                totalIndexTextField.setText(Integer.toString(numberOfOrders+1)); 
                
                //Hard coding this value since assuming always positon 1
                currentIndexTextField.setText("1"); 
                populateForm(session); 
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while loading product list: " + ex.getMessage());
            session.setUser(null);
        }
    }
    
    private void populateForm(Session session) throws Exception { 
        //Set list of products ordered then tabulate cost 
        List<OrderItem> currentOrderItem = currentOrder.getProductList(); 
        String listOfProducts = ""; 
        double itemTotal = 0; 
        
        
        
        
        //Get Customers 
        session.objOut.writeObject("CustomerById"); 
        Customer cCustomer = (Customer) session.objIn.readObject(); 
        
        //Get OrderItems (Product Name and Price) via the productId
        //CONSIDER: Bundle ProductIDs together and sending in one stream, server 
        //can process into a bundle of product names + cost 
        for (int i=0; i < currentOrderItem.size(); i++) {
            session.objOut.writeObject("ProductById"); 
            session.objOut.writeObject(currentOrderItem.get(i).getProductId()); 
            Product cProduct = (Product) session.objIn.readObject(); 
            
            //If product was deleted, thus no result 
            if (cProduct == null) { 
                //Do nothing, won't add to it
            } else { 
                listOfProducts += cProduct.getProductName() + "\n";
                //Assumption: Always at least one quantity 
                itemTotal = itemTotal + (cProduct.getPrice() * cProduct.getQuantity());
            } 
        }
        
        //Get tax value and total value 
        double taxValue = itemTotal * 0.1; 
        double totalCost = itemTotal + taxValue; 
        
        //Finally, populate the tables 
        firstNameTextField.setText(cCustomer.getFirstName()); 
        lastNameTextField.setText(cCustomer.getLastName()); 
        emailTextField.setText(cCustomer.getEmailAddress()); 
        phonenumberTextField.setText(Integer.toString(cCustomer.getPhoneNumber())); 
        addressTextField.setText(cCustomer.getDeliveryAddress()); 
        deliveryTimeTextField.setText(currentOrder.getDeliveryTime()); 
        orderItemsTextArea.setText(listOfProducts); 
        deliveryCostTextField.setText("Unimplemented for now"); 
        subtotalTextField.setText("$"+Double.toString(itemTotal)); 
        taxTextField.setText("$"+Double.toString(taxValue));
        totalCostTextField.setText("$"+Double.toString(totalCost));
    }
    
    private void clear() { 
        
    }
    
}
