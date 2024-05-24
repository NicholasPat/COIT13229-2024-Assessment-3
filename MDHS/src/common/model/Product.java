package common.model;

import java.io.Serializable;

/**
 *
 * @author linke
 */
public class Product  implements Serializable {
    private int productId ; 
    private String productName ; 
    private int quantity;
    private String unit ; 
    private double price ; 
    private String ingredients ; 

    public Product() {
    }

    public Product(String productName, int quantity, String unit, double price, String ingredients) {
        this.productName = productName;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.ingredients = ingredients;
    }

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

    
}
