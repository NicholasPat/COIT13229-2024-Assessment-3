
package client.controller;

import client.MDHSClient;
import client.Session;
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
 * @author lucht, linke
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
    private int currentProductId; 
    
    /**
     * 
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
    private void previousIndexButtonHandler(ActionEvent event) {
        currentProductIndex--;
        if (currentProductIndex < 0 && numberOfProducts >= 0)
            currentProductIndex = numberOfProducts-1; // if index went below 0 cycle back to end of list
        if (!productList.isEmpty()) {
            currentProduct = productList.get(currentProductIndex);
            populateForm();
        }
        currentProductId = currentProduct.getProductId();
    }
    
    /**
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
        }
        currentProductId = currentProduct.getProductId(); 
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void addButtonHandler(ActionEvent event) {
        if (currentProductId != -1) { 
            alertError("Please press the 'New' button before defining a product."); 
            return; 
        }
        recordProduct();
        try {
            session.objOut.writeObject("RecordProduct");
            session.objOut.writeObject(currentProduct);
            String outcome = (String) session.objIn.readObject(); 
            if (outcome.equalsIgnoreCase("RecordProductSuccess")) {
                alertInformation("Product was successfully sent to the server and added to the DB");
            } else { 
                alertError("An error was noted with the addition of the product"); 
            }
            
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Exception while adding product: " + ex.getMessage());
            alertError("Exception while adding product:\n" + ex.getMessage()); 
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
        if (currentProductId == -1) { 
            alertError("Please search for a product first, then enter changes."); 
            return; 
        }
        try {
            session.objOut.writeObject("EditProduct");
            session.objOut.writeObject(currentProduct);
            String message = (String) session.objIn.readObject(); 
            
            if (message.equalsIgnoreCase(message)) { 
                alertInformation("Product with ID: " + currentProduct.getProductId() + 
                        ", and name: " + currentProduct.getProductName() + " was "
                        + "successful edited.");
            } else { 
                alertError("Product with ID: " + currentProduct.getProductId() + 
                        ", and name: " + currentProduct.getProductName() + " was "
                        + "unsuccessfully edited.");
            }
            
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Exception while updating product: " + ex.getMessage());
            alertError("Exception while updating the product:\n" + ex.getMessage()); 
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
        if (currentProductId == -1) { 
            alertError("Please search for a product for deletion first."); 
            return; 
        }
        
        try {
            session.objOut.writeObject("DeleteProduct");
            session.objOut.writeObject(currentProduct.getProductId());
            String message = (String) session.objIn.readObject(); 
            
            if (message.equalsIgnoreCase(message)) { 
                alertInformation("Product with ID: " + currentProduct.getProductId() + 
                        ", and name: " + currentProduct.getProductName() + " was "
                        + "successful deleted.");
            } else { 
                alertError("Product with ID: " + currentProduct.getProductId() + 
                        ", and name: " + currentProduct.getProductName() + " was "
                        + "unsuccessfully deleted.");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while deleting product: " + ex.getMessage());
            alertError("Exception occured while deleting product" + ex.getMessage());
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
        currentProductId = -1; 
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
                currentProductId = currentProduct.getProductId(); 
            } else {
                currentProduct = new Product();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while loading product list: " + ex.getMessage());
            alertError("Exception while loading product list:\n" + ex.getMessage()); 
        }
        
        for (Product p : productList) {
            System.out.println(p.toString());
        }
    }
    
    /** 
     * Records the current product for addition 
     */
    private void recordProduct() {
        int qty = 0; 
        double price = 0;
        String name = nameTextField.getText().trim();
        String unit = unitTextField.getText().trim();
        String ingredients = ingredientsTextArea.getText().trim();
        
        String errorMessage = errorHandler(name, unit, ingredients); 
        
        try { 
            qty = Integer.parseInt(quantityTextField.getText().trim());
        } catch (NumberFormatException ex) { 
            errorMessage += "Error with quantity: " + ex.getMessage() + "\n";
        }
        
        try { 
            price = Double.parseDouble(priceTextField.getText().trim());
        } catch (NumberFormatException ex) { 
            errorMessage += "Error with price: " + ex.getMessage() + "\n";
        }
        
        if (!errorMessage.equals("")) { 
            alertError(errorMessage); 
            clear();
            return;
        }
        
        currentProduct = new Product(name, qty, unit, price, ingredients);
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
        
        currentIndexTextField.setText("0"); 
        totalIndexTextField.setText("1"); 
    }
    
    /** 
     * Creates an error Alert. Saves having to create a new Alert object in each 
     * catch{} block
     * @param message 
     */
    private void alertError(String message) { 
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait(); 
    }
    
    /** 
     * Creates an information Alert. Saves having to create a new one each time 
     * @param message 
     */
    private void alertInformation(String message) { 
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
    
    private String errorHandler(String name, String unit, String ingredients) { 
        String message = ""; 
        
        if (!Utility.isEmpty(name)) 
            message += "\nProduct Name is a mandatory field.";
        
        if (!Utility.isEmpty(unit))
            message += "\nUni is a mandatory field."; 
        
        if (!Utility.isEmpty(ingredients)) 
            message += "\nIngredients is a mandatory field."; 
        
        return message; 
    }
    
}