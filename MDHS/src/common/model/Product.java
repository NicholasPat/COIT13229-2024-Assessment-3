package common.model;

import common.UserInputException;
import common.Utility;
import java.io.Serializable;

/**
 *
 * @author lucht, linke
 */
public class Product implements Serializable {
    private int productId ; 
    private String productName ; 
    private int quantity;
    private String unit ; 
    private double price ; 
    private String ingredients ; 
    
    /** 
     * 
     */
    public Product() {
    }
    
    /**
     * Generates a Product object, also has error handling implemented to ensure 
     * correct variables are passed 
     * @param productName
     * @param quantity
     * @param unit
     * @param price
     * @param ingredients 
     */
    public Product(String productName, int quantity, String unit, double price, String ingredients) {
        if (!Utility.isValidString(productName, 1, 30, true))
            throw new UserInputException("Product name is invalid.\nPlease input a name less than 30 characters.");
        
        if (!Utility.isValidString(unit, 1, 10, true)) 
            throw new UserInputException("Unit is invalid.\nPlease input a unit less than 10 characters.");
        
        if (!Utility.isValidString(ingredients, 1, 200, true))
            throw new UserInputException("Ingredients are not valid.\nPlease input a unit less than 200 characters.");
        
        //Error handling for price and quantity are not done here, since they are 
        //cast as double and int, they must be error handled in the controllers 
        this.price = price;
        this.quantity = quantity;
        this.productName = productName;
        this.unit = unit;
        this.ingredients = ingredients;
    }
    
    /** 
     * Used by the server to create the object. No error handling needed 
     * @param productId
     * @param productName
     * @param quantity
     * @param unit
     * @param price
     * @param ingredients 
     */
    public Product(int productId, String productName, int quantity, String unit, double price, String ingredients) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.ingredients = ingredients;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public double getPrice() {
        return price;
    }

    public String getIngredients() {
        return ingredients;
    }

    @Override
    public String toString() {
        return productName + " (" + quantity + unit + ')';
    }

}
