package common.model;

import common.UserInputException;
import common.Utility;

/**
 * Customer.java, extends Account.java. <p> 
 * This class is responsible for denoting an Account as a Customer. This governs 
 * what they can and can't do within the program. 
 * 
 * @author Brodie Lucht
 * @author Nicholas Paterno
 * @author Christopher Cox
 * @see Account
 */
public class Customer extends Account {
    private int phoneNumber ;  
    private String deliveryAddress ; 
    private int postcode;
    
    /** 
     * Empty constructor to create an empty Customer object. 
     */
    public Customer() {
        //No code
    }
    
    /** 
     * Creates Customer without the Customer ID, for first creation to be 
     * sent to the server. 
     * 
     * @param phoneNumber       Phone Number of the Customer. 
     * @param deliveryAddress   Address of the Customer. 
     * @param postcode          Postcode of the Customer. 
     * @param firstName         First name of the Customer. 
     * @param lastName          Last name of the Customer. 
     * @param emailAddress      Email address of the Customer, in format 
     *                          '[name]@[email].com'. 
     * @param password          Bytes of password. Never save plain text. 
     * @throws UserInputException   Thrown when there's an issue with input from 
     *                              the User. 
     */
    public Customer(int phoneNumber, String deliveryAddress, int postcode, String firstName, String lastName, String emailAddress, byte[] password) {
        super(firstName, lastName, emailAddress, password);
                
        if (!Utility.isValidString(deliveryAddress, 10, 100, true)) 
            throw new UserInputException("\nInvalid delivery address. \nDelivery address cannot be less than 10 characters, and cannot excede 100 characters.");
        
        this.phoneNumber = phoneNumber;
        this.deliveryAddress = deliveryAddress;
        this.postcode = postcode;
    }
    
    /** 
     * Used for error handling. So that integer values can be parsed and see if 
     * they succeed or not. Creates initial Customer to be sent to the Server. 
     * 
     * @param phoneNumberString Phone Number of the Customer. String to test if it can. 
     *                          be parsed to Int. If error then throw Exception 
     * @param deliveryAddress   Delivery address of the customer 
     * @param postcodeString    Postcode of the Customer. String to test if it can be 
     *                          parsed to Int. If error then throw Exception 
     * @param firstName         First name of the Customer 
     * @param lastName          Last name of the Customer 
     * @param emailAddress      Email address of the Customer, in '[name]@[email.com' format. 
     * @param password          Bytes of password (generally will be null) 
     * @throws UserInputException   Thrown if there is an issue with the input 
     *                              from the user 
     */
    public Customer(String phoneNumberString, String deliveryAddress, String postcodeString, String firstName, String lastName, String emailAddress, byte[] password) { 
        super(firstName, lastName, emailAddress, password);
        
        if (!Utility.isStringNumeric(phoneNumberString) || !Utility.isValidPhoneNumber(phoneNumberString)) 
            throw new UserInputException("\nInvalid phone number. \nPhone number must be an Integer value of 10 digits long."); 
        
        try {phoneNumber = Integer.parseInt(phoneNumberString); 
        } catch (NumberFormatException e) { 
            throw new UserInputException("\nInvalid phone number."
                    + "\nPhone number must be an Integer."
                    + "\nError: " + e.getMessage());}
        
        if (!Utility.isValidString(deliveryAddress, 10, 100, true)) 
            throw new UserInputException("\nInvalid delivery address. \nDelivery address cannot be less than 10 characters, and cannot excede 100 characters.");
        
        try {postcode = Integer.parseInt(postcodeString);
        } catch (NumberFormatException e) { 
            throw new UserInputException("\nInvalid postcode."
                    + "\nPostcode must be an integer."
                    + "\nError: " + e.getMessage());}
        
        this.deliveryAddress = deliveryAddress; 
    }
    
    /** 
     * Used by the server as such won't have any validation. Creates Customer 
     * and will hold all the information required from the DB. 
     * 
     * @param phoneNumber       Phone Number of the Customer 
     * @param deliveryAddress   Address of the Customer 
     * @param postcode          Postcode of the Customer 
     * @param accountId         Account ID as determined by the DB 
     * @param firstName         First name of the Customer 
     * @param lastName          Last name of the Customer 
     * @param emailAddress      Email address of the Customer, in format 
     *                          '[name]@[email].com' 
     * @param password          Bytes of password. Never save plain text 
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
