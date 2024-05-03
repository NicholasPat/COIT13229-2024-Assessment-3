package MainPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javafx.scene.control.Alert;

/**
 * This class is used like a session handler, or a cookie. It will hold current
 * information sent from the server plus any other flags needed. Will share 
 * commonalities with the Customer class on the server side. Will be initialised 
 * by the MdhsClient.java class (application) and then passed to the controllers 
 * as they are initialised. This handles the issue of needing to pass session 
 * information between classes 
 * 
 * @author linke
 */
public class CurrentSession {
    /*Customer information*/
    private int customerId ; 
    private String firstName ; 
    private String lastName ; 
    private String username ; 
    private int phoneNumber ; 
    private String emailAddress ; 
    private String password ; 
    private String deliveryAddress ; 
    
    /*Flags and necessary information*/
    private String currentOrder ; 
    private int serverCode ; //Used by server to determine it's the current user 
    
    //Socket and streams. This is like this because the way the streams are going 
    //to work is that it is all data streams used. Opening and closing the streams
    //and by extension the sockets will cause overall issues I feel 
    private static Socket s ; 
    private static DataOutputStream dataOut = null ; 
    private static DataInputStream dataIn = null ; 
    
    /**
     * Upon initialising the program, will attempt connecting to the server and if 
     * it fails, then throw an error and then exit the program
     */
    public CurrentSession() { 
        
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(String currentOrder) {
        this.currentOrder = currentOrder;
    }

    public int getServerCode() {
        return serverCode;
    }

    public void setServerCode(int serverCode) {
        this.serverCode = serverCode;
    }
    
}
