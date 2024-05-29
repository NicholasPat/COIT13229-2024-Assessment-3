
package common.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author lucht
 */
public class Order implements Serializable {
    private int orderID;
    private int customerID;
    private String deliveryTime;
    private ArrayList<OrderItem> productList;
    private double totalCost;

    public Order() {
    }

    public Order(int customerID, String deliveryTime, ArrayList<OrderItem> productList) {
        this.customerID = customerID;
        this.deliveryTime = deliveryTime;
        this.productList = productList;
    }
    
    public Order(int customerID, String deliveryTime, ArrayList<OrderItem> productList, double totalCost) {
        this.customerID = customerID;
        this.deliveryTime = deliveryTime;
        this.productList = productList;
        this.totalCost = totalCost;
    }

    public Order(int orderID, int customerID, String deliveryTime, ArrayList<OrderItem> productList, double totalCost) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.deliveryTime = deliveryTime;
        this.productList = productList;
        this.totalCost = totalCost;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
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

    public int getOrderID() {
        return orderID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public ArrayList<OrderItem> getProductList() {
        return productList;
    }

    public double getTotalCost() {
        return totalCost;
    }
    
    
}
