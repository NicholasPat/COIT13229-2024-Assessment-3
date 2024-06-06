package client.controller;

import client.MDHSClient;
import client.Session;
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
 * FXML Controller class. <p> 
 * This Controller is responsible for controller the view for ViewProductFXML.fxml. 
 * It displays all the Products saved to the DataBase, if none, it will throw an 
 * Alert and return to the Dashboard. 
 * 
 * @author Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox
 */
public class ViewProductFXMLController implements Initializable, SceneController {

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
    
    private List<Product> productlist;
    private Product currentProduct;
    private int currentProductIndex;
    private int numberOfProducts;
    
    /**
     * Handles the changing between scenes. Loads the product information from 
     * the server. 
     */
    @Override
    public void handleSceneChange() {
        loadProducts();
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
     * When pressed, sets the scene back to the Dashboard. 
     * 
     * @param event 
     */
    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    /** 
     * Cycles back through the entries. If already at the minimum entry, cycle 
     * forward to the last entry. No error handling due to the fact that there 
     * is no editing, no input from the user so it isn't a necessary fucntion. 
     * 
     * @param event 
     */
    @FXML
    private void previousIndexButtonHandler(ActionEvent event) {
        currentProductIndex--;
        if (currentProductIndex < 0 && numberOfProducts > 0)
            currentProductIndex = numberOfProducts-1; // if index went below 0 cycle back to end of list
        currentProduct = productlist.get(currentProductIndex);
        populateForm();
    }
    
    /** 
     * Cycles forward through the entries. If already at maximum entry, cycle back 
     * around to the first. No error handling due to the fact that there 
     * is no editing, no input from the user so it isn't a necessary fucntion. 
     * 
     * @param event 
     */
    @FXML
    private void nextIndexButtonHandler(ActionEvent event) {
        currentProductIndex++;
        if ( currentProductIndex >= numberOfProducts ) // if index went above total number, then cycle back to first entry
            currentProductIndex = 0;
        currentProduct = productlist.get(currentProductIndex);
        populateForm();
    }
    
    /** 
     * Gets all the products from the Server and outputs their information into 
     * a cycle system. 
     */
    private void loadProducts() {
        Session session = Session.getSession();
        try {
            //Write out to server code for getting all Product list. Receive it back 
            session.objOut.writeObject("AllProducts");
            productlist = (ArrayList<Product>) session.objIn.readObject();
            
            if ( !productlist.isEmpty() ) {
                numberOfProducts = productlist.size();
                currentProductIndex = 0;
                currentProduct = productlist.get(currentProductIndex);
                populateForm(); // display current product
                
            } else { 
                //If the productList was indeed empty
                String message = "No products exist at the moment, please return another time!"; 
                Utility.alertGenerator("No Products exist!", "No Products exist!", message, 2);
                MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            String message = "Exception while loading the Product list:\n" + ex.getMessage();
            Utility.alertGenerator("Exception occurred!", 
                    "Exception occurred!", message, 1);
        }
    }
    
    /** 
     * Populates the cycle-able form with the information from the currently 
     * selected Product object. 
     */
    private void populateForm() {
        nameTextField.setText(currentProduct.getProductName()+"");
        quantityTextField.setText(currentProduct.getQuantity()+"");
        unitTextField.setText(currentProduct.getUnit()+"");
        priceTextField.setText("$"+currentProduct.getPrice());
        ingredientsTextArea.setText(currentProduct.getIngredients()+"");
        currentIndexTextField.setText(currentProductIndex+1+"");
        totalIndexTextField.setText(numberOfProducts+"");
    }
}
