package common.model;

import common.UserInputException;
import common.Utility;

/**
 *
 * @author lucht
 */
public class Customer extends Account {
    private int phoneNumber ;  
    private String deliveryAddress ; 
    private int postcode;
    
    /** 
     * 
     */
    public Customer() {
    }
    
    /** 
     * 
     * 
     * @param phoneNumber
     * @param deliveryAddress
     * @param postcode
     * @param firstName
     * @param lastName
     * @param emailAddress
     * @param password 
     */
    public Customer(int phoneNumber, String deliveryAddress, int postcode, String firstName, String lastName, String emailAddress, byte[] password) {
        super(firstName, lastName, emailAddress, password);
                
        if (!Utility.isValidString(deliveryAddress, 0, 100, true)) 
            throw new UserInputException("Invalid delivery address. \nDelivery address cannot excede 100 characters.");
        
        this.phoneNumber = phoneNumber;
        this.deliveryAddress = deliveryAddress;
        this.postcode = postcode;
    }
    
    /** 
     * Used by the server so won't have any validation. 
     * 
     * @param phoneNumber
     * @param deliveryAddress
     * @param postcode
     * @param accountId
     * @param firstName
     * @param lastName
     * @param emailAddress
     * @param password 
     */
    public Customer(int phoneNumber, String deliveryAddress, int postcode, int accountId, String firstName, String lastName, String emailAddress, byte[] password) {
        super(accountId, firstName, lastName, emailAddress, password);
        this.phoneNumber = phoneNumber;
        this.deliveryAddress = deliveryAddress;
        this.postcode = postcode;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public int getPostcode() {
        return postcode;
    }

    
}
