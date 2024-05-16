package common.model;

/**
 *
 * @author lucht
 */
public class Customer extends Account {
    private int phoneNumber ;  
    private String deliveryAddress ; 

    public Customer() {
    }

    public Customer(int phoneNumber, String deliveryAddress, String firstName, String lastName, String emailAddress, String password) {
        super(firstName, lastName, emailAddress, password);
        this.phoneNumber = phoneNumber;
        this.deliveryAddress = deliveryAddress;
    }
    
    public Customer(int phoneNumber, String deliveryAddress, int accountId, String firstName, String lastName, String emailAddress, String password) {
        super(accountId, firstName, lastName, emailAddress, password);
        this.phoneNumber = phoneNumber;
        this.deliveryAddress = deliveryAddress;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    
    
    

}
