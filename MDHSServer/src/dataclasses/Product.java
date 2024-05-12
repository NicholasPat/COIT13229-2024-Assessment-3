package dataclasses;

/**
 *
 * @author linke
 */
public class Product {
    private int productId ; 
    private String productName ; 
    private String unit ; 
    private int totalQuantity ; 
    private double price ; 
    private String ingredients ; 

    /** 
     * Used with reading in the products to get the Id, used for some queries 
     * 
     * @param productId
     * @param productName
     * @param unit
     * @param totalQuantity
     * @param price
     * @param ingredients 
     */
    public Product(int productId, String productName, String unit, int totalQuantity, double price, String ingredients) {
        this.productId = productId;
        this.productName = productName;
        this.unit = unit;
        this.totalQuantity = totalQuantity;
        this.price = price;
        this.ingredients = ingredients;
    }

    
    
    public Product(String productName, String unit, int totalQuantity, double price, String ingredients) {
        this.productName = productName;
        this.unit = unit;
        this.totalQuantity = totalQuantity;
        this.price = price;
        this.ingredients = ingredients;
    }

    public Product() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    
    
}
