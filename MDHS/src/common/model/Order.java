/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common.model;

import java.util.ArrayList;

/**
 *
 * @author lucht
 */
public class Order {
    private int orderID;
    private int customerID;
    private int postcode;
    private String deliveryTime;
    private ArrayList<OrderItem> productList;
    private double totalCost;

    public Order() {
    }

    public Order(int customerID, int postcode, String deliveryTime, ArrayList<OrderItem> productList) {
        this.customerID = customerID;
        this.postcode = postcode;
        this.deliveryTime = deliveryTime;
        this.productList = productList;
    }
    
    public Order(int customerID, int postcode, String deliveryTime, ArrayList<OrderItem> productList, double totalCost) {
        this.customerID = customerID;
        this.postcode = postcode;
        this.deliveryTime = deliveryTime;
        this.productList = productList;
        this.totalCost = totalCost;
    }

    public Order(int orderID, int customerID, int postcode, String deliveryTime, ArrayList<OrderItem> productList, double totalCost) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.postcode = postcode;
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

    public void setPostcode(int postcode) {
        this.postcode = postcode;
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

    public int getPostcode() {
        return postcode;
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
