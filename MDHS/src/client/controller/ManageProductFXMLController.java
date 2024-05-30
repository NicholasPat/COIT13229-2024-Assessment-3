
package client.controller;

import client.MDHSClient;
import client.Session;
import common.UserInputException;
import common.model.DeliverySchedule;
import common.model.Order;
import common.model.OrderItem;
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

    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }

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

    @FXML
    private void addButtonHandler(ActionEvent event) {
        recordProduct();
        try {
            session.objOut.writeObject("RecordProduct");
            session.objOut.writeObject(currentProduct);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while adding product: " + ex.getMessage());
        }

        clear();
        loadProducts();
        populateForm();  
    }

    @FXML
    private void updateButtonHandler(ActionEvent event) {
        recordProduct();
        
        try {
            session.objOut.writeObject("RecordProduct");
            session.objOut.writeObject(currentProduct);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while updating product: " + ex.getMessage());
        }
        
        clear();
        loadProducts();
        populateForm();  
    }

    @FXML
    private void deleteButtonHandler(ActionEvent event) {
        recordProduct();
        
        try {
            session.objOut.writeObject("DeleteProduct");
            session.objOut.writeObject(currentProduct);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while deleting product: " + ex.getMessage());
        }
        
        clear();
        loadProducts();
        populateForm();  
    }


    @FXML
    private void newButtonHandler(ActionEvent event) {
        clear();
        loadProducts();
        try {
            numberOfProducts++;
            currentProductIndex = numberOfProducts-1;
            currentProduct = new Product();
            populateForm();
        } catch (UserInputException ie) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ie.getMessage());   
            alert.showAndWait();
        } 
    }
    
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
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while loading product list: " + ex.getMessage());
        }
        
        for (Product p : productList) {
            System.out.println(p.toString());
        }
    }
    
    private void recordProduct() {
        String name = nameTextField.getText().trim();
        int qty = Integer.parseInt(quantityTextField.getText().trim());
        String unit = unitTextField.getText().trim();
        double price = Double.parseDouble(priceTextField.getText().trim());
        String ingredients = ingredientsTextArea.getText().trim();
        
        currentProduct = new Product(name, qty, unit, price, ingredients);
    }
    
    private void populateForm() {
        nameTextField.setText(currentProduct.getProductName());
        quantityTextField.setText(currentProduct.getQuantity()+"");
        unitTextField.setText(currentProduct.getUnit());
        priceTextField.setText(currentProduct.getPrice()+"");
        ingredientsTextArea.setText(currentProduct.getIngredients());
        currentIndexTextField.setText(currentProductIndex+1+"");
        totalIndexTextField.setText(numberOfProducts+"");
    }
    
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
        
    }
}
