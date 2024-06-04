package client.controller;

import client.MDHSClient;
import client.Session;
import common.UserInputException;
import common.Utility;
import common.model.*;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
 */
public class PlaceOrderFXMLController implements Initializable, SceneController {

    @FXML
    private AnchorPane contentAnchorPane;
    @FXML
    private Button dashboardButton;
    @FXML
    private TextField quantityTextField;
    @FXML
    private Button previousOrderItemButton;
    @FXML
    private Button nextOrderItemButton;
    @FXML
    private TextField currentOrderItemTextField;
    @FXML
    private TextField totalOrderItemTextField;
    @FXML
    private Button addOrderItemButton;
    @FXML
    private Button removeOrderItemButton;
    @FXML
    private Button placeOrderButton;
    @FXML
    private TextField subtotalTextField;
    @FXML
    private TextField deliveryCostTextField;
    @FXML
    private TextField taxTextField;
    @FXML
    private TextField totalCostTextField;
    @FXML
    private Button cancelOrderButton;
    @FXML
    private TextField productPriceTextField;
    @FXML
    private ComboBox<String> hourComboBox;
    @FXML
    private ComboBox<String> minuteComboBox;
    @FXML
    private TextField deliveryDayTextField;
    @FXML
    private ChoiceBox<String> productChoiceBox;
    
    private Session session; 
    
    private Order currentOrder;
    private List<OrderItem> orderItems;
    private OrderItem currentItem;
    private int currentItemIndex;
    private int numberOfItems;
    
    private List<Product> productList;
    private DeliverySchedule schedule;
    
    /** 
     * 
     */
    @Override
    public void handleSceneChange() {
        clear();
        loadProducts();
        loadExistingOrderData();
        
        // set product options
        for (Product product : productList) {
            productChoiceBox.getItems().add(product.toString());
        }
        
        // set delivery hour options
        hourComboBox.getItems().addAll(
            "08", "09", "10", "11", "12", // AM hours
            "13", "14", "15", "16"  // PM hours
        );
        hourComboBox.setValue("08");
        
        // set delivery minute options
        minuteComboBox.getItems().addAll("00", "15", "30", "45");
        minuteComboBox.setValue("00");
        
        populateForm();
    }
    
