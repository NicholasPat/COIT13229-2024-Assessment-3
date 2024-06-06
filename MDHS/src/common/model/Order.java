package common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the information of a Customer's Order 
 * 
 * @author Brodie Lucht 
 * @author Nicholas Paterno
 * @author Christopher Cox 
 */
public class Order implements Serializable {
    private int orderId;
    private int customerId;
    private String deliveryTime;
    private List<OrderItem> productList;
    private double totalCost;
    
    /** 
     * Empty constructor. 
     */
    public Order() {
        //No code 
    }
    
    /** 
     * Constructs an Order 
     * 
     * @param customerId    ID of Customer 
     * @param deliveryTime  Time of delivery (24 hour format) 
     * @param productList   List of OrderItems 
     */
    public Order(int customerId, String deliveryTime, List<OrderItem> productList) {
        this.customerId = customerId;
        this.deliveryTime = deliveryTime;
        this.productList = productList;
    }
    
    /** 
     * Constructs an Order 
     * 
     * @param customerId    ID of Customer 
     * @param deliveryTime  Time of delivery (24 hour format) 
     * @param productList   List of OrderItems 
     * @param totalCost     Total cost of the order 
     */
    public Order(int customerId, String deliveryTime, List<OrderItem> productList, double totalCost) {
        this.customerId = customerId;
        this.deliveryTime = deliveryTime;
        this.productList = productList;
        this.totalCost = totalCost;
    }
    
    /** 
     * Constructs an Order 
     * 
     * @param orderId       ID of the Order 
     * @param customerId    ID of the Customer tied to the Order 
     * @param deliveryTime  Time of delivery (24 hour format) 
     * @param productList   List of OrderItems 
     * @param totalCost     Total cost of the order (subject to variance) 
     */
    public Order(int orderId, int customerId, String deliveryTime, List<OrderItem> productList, double totalCost) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.deliveryTime = deliveryTime;
        this.productList = productList;
        this.totalCost = totalCost;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
    
    public void setProductList(ArrayList<OrderItem> productList) {
        this.productList = productList;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public String getDeliveryTime() {
        return deliveryTime;
    }
    
    public List<OrderItem> getProductList() {
        return productList;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    
}
