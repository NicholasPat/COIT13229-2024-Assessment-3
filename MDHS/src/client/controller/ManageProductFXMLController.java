
package client.controller;

import client.MDHSClient;
import client.Session;
import common.UserInputException;
import common.Utility;
import common.model.Product;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
    
    private Session session; 
    
    private Product currentProduct;
    private List<Product> productList;
    private int currentProductIndex;
    private int numberOfProducts;
    
    /**
     * Handles the change to this scene. Clears logs and gets current list from server. 
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
        }
    }
    
    /**
     * Cycles forward through the entries. 
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
        }
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void addButtonHandler(ActionEvent event) {
        if (!"".equals(productIdTextField.getText())) { 
            String error = "Please click on 'New' first."; 
            exceptionOutput("Notice!", error, 2); 
            return; 
        }
        
        try {
            recordProduct();
            session.objOut.writeObject("RecordProduct");
            session.objOut.writeObject(currentProduct);
            String outcome = (String) session.objIn.readObject(); 
            if (outcome.equalsIgnoreCase("RecordProductSuccess")) {
                String outcomeString = "Product was successfully recorded!"; 
                String title = "Successful addition of product!"; 
                exceptionOutput(title, outcomeString, 2); 
                
            } else { 
                String outcomeString = "Product was unsuccessfully recorded!"; 
                String title = "Unsuccessful addition of product!"; 
                exceptionOutput(title, outcomeString, 1); 
            }
            
        } catch (UserInputException e) { 
            String message = "Input mismatch occured!: " + e.getMessage(); 
            String title = "Input mismatch!"; 
            exceptionOutput(title, message, 1); 
            return; 
            
        } catch (Exception ex) {
            String message = "General Exception occured!\n" + ex.getMessage(); 
            String title = "General Exception occurted!"; 
            exceptionOutput(title, message, 1); 
            return; 
        }
        clear();
        loadProducts();
        populateForm();  
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void updateButtonHandler(ActionEvent event) {
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
                String title = "Successful edit of product!"; 
                exceptionOutput(title, outcomeString, 2); 
                
            } else { 
                String outcomeString = "Product was unsuccessfully edited!"; 
                String title = "Unsuccessful edit of product!"; 
                exceptionOutput(title, outcomeString, 1); 
            }
            
        } catch (UserInputException e) { 
            String message = "Input mismatch occured!: " + e.getMessage(); 
            String title = "Input mismatch!"; 
            exceptionOutput(title, message, 1); 
            
        } catch (Exception ex) {
            String message = "General Exception occured!\n" + ex.getMessage(); 
            String title = "General Exception occurted!"; 
            exceptionOutput(title, message, 1); 
        }
        
        clear();
        loadProducts();
        populateForm();  
    }
    
    /** 
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
        
        try {
            recordProduct(); 
            session.objOut.writeObject("DeleteProduct");
            session.objOut.writeObject(currentProduct.getProductId());
            String message = (String) session.objIn.readObject(); 
            
            if (message.equalsIgnoreCase("DeleteProductSuccess")) { 
                String outcomeString = "Product was successfully deleted!"; 
                String title = "Successful deletion of product!"; 
                exceptionOutput(title, outcomeString, 2); 
                
            } else { 
                String outcomeString = "Product was unsuccessfully deleted!"; 
                String title = "Unsuccessful deletion of product!"; 
                exceptionOutput(title, outcomeString, 1); 
            }
            
        } catch (UserInputException e) { 
            String message = "Input mismatch occured!: " + e.getMessage(); 
            String title = "Input mismatch!"; 
            exceptionOutput(title, message, 1); 
            
        } catch (Exception ex) {
            String message = "General Exception occured!\n" + ex.getMessage(); 
            String title = "General Exception occurted!"; 
            exceptionOutput(title, message, 1); 
        }
        
        clear();
        loadProducts();
        populateForm();  
    }

    /** 
     * Simply will reset the text fields and set Id to a negative value. This 
     * should prevent errors with accidental Update and Deletion button presses. 
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
            session.objOut.writeObject("AllProducts");
            productList = (ArrayList<Product>) session.objIn.readObject();
            
            numberOfProducts = productList.size();
            if (numberOfProducts > 0) {
                currentProductIndex = 0;
                currentProduct = productList.get(currentProductIndex);
            } else {
                currentProduct = new Product();
            }
        } catch (UserInputException e) { 
            String message = "Input mismatch occured!: " + e.getMessage(); 
            String title = "Input mismatch!"; 
            exceptionOutput(title, message, 1); 
            
        } catch (Exception ex) {
            String message = "General Exception occured!\n" + ex.getMessage(); 
            String title = "General Exception occurted!"; 
            exceptionOutput(title, message, 1); 
        }
        
        //for (Product p : productList) {
            //System.out.println(p.toString());
       // }
    }
    
    /** 
     * Records the current product selected
     * @throws UserInputException 
     */
    private void recordProduct() throws UserInputException {
        //Make all into strings, error handling done in the constructor 
        int productId = 0; 
        String qty = quantityTextField.getText().trim(); 
        String price = priceTextField.getText().trim(); 
        String name = nameTextField.getText().trim();
        String unit = unitTextField.getText().trim();
        String ingredients = ingredientsTextArea.getText().trim();
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
        
        currentIndexTextField.setText("0"); 
        totalIndexTextField.setText("1"); 
    }
    
    private void exceptionOutput(String title, String message, int i) { 
        System.out.println(message); 
        Utility.alertGenerator(title, title, message, i);
    }
    
}