    /**
     * Initializes the controller class. 
     * 
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleProductSelection(newValue);
            }
        });
    }    
    
    /** 
     * 
     * 
     * @param event 
     */
    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void previousOrderItemButtonHandler(ActionEvent event) {
        recordOrderItem();
        currentItemIndex--;
        if (currentItemIndex < 0 && numberOfItems >= 0)
            currentItemIndex = numberOfItems-1; // if index went below 0 cycle back to end of list
        if (!orderItems.isEmpty()) {
            currentItem = orderItems.get(currentItemIndex);
            populateItemForm();
        }
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void nextOrderItemButtonHandler(ActionEvent event) {
        recordOrderItem();
        currentItemIndex++;
        if ( currentItemIndex >= numberOfItems ) // if index went above total number, then cycle back to first entry
            currentItemIndex = 0;
        if (!orderItems.isEmpty()) {
            currentItem = orderItems.get(currentItemIndex);
            populateItemForm();
        }
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void addOrderItemButtonHandler(ActionEvent event) {
        try {
            recordOrderItem();
            numberOfItems++;
            currentItemIndex = numberOfItems-1;
            currentItem = new OrderItem();
            productChoiceBox.setValue(null);
            quantityTextField.clear();
            populateItemForm();
        } catch (UserInputException ie) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ie.getMessage());   
            alert.showAndWait();
        } 
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void removeOrderItemButtonHandler(ActionEvent event) {
        if (!orderItems.isEmpty()) {
            orderItems.remove(currentItemIndex);
            
            numberOfItems = Integer.max(orderItems.size(), 1);
            if (currentItemIndex >= orderItems.size()) {
                currentItemIndex = orderItems.size()-1;
            }
                
            if (!orderItems.isEmpty()) {
                currentItem = orderItems.get(currentItemIndex);
            } else {
                currentItem = new OrderItem();
                productChoiceBox.setValue(null);
                quantityTextField.clear();
            }
        }
        populateItemForm();  
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void placeOrderButtonHandler(ActionEvent event) {
        recordOrderItem();
        session = Session.getSession();
        
        int accountId = session.getUser().getAccountId();
        String deliveryTime = hourComboBox.getValue() +":"+minuteComboBox.getValue();
        double totalCost = costUpdate();
        try {
            // TODO: Update Order
            session.objOut.writeObject("PlaceOrder");
            int currentOrderId = 0;
            if (currentOrder != null) {
                currentOrderId = currentOrder.getOrderId();
            }
            Order order = new Order(currentOrderId, accountId, deliveryTime, orderItems, totalCost);
            session.objOut.writeObject(order);
            
            MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while deleting order: " + ex.getMessage());
        }
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void cancelOrderButtonHandler(ActionEvent event) {
        session = Session.getSession();
        try {
            session.objOut.writeObject("CancelOrder");
            
            session.objOut.writeObject(session.getUser().getAccountId());
            
            clear();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while deleting order: " + ex.getMessage());
        }
    }
    
    /**
     * 
     * @param selectedProduct 
     */
    private void handleProductSelection(String selectedProduct) {
        for (Product product : productList) {
            if (product.toString().equals(selectedProduct)) {
                currentItem.setProductId(product.getProductId());
            }
        }
        // determine selected product
        populateItemForm();
    }
    
    /**
     * Gets products from the server and loads them into a product list, for use 
     * with the combo box 
     */
    private void loadProducts() {
        Session session = Session.getSession();
        try {
            session.objOut.writeObject("AllProducts");
            productList = (ArrayList<Product>) session.objIn.readObject();

        } catch (Exception ex) {
            String message = "Exception while loading the product list: " + ex.getMessage(); 
            exceptionOutput("General Exception occurred!", message, 1); 
            session.setUser(null);
        }
    }
    
    /**
     * 
     */
    private void loadExistingOrderData() {
        // load customer info
        session = Session.getSession();
        Customer user = (Customer) session.getUser();
        
        // load delivery info
        try {
            session.objOut.writeObject("DeliveryScheduleByPostcode");
            session.objOut.writeObject(user.getPostcode());

            schedule = (DeliverySchedule) session.objIn.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while loading delivery schedule: " + ex.getMessage());
        }
        
        // if customer already has order load its data
        try {
            session.objOut.writeObject("GetCustomerOrder");
            session.objOut.writeObject(user.getAccountId());
            
            
            Order order = (Order) session.objIn.readObject();
            if (order != null) {
                currentOrder = order;
                orderItems = order.getProductList();
                
                numberOfItems = orderItems.size();
                if (numberOfItems > 0) {
                    currentItemIndex = 0;
                    currentItem = orderItems.get(currentItemIndex);
                } else {
                    currentItem = new OrderItem();
                }
            } else {
                currentItem = new OrderItem();
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Exception while loading customer order: " + ex.getMessage());
        } 
    }
    
    /**
     * 
     */
    private void recordOrderItem() {
        int qty = Integer.parseInt(quantityTextField.getText().trim());
        double cost = 0;
        for (Product product : productList) {
            if (product.getProductId() == currentItem.getProductId()) {
                cost = product.getPrice() * qty;
                cost = Math.round(cost * 100.0) / 100.0;
            }
        }
        if (orderItems.size() <= currentItemIndex) {
            orderItems.add(new OrderItem(currentItem.getProductId(), qty, cost));
        } else {
            orderItems.set(currentItemIndex, new OrderItem(currentItem.getProductId(), qty, cost));
        }
        numberOfItems = orderItems.size();
    }
    
    /**
     * 
     */
    private void populateForm() {
        // Populate delivery schedule info
        if (schedule != null) {
            deliveryDayTextField.setText(schedule.getDeliveryDay());
            deliveryCostTextField.setText("$"+schedule.getDeliveryCost());
        } 
        
        // Set existing delivery Time
        if (currentOrder != null && currentOrder.getDeliveryTime() != null) {
            String deliveryTime = currentOrder.getDeliveryTime();
            System.out.println(deliveryTime);
            String[] timeParts = deliveryTime.split(":");
            if (timeParts.length >= 2) {
                hourComboBox.setValue(timeParts[0]);
                minuteComboBox.setValue(timeParts[1]);
            }
        }
        
        // set existing order items
        if (!orderItems.isEmpty()) {
            populateItemForm();           
        }
    }
    
    /**
     * 
     */
    private void populateItemForm() {
        for (Product product : productList) {
            if (product.getProductId() == currentItem.getProductId()) {
                productChoiceBox.setValue(product.toString());
            }
        }
        
        currentOrderItemTextField.setText(currentItemIndex+1+"");
        totalOrderItemTextField.setText(numberOfItems+"");
        if (currentItem.getQuantity() != 0) {
            quantityTextField.setText(currentItem.getQuantity()+"");
        }

        for (Product product : productList) {
            if (product.getProductId() == currentItem.getProductId()) {
                productPriceTextField.setText("$"+product.getPrice());
            }
        }
        
        costUpdate();
    }
    
    /**
     * 
     * 
     * @return Total cost as a value 
     */
    private double costUpdate() {
        double subtotal = 0;
        double delivery = schedule.getDeliveryCost();
        double tax = 0;
        double total = 0;

        // Calculate subtotal
        for (OrderItem item : orderItems) {
            double itemPrice = 0;
            for (Product product : productList) {
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
    
    /**
     * 
     */
    private void clear() {
        currentItem = new OrderItem();
        orderItems = new ArrayList<>();
        currentItemIndex = 0;
        numberOfItems = 1;
        currentOrder = new Order();
        
        productChoiceBox.getItems().clear();
        hourComboBox.getItems().clear();
        minuteComboBox.getItems().clear();
        
        deliveryDayTextField.clear();
        deliveryCostTextField.clear();
        
        currentOrderItemTextField.clear();
        totalOrderItemTextField.clear();
        quantityTextField.clear();
        productPriceTextField.clear();
        
        subtotalTextField.clear(); 
        taxTextField.clear();
        totalCostTextField.clear();
    }
    
    /**
     * 
     * 
     * @param title
     * @param message
     * @param i 
     */
    private void exceptionOutput(String title, String message, int i) { 
        System.out.println(message); 
        Utility.alertGenerator(title, title, message, i);
    }
}
