package client.controller;

import client.MDHSClient;
import client.Session;
import common.UserInputException;
import common.Utility;
import common.model.Product;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
 */
public class ManageProductFXMLController implements Initializable, SceneController {

    @FXML
    private AnchorPane contentAnchorPane;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField quantityTextField;
    @FXML
    private TextField priceTextField;
    @FXML
    private TextArea ingredientsTextArea;
    @FXML
    private TextField unitTextField;
    @FXML
    private TextField productIdTextField;
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
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button newButton;
    
    //Local global variables. 
    private Session session;
    private Product currentProduct;
    private List<Product> productList;
    private int currentProductIndex;
    private int numberOfProducts;
    
    /**
     * Handles the change to this scene. Clears logs and gets current list from 
     * the server. 
     */
    @Override
    public void handleSceneChange() {
        session = Session.getSession();
        clear();
        loadProducts();
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
        // TODO
    }    
    
    /**
     * Returns to Dashboard. 
     * 
     * @param event 
     */
    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    /**
     * Cycles back through the entries. 
     * 
     * @param event 
     */
    @FXML
    private void previousIndexButtonHandler(ActionEvent event) {
        currentProductIndex--;
        
        if (currentProductIndex < 0 && numberOfProducts >= 0)
            currentProductIndex = numberOfProducts-1; // if index went below 0 cycle back to end of list
        
        if (!productList.isEmpty()) {
            currentProduct = productList.get(currentProductIndex);
            populateForm();
        } else { 
            numberOfProducts = 0; 
        }
    }
    
    /**
     * Cycles forward through the entries. 
     * 
     * @param event 
     */
    @FXML
    private void nextIndexButtonHandler(ActionEvent event) {
        currentProductIndex++;
        
        if ( currentProductIndex >= numberOfProducts ) // if index went above total number, then cycle back to first entry
            currentProductIndex = 0;
        
        if (!productList.isEmpty()) {
            currentProduct = productList.get(currentProductIndex);
            populateForm();
        } else { 
            numberOfProducts = 0; 
        }
    }
    
    /**
     * Adds the Product to DB, takes user input and attempts to do so. 
     * 
     * @param event 
     */
    @FXML
    private void addButtonHandler(ActionEvent event) {
        //Check Product ID Field is not empty. 
        if (!"".equals(productIdTextField.getText())) { 
            String error = "Please click on 'New' first."; 
            exceptionOutput("Notice!", error, 2); 
            return; 
        }
        
        try {
            //Record Product details in textboxes, then ping server, then send Product. 
            recordProduct();
            session.objOut.writeObject("RecordProduct");
            session.objOut.writeObject(currentProduct);
            String outcome = (String) session.objIn.readObject(); 
            
            if (outcome.equalsIgnoreCase("RecordProductSuccess")) {
                String outcomeString = "Product was successfully recorded!"; 
                exceptionOutput("Successful addition of Product!", outcomeString, 2); 
                
            } else { 
                String outcomeString = "Product was unsuccessfully recorded!"; 
                exceptionOutput("Unsuccessful addition of Product!", outcomeString, 1); 
            }
            
        } catch (UserInputException e) { 
            String message = "Input mismatch occurred!: " + e.getMessage(); 
            exceptionOutput("Input mismatch!", message, 1); 
            return; 
            
        } catch (IOException | ClassNotFoundException ex) {
            String message = "General Exception occurred!\n" + ex.getMessage(); 
            exceptionOutput("Genral Exception occured!", message, 1); 
            return; 
        }
        
        //Clear the scene and repopulate with updated information. 
        clear();
        loadProducts();
        populateForm();  
    }
    
    /**
     * Updates the Product entry in DB. 
     * 
     * @param event 
     */
    @FXML
    private void updateButtonHandler(ActionEvent event) {
        /*If Product ID Field is empty, then abort because that means you haven't 
        searched for a product first*/
        if ("".equals(productIdTextField.getText())) { 
            String error = "Please search for a product for updating first."; 
            exceptionOutput("Notice!", error, 2); 
            return; 
        }
        
        try { 
            recordProduct(); 
            System.out.println(currentProduct.getProductId()); 
            session.objOut.writeObject("EditProduct");
            session.objOut.writeObject(currentProduct);
            String message = (String) session.objIn.readObject(); 
            
            if (message.equalsIgnoreCase("UpdateProductSuccess")) { 
                String outcomeString = "Product was successfully edited!"; 
                exceptionOutput("Successful edit of Product!", outcomeString, 2); 
                
            } else { 
                String outcomeString = "Product was unsuccessfully edited!"; 
                exceptionOutput("Unsuccessful edit of Product!", outcomeString, 1); 
            }
            
        } catch (UserInputException e) { 
            String message = "Input mismatch occurred!: " + e.getMessage(); 
            exceptionOutput("Input mismatch!", message, 1); 
            
        } catch (IOException | ClassNotFoundException ex) {
            String message = "General Exception occurred!\n" + ex.getMessage(); 
            exceptionOutput("General Exception occurred!", message, 1); 
        }
        
        clear();
        loadProducts();
        populateForm();  
    }
    
