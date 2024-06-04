package common.model;

import java.io.Serializable;

/**
 * OrderItem.java holds the List<> of items for the individual Order. Initially 
 * it is created without an OrderId, but when it is created by the server, it will 
 * receive an OrderId. 
 * 
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
 */
public class OrderItem implements Serializable, Cloneable {
    private int orderId;
    private int productId;
    private int quantity;
    private double cost;
    
    /**
     * 
     */
    public OrderItem() {
        //No code 
    }
    
    /**
     * Used by client to create an Item to be sent and added to the DB. 
     * 
     * @param productId ID linked with the Product desired. 
     * @param quantity  Count of items wanted with the order 
     * @param cost      Cost of the order in $
     */
    public OrderItem(int productId, int quantity, double cost) {
        this.productId = productId;
        this.quantity = quantity;
        this.cost = cost;
    }
    
    /**
     * Created generally by the server to be sent to the client. 
     * 
     * @param orderId   ID of the Order the Order Item is linked with 
     * @param productId ID of the Product desired 
     * @param quantity  Count of items wanted with the order 
     * @param cost      Total cost of the order in $ 
     */
    public OrderItem(int orderId, int productId, int quantity, double cost) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.cost = cost;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCost() {
        return cost;
    }

}
