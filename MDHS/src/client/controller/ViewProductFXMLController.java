
package client.controller;

import client.MDHSClient;
import client.Session;
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
 * @author lucht
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
    
    @Override
    public void handleSceneChange() {
        loadProducts();
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
        currentProductIndex--;
        if (currentProductIndex < 0 && numberOfProducts > 0)
            currentProductIndex = numberOfProducts-1; // if index went below 0 cycle back to end of list
        currentProduct = productlist.get(currentProductIndex);
        populateForm();
    }

    @FXML
    private void nextIndexButtonHandler(ActionEvent event) {
        currentProductIndex++;
        if ( currentProductIndex >= numberOfProducts ) // if index went above total number, then cycle back to first entry
            currentProductIndex = 0;
        currentProduct = productlist.get(currentProductIndex);
        populateForm();
    }
    
    private void loadProducts() {
        Session session = Session.getSession();
        try {
            session.objOut.writeObject("AllProducts");

            productlist = (ArrayList<Product>) session.objIn.readObject();
            if ( productlist.size() != 0 ) {
                numberOfProducts = productlist.size();
                currentProductIndex = 0;
                currentProduct = productlist.get(currentProductIndex);
                populateForm(); // display current complaint
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while loading product list: " + ex.getMessage());
        }
    }
    
    private void populateForm() {
        nameTextField.setText(currentProduct.getProductName()+"");
        quantityTextField.setText(currentProduct.getQuantity()+"");
        unitTextField.setText(currentProduct.getUnit()+"");
        priceTextField.setText("$"+currentProduct.getPrice());
        ingredientsTextArea.setText(currentProduct.getIngredients()+"");
        currentIndexTextField.setText(currentProductIndex+1+"");
        totalIndexTextField.setText(numberOfProducts+"");
    }
    
    private void clear() {
        nameTextField.clear();
        quantityTextField.clear();
        unitTextField.clear();
        priceTextField.clear();
        ingredientsTextArea.clear();
    }
}
