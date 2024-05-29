
package common.model;

import java.io.Serializable;

/**
 *
 * @author lucht
 */
public class OrderItem implements Serializable, Cloneable {
    private int orderId;
    private int productId;
    private int quantity;
    private double cost;

    public OrderItem() {
    }

    public OrderItem(int productId, int quantity, double cost) {
        this.productId = productId;
        this.quantity = quantity;
        this.cost = cost;
    }
    
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