    /** 
     * Deletes Product entry from DB 
     * 
     * @param event 
     */
    @FXML
    private void deleteButtonHandler(ActionEvent event) {
        if ("".equals(productIdTextField.getText())) { 
            String error = "Please search for a product for deletion first."; 
            exceptionOutput("Notice!", error, 2); 
            return; 
        }
        
        //Try and send the Product input to the server. Catch failure. 
        try {
            //Record current information about Product to Object 
            recordProduct(); 
            
            //Send server message that incoming Product is for deletion from DB 
            session.objOut.writeObject("DeleteProduct");
            session.objOut.writeObject(currentProduct.getProductId());
            
            //Confirmation message sent, more like 'outcome' 
            String message = (String) session.objIn.readObject(); 
            
            if (message.equalsIgnoreCase("DeleteProductSuccess")) { 
                String outcomeString = "Product was successfully deleted!";
                exceptionOutput("Successful deletion of Product!", outcomeString, 2); 
                
            } else { 
                String outcomeString = "Product was unsuccessfully deleted!"; 
                exceptionOutput("Unsuccessful deletion of Product!", outcomeString, 1); 
            }
            
        } catch (UserInputException e) { 
            String message = "Input mismatch occured!: " + e.getMessage(); 
            exceptionOutput("Input mismatch!", message, 1); 
            
        } catch (IOException | ClassNotFoundException ex) {
            String message = "General Exception occurred!\n" + ex.getMessage(); 
            exceptionOutput("General Exception occurred!", message, 1); 
        }
        
        //Upon finishing the code above, reset the scene, re-get the Product List
        clear();
        loadProducts();
        populateForm();  
    }

    /** 
     * Simply will reset the text fields to be null. 
     * 
     * @param event 
     */
    @FXML
    private void newButtonHandler(ActionEvent event) {
        clear();
    }
    
    /**
     * Gets products from the server and puts them into a list to be output 
     */
    private void loadProducts() {
        try {
            //Write to the server asking for Products. 
            session.objOut.writeObject("AllProducts");
            productList = (ArrayList<Product>) session.objIn.readObject();
            
            /*
            Take product List returned and check the size of it, to determine
            number of Products in the list. 
            */
            numberOfProducts = productList.size();
            if (numberOfProducts > 0) {
                currentProductIndex = 0;
                currentProduct = productList.get(currentProductIndex);
            } else {
                //If the List is empty, simply create an empty Product to be added. 
                currentProduct = new Product();
            }
        } catch (UserInputException e) { 
            String message = "Input mismatch occurred!: " + e.getMessage();
            exceptionOutput("Input mismatch!", message, 1); 
            
        } catch (IOException | ClassNotFoundException ex) {
            String message = "General Exception occurred!\n" + ex.getMessage(); 
            exceptionOutput("General Exception occurred!", message, 1); 
        }
        
        //Output of List to console, removed. 
        //for (Product p : productList) {
            //System.out.println(p.toString());
        //}
    }
    
    /** 
     * Records the current product selected
     * @throws UserInputException   Thrown if there is a mismatch input from the user 
     */
    private void recordProduct() throws UserInputException {
        /*
        Make all into strings, error handling done in the constructor, throws 
        UserInputException which is made for this task.
        */
        int productId = 0; 
        String qty = quantityTextField.getText().trim(); 
        String price = priceTextField.getText().trim(); 
        String name = nameTextField.getText().trim();
        String unit = unitTextField.getText().trim();
        String ingredients = ingredientsTextArea.getText().trim();
        
        //Check if ID Field is not empty 
        if (!"".equals(productIdTextField.getText())) 
            productId = Integer.parseInt(productIdTextField.getText()); 
        
        currentProduct = new Product(productId, name, qty, unit, price, ingredients);
    }
    
    /** 
     * Populates the form with values from the current List object 
     */
    private void populateForm() {
        nameTextField.setText(currentProduct.getProductName());
        quantityTextField.setText(currentProduct.getQuantity()+"");
        unitTextField.setText(currentProduct.getUnit());
        priceTextField.setText(currentProduct.getPrice()+"");
        ingredientsTextArea.setText(currentProduct.getIngredients());
        currentIndexTextField.setText(currentProductIndex+1+"");
        totalIndexTextField.setText(numberOfProducts+"");
        productIdTextField.setText(currentProduct.getProductId()+""); 
    }
    
    /** 
     * Clears all entries 
     */
    private void clear() {
        //Upon clearing, set new Product, ArrayList, and clear all fields 
        currentProduct = new Product();
        productList = new ArrayList<>();
        currentProductIndex = 0;
        numberOfProducts = 1;
        
        nameTextField.clear();
        quantityTextField.clear();
        unitTextField.clear();
        priceTextField.clear();
        ingredientsTextArea.setText("");
        productIdTextField.clear(); 
        
        //Hardcode because should always be 0 / 1
        currentIndexTextField.setText("0"); 
        totalIndexTextField.setText("1"); 
    }
    
    /**
     * Used to generate an Alert message and output to the console what is 
     * happening.
     * 
     * @param title     Title and header for the ALert. 
     * @param message   Body text to be used for the Alert 
     * @param i         1 - ERROR / 2 - CONFIRMATION 
     */
    private void exceptionOutput(String title, String message, int i) { 
        System.out.println(message); 
        Utility.alertGenerator(title, title, message, i);
    }
    
}