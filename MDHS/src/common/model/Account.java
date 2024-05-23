
package common.model;

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

    public Account() {
    }

    public Account(String emailAddress, byte[] password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }
    
    public Account(String firstName, String lastName, String emailAddress, byte[] password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
    }
    
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
