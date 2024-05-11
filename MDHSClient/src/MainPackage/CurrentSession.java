package MainPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * This class is used like a session handler, or a cookie. It will hold current
 * information sent from the server plus any other flags needed. Will share 
 * commonalities with the Customer class on the server side. Will be initialized 
 * by the MdhsClient.java class (application) and then passed to the controllers 
 * as they are initialized. This handles the issue of needing to pass session 
 * information between classes 
 * 
 * @author linke
 */
public class CurrentSession {
    /*Customer information*/
    private static int customerId ; 
    private static String firstName ; 
    private static String lastName ; 
    private static String username ; 
    private static int phoneNumber ; 
    private static String emailAddress ; 
    private static String password ; 
    private static String deliveryAddress ; 
    private static int administrator ; 
    
    /*Flags and necessary information*/
    private static String currentOrder ; 
    private static int serverCode ; //Used by server to determine it's the current user 
    
    //Socket and streams. This is like this because the way the streams are going 
    //to work is that it is all data streams used. Opening and closing the streams
    //and by extension the sockets will cause overall issues I feel 
    private static Socket s ; 
    private static DataOutputStream dataOut = null ; 
    private static DataInputStream dataIn = null ; 
    
    private static PublicKey publicKey = null ; 
    
    private static final String hostName = "localhost" ; 
    private static final int serverPort = 6464 ; 
    
    /**
     * Upon initializing the program, will attempt connecting to the server and if 
     * it fails, then throw an error and then exit the program
     */
    public CurrentSession() { 
        
    }
    
    public static void initialiseStream() { 
        try {
            s = new Socket(hostName, serverPort) ;
            dataOut = new DataOutputStream(s.getOutputStream()) ; 
            dataIn = new DataInputStream(s.getInputStream()) ;
            
            //if (publicKey == null) { 
               // dataOut.writeUTF("Public key please") ; 
             //   setPublicKey() ; 
          //  } 
        } catch (IOException e) {System.out.println("readline:"+e.getMessage());}
    }
    
    public static void setPublicKey() { 
        try { 
            //dataOut.writeUTF("Public key please") ; 
            int pubKeyLength = dataIn.readInt() ; 
            byte[] bytesPublicKey = new byte[pubKeyLength] ; 
            dataIn.readFully(bytesPublicKey, 0, pubKeyLength) ; 
            
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPublicKey) ; 
            KeyFactory keyFactory = KeyFactory.getInstance("RSA") ; 
            
            publicKey = keyFactory.generatePublic(pubKeySpec) ; 
            
            System.out.println("Public Key from the server: " + publicKey) ; 
        } catch (IOException e) {System.out.println("readline:"+e.getMessage());
        } catch (NoSuchAlgorithmException ex) {ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {ex.printStackTrace();}
    }

    public static int getCustomerId() {
        return customerId;
    }

    public static void setCustomerId(int customerIdA) {
        customerId = customerIdA;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstNameA) {
        firstName = firstNameA;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastNameA) {
        lastName = lastNameA;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String usernameA) {
        username = usernameA;
    }

    public static int getPhoneNumber() {
        return phoneNumber;
    }

    public static void setPhoneNumber(int phoneNumberA) {
        phoneNumber = phoneNumberA;
    }

    public static String getEmailAddress() {
        return emailAddress;
    }

    public static void setEmailAddress(String emailAddressA) {
        emailAddress = emailAddressA;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String passwordA) {
        password = passwordA;
    }

    public static String getDeliveryAddress() {
        return deliveryAddress;
    }

    public static void setDeliveryAddress(String deliveryAddressA) {
        deliveryAddress = deliveryAddressA;
    }

    public static String getCurrentOrder() {
        return currentOrder;
    }

    public static int getAdministrator() {
        return administrator;
    }

    public static void setAdministrator(int administrator) {
        CurrentSession.administrator = administrator;
    } 

    public static void setCurrentOrder(String currentOrderA) {
        currentOrder = currentOrderA;
    }

    public static int getServerCode() {
        return serverCode;
    }

    public static void setServerCode(int serverCodeA) {
        serverCode = serverCodeA;
    }

    public static Socket getS() {
        return s;
    }

    public static DataOutputStream getDataOut() {
        return dataOut;
    }

    public static DataInputStream getDataIn() {
        return dataIn;
    }
    
    
}
