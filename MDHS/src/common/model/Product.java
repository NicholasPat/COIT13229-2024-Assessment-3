package common.model;

/**
 *
 * @author linke
 */
public class Product {
    private int productId ; 
    private String productName ; 
    private int quanity;
    private String unit ; 
    private double price ; 
    private String ingredients ; 

    public Product() {
    }

    public Product(String productName, int quanity, String unit, double price, String ingredients) {
        this.productName = productName;
        this.quanity = quanity;
        this.unit = unit;
        this.price = price;
        this.ingredients = ingredients;
    }

    public Product(int productId, String productName, int quanity, String unit, double price, String ingredients) {
        this.productId = productId;
        this.productName = productName;
        this.quanity = quanity;
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

    public void setQuanity(int quanity) {
        this.quanity = quanity;
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

    public int getQuanity() {
        return quanity;
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
