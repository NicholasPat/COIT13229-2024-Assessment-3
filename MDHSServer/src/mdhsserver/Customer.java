package mdhsserver;

/**
 *
 * @author linke
 */
public class Customer {
    private int customerId ; 
    private String firstName ; 
    private String lastName ; 
    private String userName ; 
    private int phoneNumber ; 
    private String emailAddress ; 
    private String password ; //NOTE! PASSWORD WILL BE ENCRYPTED SO THIS WILL CHANGE. THIS IS TEMP 
    private String deliveryAddress ; 

    public Customer(int customerId, String firstName, String lastName, String userName, int phoneNumber, String emailAddress, String password, String deliveryAddress) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.password = password;
        this.deliveryAddress = deliveryAddress;
    }

    public Customer() {
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public String toString() {
        return "Customer{" + "customerId=" + customerId + ", firstName=" + firstName + ", lastName=" + lastName + ", userName=" + userName + ", phoneNumber=" + phoneNumber + ", emailAddress=" + emailAddress + ", password=" + password + ", deliveryAddress=" + deliveryAddress + '}';
    }
    
    
}
