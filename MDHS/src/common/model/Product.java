package common.model;

import common.UserInputException;
import common.Utility;
import java.io.Serializable;

/**
 * Product.java is responsible for holding the information of a Product. This is 
 * used by the server and client to pass Product information between each other. 
 * 
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
 * @see Serializable
 */
public class Product implements Serializable {
    private int productId ; 
    private String productName ; 
    private int quantity;
    private String unit ; 
    private double price ; 
    private String ingredients ; 
    
    /** 
     * Creates an empty Product object. 
     */
    public Product() {
        //No code 
    }
    
    /**
     * Generates a Product object, also has error handling implemented to ensure 
     * correct variables are passed. 
     * 
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
        
        this.price = price;
        this.quantity = quantity;
        this.productName = productName;
        this.unit = unit;
        this.ingredients = ingredients;
    }
    
    /** 
     * Constructor used by the client (Administrator) to add a product, hence the 
     * error handling. 
     * 
     * @param productName       Name of the Product item 
     * @param quantityString    Max quantity of the Product item 
     * @param unit              Unit of the item (not total) 
     * @param priceString       Price of the individual units of the item 
     * @param ingredients       List of ingredients for them item 
     */
    public Product (String productName, String quantityString, String unit, String priceString, String ingredients) { 
        if (!Utility.isValidString(productName, 5, 30, true))
            throw new UserInputException("\nProduct name is invalid.\nPlease input a name more than 5 characters and less than 30 characters.");
                
        if (!Utility.isValidString(unit, 1, 10, true)) 
            throw new UserInputException("\nUnit is invalid.\nPlease input a unit more than 1 characters and less than 10 characters.");
        
        if (!Utility.isValidString(ingredients, 1, 200, true))
            throw new UserInputException("\nIngredients are not valid.\nPlease input an ingredient list of more than 5 characters and less than 200 characters.");
        
        if (ingredients.contains(",")) 
            throw new UserInputException("\nIngredients list should not be separated by anything. Leave without commas please."); 
        
        try {quantity = Integer.parseInt(quantityString); 
        } catch (NumberFormatException e) {throw new UserInputException("\nQuantity must be a whole number.");}
        
        try {price = Integer.parseInt(priceString);
        } catch (NumberFormatException e) {throw new UserInputException("\nPrice must be a whole number, or a number with a decimal place.");}
        
        this.productName = productName; 
        this.unit = unit; 
        this.ingredients = ingredients; 
        
    }
    
    /** 
     * Used by the server to create a Product object. No error handling needed. 
     * 
     * @param productId     Product ID as defined by the server 
     * @param productName   Name of the Product item 
     * @param quantity      Max quantity of the item available 
     * @param unit          Unit of the item (gm, mL, L, etc) 
     * @param price         Price of each unit in ($) 
     * @param ingredients   List of ingredients, not separated via ',' 
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
