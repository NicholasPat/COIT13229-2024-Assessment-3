package dataclasses;

/**
 * This class is used to create an object with all of the customer's orders which 
 * will then be placed into the TableView in Orders.fxml 
 * 
 * @author linke
 */
public class OrderInformation {
    private int deliveryId ; 
    private String deliveryDay ; 
    private String deliveryTime ; 
    private double deliveryCost ; 
    private String[] products ; 
    private int orderId ; 

    public OrderInformation(int deliveryId, String deliveryDay, String deliveryTime, double deliveryCost, int orderId) {
        this.deliveryId = deliveryId;
        this.deliveryDay = deliveryDay;
        this.deliveryTime = deliveryTime;
        this.deliveryCost = deliveryCost;
        this.orderId = orderId; 
    }

    public OrderInformation() {
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    } 
    
    public String getDeliveryDay() {
        return deliveryDay;
    }

    public void setDeliveryDay(String deliveryDay) {
        this.deliveryDay = deliveryDay;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public String[] getProducts() {
        return products;
    }

    public void setProducts(String[] products) {
        this.products = products;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    
}
