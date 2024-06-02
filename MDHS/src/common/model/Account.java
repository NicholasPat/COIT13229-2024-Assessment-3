
package common.model;

import common.UserInputException;
import common.Utility;
import java.io.Serializable;

/**
 *
 * @author lucht
 */
public class Account implements Serializable {
    private int accountId;
    private String firstName ; 
    private String lastName ; 
    private String emailAddress ;
    private byte[] password ;

    /** 
     * To generate an empty Account class
     */
    public Account() {
    }

    /** 
     * This is used for the Customer login, to make as a general account. Would 
     * be replaced with proper instance. Error handling implemented in the 
     * LoginFXMLController.java class. 
     * 
     * @param emailAddress
     * @param password 
     */
    public Account(String emailAddress, byte[] password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }
    
    /** 
     * Used by customer to create first time account. This is to then send to the 
     * server. Customer specific construction is in the Customer.java child class. 
     * 
     * @param firstName
     * @param lastName
     * @param emailAddress
     * @param password 
     */
    public Account(String firstName, String lastName, String emailAddress, byte[] password) {
        if (!Utility.isValidString(firstName, 0, 30, true)) 
            throw new UserInputException("Invalid first name. \nFirst name cannot excede 30 characters."); 
        
        if (!Utility.isValidString(lastName, 0, 30, true))
            throw new UserInputException("Invalid last name. \nLast name cannot excede 30 characters."); 
        
        if (!Utility.isValidString(emailAddress, 0, 100, true)) 
            throw new UserInputException("Invalid email address. \nEmail address cannot excede 100 characters.");
        if (!Utility.isValidEmail(emailAddress)) 
            throw new UserInputException("Invalid email address. \nEmail address must contain an '@' symbol.");
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
    }
    
    /** 
     * Since used by server, wouldn't need to try-catch with validation 
     * 
     * @param accountId
     * @param firstName
     * @param lastName
     * @param emailAddress
     * @param password 
     */
    public Account(int accountId, String firstName, String lastName, String emailAddress, byte[] password) {
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public byte[] getPassword() {
        return password;
    }
    
    
}
