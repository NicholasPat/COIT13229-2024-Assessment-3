package common.model;

/**
 *
 * @author lucht
 */
public class Customer extends Account {
    private int phoneNumber ;  
    private String deliveryAddress ; 
    private int postcode;

    public Customer() {
    }

    public Customer(int phoneNumber, String deliveryAddress, int postcode, String firstName, String lastName, String emailAddress, byte[] password) {
        super(firstName, lastName, emailAddress, password);
        this.phoneNumber = phoneNumber;
        this.deliveryAddress = deliveryAddress;
        this.postcode = postcode;
    }

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
