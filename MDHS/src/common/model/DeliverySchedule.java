
package common.model;

import common.UserInputException;
import java.io.Serializable;

/**
 *
 * @author lucht
 */
public class DeliverySchedule implements Serializable {
    private int postcode;
    private String deliveryDay;
    private double deliveryCost;
    
    /** 
     * Empty constructor, makes an object with no properties 
     */
    public DeliverySchedule() {
    }
    
    /** 
     * Used to set a day's Delivery Schedule. 
     * 
     * @param postcode
     * @param deliveryDay
     * @param deliveryCost 
     */
    public DeliverySchedule(int postcode, String deliveryDay, double deliveryCost) {
        this.postcode = postcode;
        this.deliveryDay = deliveryDay;
        this.deliveryCost = deliveryCost;
    }
    
    public DeliverySchedule(String postcode, String deliveryDay, String deliveryCost) { 
        try {this.postcode = Integer.parseInt(postcode);} catch (NumberFormatException e) { 
            throw new UserInputException("\nPostcode must be an Integer value.");}
        
        try {this.deliveryCost = Double.parseDouble(deliveryCost);} catch (NumberFormatException e) { 
            throw new UserInputException("\nDeliveryCost must be an Integer value, or a decimal value.");}
        
        this.deliveryDay = deliveryDay; 
        
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    public void setDeliveryDay(String deliveryDay) {
        this.deliveryDay = deliveryDay;
    }

    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public int getPostcode() {
        return postcode;
    }

    public String getDeliveryDay() {
        return deliveryDay;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }
    
    
}
