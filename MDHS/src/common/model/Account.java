package common.model;

import common.UserInputException;
import common.Utility;
import java.io.Serializable;

/**
 * Account.java, super class of Customer.java and Administrator.java. Also implements 
 * Serializable. <p>
 * This class is responsible for assigning Accounts to users. The subclasses 
 * restrict what each of the users can do. 
 * 
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
 * @see Serializable 
 * @see Administrator 
 * @see Customer 
 */
public class Account implements Serializable {
    private int accountId;
    private String firstName ; 
    private String lastName ; 
    private String emailAddress ;
    private byte[] password ;

    /** 
     * To generate an empty Account class. 
     */
    public Account() {
        //No code
    }

    /** 
     * This is used for the Customer login, to make as a general Account. Would 
     * be replaced with proper instance. 
     * 
     * @param emailAddress  Email address linked to account to check for. 
     * @param password      Password in bytes to match with email customer retrieval. 
     */
    public Account(String emailAddress, byte[] password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }
    
    /** 
     * Used by Account holder to create first time account. This is to then send to the 
     * server. Customer specific construction is in the Customer.java child class. 
     * Admin specific construction is in the Administrator.java class. 
     * 
     * @param firstName     First name of the Account holder. 
     * @param lastName      Last name of the Account holder. 
     * @param emailAddress  Email address of the Account holder. 
     * @param password      Password of the Account holder. 
     * @throws UserInputException   Thrown when there's an issue with the input 
     *                              from the User. 
     */
    public Account(String firstName, String lastName, String emailAddress, byte[] password) {
        if (!Utility.isValidString(firstName, 3, 30, true)) 
            throw new UserInputException("\nInvalid first name.\nFirst name must be at least 3 characters and cannot excede 30 characters."); 
        
        if (!Utility.isValidString(lastName, 3, 30, true))
            throw new UserInputException("\nInvalid last name.\nLast name must be at least 3 characters and cannot excede 30 characters."); 
        
        if (!Utility.isValidString(emailAddress, 10, 100, true)) 
            throw new UserInputException("\nInvalid email address.\nEmail address must be at least 10 characters and cannot excede 100 characters.");
        if (!Utility.isValidEmail(emailAddress)) 
            throw new UserInputException("\nInvalid email address.\nEmail address must contain an '@' symbol.");
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
    }
    
    /** 
     * Since used by server, wouldn't need to try-catch with validation. 
     * 
     * @param accountId     Account Id as determined by the DB. 
     * @param firstName     First name of the Account holder. 
     * @param lastName      Last name of the Account holder. 
     * @param emailAddress  Email address of the Account holder. 
     * @param password      Password of the Account holder. 
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
