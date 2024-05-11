package MainPackage;

/**
 * This class is used to create an object with all of the customer's orders which 
 * will then be placed into the TableView in Orders.fxml 
 * 
 * @author linke
 */
public class OrderInformation {
    private String deliveryDay ; 
    private String deliveryTime ; 
    private double deliveryCost ; 
    private String address ; 
    private String[] products ; 

    public OrderInformation(String deliveryDay, String deliveryTime, double deliveryCost, String address, String[] products) {
        this.deliveryDay = deliveryDay;
        this.deliveryTime = deliveryTime;
        this.deliveryCost = deliveryCost;
        this.address = address;
        this.products = products;
    }

    public OrderInformation() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String[] getProducts() {
        return products;
    }

    public void setProducts(String[] products) {
        this.products = products;
    }
    
    
}
