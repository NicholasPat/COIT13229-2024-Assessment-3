
package common.model;

import common.UserInputException;
import java.io.Serializable;

/**
 * 
 * 
 * @author Brodie Lucht 
 * @author Nicholas Paterno
 * @author Christopher Cox 
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
     * Used to set a day's Delivery Schedule. Mainly used by the server, so no 
     * error handling needed. 
     * 
     * @param postcode      Postcode for the delivery location. Primary key 
     * @param deliveryDay   Day of delivery, Monday, Tuesday, etc. 
     * @param deliveryCost  Cost of the delivery in $ 
     */
    public DeliverySchedule(int postcode, String deliveryDay, double deliveryCost) {
        this.postcode = postcode;
        this.deliveryDay = deliveryDay;
        this.deliveryCost = deliveryCost;
    }
    
    /** 
     * Main constructor for determining if the details from the client is accurate 
     * and correct. 
     * 
     * @param postcode
     * @param deliveryDay
     * @param deliveryCost 
     */
    public DeliverySchedule(String postcode, String deliveryDay, String deliveryCost) { 
        try {this.postcode = Integer.parseInt(postcode);} catch (NumberFormatException e) { 
            throw new UserInputException("\nPostcode must be an Integer value.");}
        
        try {this.deliveryCost = Double.parseDouble(deliveryCost);} catch (NumberFormatException e) { 
            throw new UserInputException("\nDeliveryCost must be an Integer value, or a decimal value.");}
        
        //Should always be a value, won't error handle for it 
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
