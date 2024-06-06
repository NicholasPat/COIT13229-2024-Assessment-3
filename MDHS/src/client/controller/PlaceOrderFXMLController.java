package client.controller;

import client.MDHSClient;
import client.Session;
import common.UserInputException;
import common.Utility;
import common.model.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
     * Upon changing the scene to the current one, will invoke the following steps 
     * Mainly: Load Order + Order Items from the server, and set the combo boxes. 
     * Afterwards, populate the form. 
     */
    @Override
    public void handleSceneChange() {
        try {
            clear();
            loadProducts();
            loadExistingOrderData();
        } catch (Exception e) {
            String message = "Exception Occurred!\n" + e.getMessage(); 
            exceptionOutput("Notice!", message, 2); 
            MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
            return; 
        }
        
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
        costUpdate(); 
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
     * Sends user back to the Dashboard. 
     * 
     * @param event 
     */
    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    /**
     * Upon clicking, cycles backwards through the list of Order Items attached 
     * to the current Order. 
     * 
     * @param event 
     */
    @FXML
    private void previousOrderItemButtonHandler(ActionEvent event) {
        
        try {
            if (numberOfItems == 1 || numberOfItems == 0) {return;}
            currentItemIndex--;
            
            //Broken?? 
            if (currentItemIndex < 0 && numberOfItems >= 0) {
                currentItemIndex = numberOfItems - 1; // if index went below 0 cycle back to end of list
            } 
            
            if (!orderItems.isEmpty()) {
                currentItem = orderItems.get(currentItemIndex);
                populateItemForm();
            }
        
        /*
            This happens for a reason I do not know, but need to manually set the 
            max when attempting to reach it. As it is, it works, so leaving it. 
        */
        } catch (Exception e) {
            currentItemIndex = numberOfItems-1; 
            currentItem = new OrderItem(); 
            quantityTextField.clear(); 
            productPriceTextField.clear();
            productChoiceBox.setValue(""); 
            populateForm(); 
        }
    }
    
    /**
     * Moves through the list of Order Items to the next option. If at end, will 
     * cycle back around to the bottom. 
     * 
     * @param event 
     */
    @FXML
    private void nextOrderItemButtonHandler(ActionEvent event) {
        
        try {
            if (numberOfItems == 0 || numberOfItems == 1) {return;}
            currentItemIndex++;
            
            //If index went above total number, cycle back -- Broken?? 
            if (currentItemIndex >= numberOfItems) {currentItemIndex = 0;}
            
            if (!orderItems.isEmpty()) {
                currentItem = orderItems.get(currentItemIndex);
                populateItemForm();
            }
            
        /*
            This happens for a reason I do not know, but need to manually set the 
            max when attempting to reach it. As it is, it works, so leaving it. 
        */
        } catch (Exception e) {
            currentItemIndex = numberOfItems-1; 
            currentItem = new OrderItem(); 
            quantityTextField.clear(); 
            productPriceTextField.clear();
            productChoiceBox.setValue(""); 
            populateForm(); 
        }
    }
    
    /**
     * Adds Order Item to the list. 
     * 
     * @param event 
     */
    @FXML
    private void addOrderItemButtonHandler(ActionEvent event) {
        if (currentItemIndex+1 != numberOfItems && numberOfItems !=1) { 
            System.out.println("Current Index: " + currentItemIndex + "\nNumber of Items total: " + numberOfItems + "\n");
            String message = "Please select the most recent OrderItem according to count: " + (numberOfItems-1);
            exceptionOutput("Notice!", message, 2); 
            return; 
        }
        
        try {
            //Get all information recorded in the Form. Then clean up. 
            recordOrderItem();
            numberOfItems++;
            currentItemIndex = numberOfItems-1;
            callNewItem();
            populateItemForm();
            costUpdate(); 
        } catch (UserInputException ie) {
            currentItemIndex = numberOfItems-1; 
            String message = "Error occured with adding an Order!\n" + ie.getMessage();
            exceptionOutput("Error occurred!", message, 1); 
        } catch (Exception e) { 
            String message = "Notice regarding duplicate!\n" + e.getMessage();
            exceptionOutput("Notice!", message, 2); 
        }
    }
    
    /**
     * Removes an Order Item that is selected. If not actually added to the list, 
     * it will throw an error and return. 
     * 
     * @param event 
     */
    @FXML
    private void removeOrderItemButtonHandler(ActionEvent event) {
        /*
        Case of if the orderItems if empty, then don't do anything, since will 
        break otherwise. 
        */
        if (orderItems.isEmpty()) { 
            String message = "No order items in list, please add some before attempting to remove!";
            exceptionOutput("Notice!", message, 2); 
            return; 
        }
        
        //Another rror handle:  If the current item is the null item, then don't 
        if (currentItem == null) { 
            exceptionOutput("Notice!", 
                    "No active Order Item added. Please remove a previous Order Item!", 1); 
            return; 
        }
        
        try { 
            /*
            Will always make it go to the next item in entry. Won't be going back 
            an entry. 
            */
            if (!orderItems.isEmpty()) {
                orderItems.remove(currentItemIndex);
                
                numberOfItems = orderItems.size() + 1;
                exceptionOutput("Notice!", "Removed the Order item from the list.", 2); 
                callNewItem(); 
            } 
        } catch (Exception e) { 
            exceptionOutput("Exception has occurred with removing an entry!", 
                    "Please remember to add an Order Item before attempting to remove it!", 2);
        }
        
        //Populate, update the cost. 
        populateItemForm();  
        costUpdate(); 
    }
    
    /**
     * Sets a new OrderItem into the scene. Won't add to the List 
     */
    private void callNewItem() { 
        currentItem = new OrderItem();
        productChoiceBox.setValue(null);
        quantityTextField.clear();
        productPriceTextField.clear(); 
    }
    
    /**
     * Handles the placing of the Order if there are Order Items in it. 
     * 
     * @param event 
     */
    @FXML
    private void placeOrderButtonHandler(ActionEvent event) {
        /*
        If the items is empty, OR the current item is a null one, then don't add,
        will break otherwise. 
        */
        if (orderItems.isEmpty() || orderItems.get(0) == null) { 
            String message = "Order Item list is empty, please add some Order Items to your Order then add!"; 
            exceptionOutput("Notice!", message, 2); 
            return; 
        }
        
        session = Session.getSession();
        
        int accountId = session.getUser().getAccountId();
        String deliveryTime = hourComboBox.getValue() +":"+minuteComboBox.getValue();
        double totalCost = costUpdate();
        
        try {
            // TODO: Update Order
            session.objOut.writeObject("PlaceOrder");
            int currentOrderId = 0;
            if (currentOrder != null) 
                currentOrderId = currentOrder.getOrderId();
            
            Order order = new Order(currentOrderId, accountId, deliveryTime, orderItems, totalCost);
            session.objOut.writeObject(order);
            
            exceptionOutput("Notice!", "Successfully added an Order!", 2); 
            MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
            
        } catch (IOException e) {
            String message = "An Exception has occurred while placing the Order! " + e.getMessage();
            exceptionOutput("an Exception has occurred!", message, 1); 
        }
    }
    
    /**
     * Handles the removal of an Order if one is actively and exists. 
     * 
     * @param event 
     */
    @FXML
    private void cancelOrderButtonHandler(ActionEvent event) {
        /*
        Can't cancel what doesn't exist already. Although could say there is an 
        assumption that wouldn't want to reset the scene. To do that though, just 
        go back to dashboard and re-enter. 
        */
        if (orderItems.isEmpty()) { 
            String message = "Please ensure you have Placed an inital Order first.";
            exceptionOutput("Notice!", message, 2); 
            return;
        }
        
        session = Session.getSession();
        
        try {
            session.objOut.writeObject("CancelOrder");
            session.objOut.writeObject(session.getUser().getAccountId());
            clear();
            exceptionOutput("Notice!", "Removed the Order! Returning to Dashboard", 2); 
            MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
            
        } catch (IOException e) {
            String message = "An Exception has occurred while deleting the Order! " + e.getMessage();
            exceptionOutput("An Exception has occurred!", message, 1); 
        }
    }
    
    /**
     * Deals with the selection of the Products as per the list received from the 
     * Server. 
     * 
     * @param selectedProduct 
     */
    private void handleProductSelection(String selectedProduct) {
        for (Product product : productList) {
            if (product.toString().equals(selectedProduct)) {
                currentItem.setProductId(product.getProductId());
            }
        }
        //Determine selected product
        populateItemForm(); 
    }
    
    /**
     * Gets products from the server and loads them into a product list, for use 
     * with the combo box. 
     * 
     * @throws Exception    Thrown if there is an issue with the Products being 
     *                      size 0 
     */
    private void loadProducts() throws Exception{
        Session currentSession = Session.getSession();
        try {
            currentSession.objOut.writeObject("AllProducts");
            productList = (ArrayList<Product>) currentSession.objIn.readObject();
            
            if (productList.isEmpty()) 
                throw new Exception("No Products exist! Please return another time!");

        } catch (IOException | ClassNotFoundException ex) {
            String message = "Exception while loading the product list: " + ex.getMessage(); 
            exceptionOutput("General Exception occurred!", message, 1); 
            currentSession.setUser(null);
        }
        
    }
    
    /**
     * Pings the server for existing Order information. 
     * 
     * @throws Exception    Thrown if the Schedule is size 0 
     */
    private void loadExistingOrderData() throws Exception {
        // load customer info
        session = Session.getSession();
        Customer user = (Customer) session.getUser();
        
        // load delivery info
        try {
            session.objOut.writeObject("DeliveryScheduleByPostcode");
            session.objOut.writeObject(user.getPostcode());

            schedule = (DeliverySchedule) session.objIn.readObject();
            
        } catch (IOException | ClassNotFoundException ex) {
            String error = "Exception occurred while loading delivery schedule:\n" + ex.getMessage(); 
            exceptionOutput("General Exception occured!", error, 1);
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
                if (numberOfItems > 0 && !productList.isEmpty()) {
                                        
                    /*
                    Error handle is Products are missing, won't reflect in server 
                    though, this is just to demonstrate an output. Also, output 
                    a notice alert saying what is happening. 
                    */
                    int previousCount = orderItems.size(); 
                    List<OrderItem> orderItemCheck = Utility.checkIfProductExists(orderItems, productList);
                    int postCount = orderItemCheck.size();
                    int numberRemoved = previousCount - postCount ;
                    
                    if (numberRemoved != 0) { 
                        exceptionOutput("Notice! Removed Products", numberRemoved + 
                                " Products have been removed!\nThis is reflected "
                                + "in the Order Items listings", 2); 
                        orderItems = orderItemCheck; 
                    }
                    
                    numberOfItems = orderItems.size(); 
                    currentItemIndex = 0;
                    currentItem = orderItems.get(currentItemIndex);
                } else {
                    currentItem = new OrderItem();
                }
            } else {
                currentItem = new OrderItem();
            }
        } catch (IOException | ClassNotFoundException ex) {
            String error = "Exception occured while loading customer order:\n" + ex.getMessage();
            exceptionOutput("General Exception occurred!", error, 1); 
        } 
    }
    
    /**
     * Records the OrderItem selected, and then adds to record. 
     * 
     * @throws UserInputException   Thrown if there is a mis-input
     * @throws Exception            Thrown if a Product added is already in the 
     *                              list 
     */
    private void recordOrderItem() throws UserInputException, Exception {
        int qty; 
        double cost = 0;
        
        //Trying outside constructor since quantity directly influences the cost 
        try { 
            qty = Integer.parseInt(quantityTextField.getText().trim());
        } catch (NumberFormatException e) { 
            throw new UserInputException("Invalid input for quantity:\n" + e.getMessage()); 
        }
        
        if (productChoiceBox.getValue() == null) { 
            throw new UserInputException("Please select a Product as well!"); 
        }
        
        //Because cost is based on already checked Product * checked quantity, 
        //won't need error handling for if valid, as should always be a valid 
        //value 
        for (Product product : productList) {
            if (product.getProductId() == currentItem.getProductId()) {
                cost = product.getPrice() * qty;
                cost = Math.round(cost * 100.0) / 100.0;
            }
        }
        
        //
        try { 
            if (orderItems.size() <= currentItemIndex) {
                orderItems.add(new OrderItem(currentItem.getProductId(), qty, cost));
            } else {
                orderItems.set(currentItemIndex, new OrderItem(currentItem.getProductId(), qty, cost));
            }
        } catch (UserInputException e) { 
            throw new UserInputException("Invalid Product selected for Order Item:\n"
                + e.getMessage()); 
        }
        
        checkEntryExists(); 
        
        //Number of items for how many items to cycle through. 
        numberOfItems = orderItems.size();
    }
    
    /** 
     * This is a brute force, but checks each orderItem to determine if there is 
     * a Product already chosen, because if there is, it will cause some issues 
     * when adding Product List to the DB. i and j check if they are the same entry. 
     * Won't throw an error for if the entry is on the same orderItem, hence the 
     * i and j values acting as counters. 
     * 
     * @throws Exception    Exception occurs when there is an entry found when 
     *                      doing the search for if there are any matching entries 
     */
    private void checkEntryExists() throws Exception{
        int i = 0; //Check 1's count 
        for (OrderItem orderItemsCheck : orderItems) {
            int j = 0; //Check 2's counter 
            for (OrderItem previousCheck : orderItems) {
                //If a match is found, AND it's not from the same index, then throw Exception 
                if (orderItemsCheck.getProductId() == previousCheck.getProductId() && i != j) {
                    throw new Exception("Product entry already exists, please edit "
                            + "previous product instance to reflect the extra count desired!"); 
                }
                j++; 
            }
            i++; 
        }
    }
    
    /**
     * Populates the form with data from the Order. 
     */
    private void populateForm() {
        //Populate delivery schedule info
        if (schedule != null) {
            deliveryDayTextField.setText(schedule.getDeliveryDay());
            deliveryCostTextField.setText("$"+schedule.getDeliveryCost());
        } 
        
        //Set existing delivery Time
        if (currentOrder != null && currentOrder.getDeliveryTime() != null) {
            String deliveryTime = currentOrder.getDeliveryTime();
            System.out.println(deliveryTime);
            String[] timeParts = deliveryTime.split(":");
            if (timeParts.length >= 2) {
                hourComboBox.setValue(timeParts[0]);
                minuteComboBox.setValue(timeParts[1]);
            }
        }
        
        //Set existing order items
        if (!orderItems.isEmpty()) {
            populateItemForm();    
            costUpdate(); 
        }
    }
    
    /**
     * Takes the current information given from the server and outputs it to the fields. 
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
        
        //Moving to after method in some cases. Causes some issues with logic 
        //When cycling from a current uncommitted item. 
        //costUpdate(); 
    }
    
    /**
     * Updates the cost fields with information from the current order. 
     * 
     * @return Total cost as a value 
     */
    private double costUpdate() {
        double subtotal = 0;
        double delivery = schedule.getDeliveryCost();
        double tax;
        double total;

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
     * Clears all fields 
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
     * Generates an Alert with title, header, and body. Type is determined by 
     * the i value. Won't always be used for "Exception" cases. 
     * 
     * @param title     Title for the alert to be shown 
     * @param message   Body message for the alert 
     * @param i         1 - ERROR / 2 - INFORMATION
     */
    private void exceptionOutput(String title, String message, int i) { 
        System.out.println(message); 
        Utility.alertGenerator(title, title, message, i);
    }
}
