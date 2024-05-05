/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package MainPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javax.crypto.Cipher;

/**
 * FXML Controller class
 *
 * @author linke
 */
public class RegistrationController implements Initializable {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailAddressField;
    @FXML
    private TextField deliveryAddressField;
    @FXML
    private PasswordField passwordField;
    
    private static PublicKey publicKey ; 
    private final String hostName = "localhost" ; 
    private final int serverPort = 6464 ; //To connect to server MDHS 
    private Socket s = null ; 
    private int publicKeyLength ; 
    private DataOutputStream dataOut ; 
    private DataInputStream dataIn ; 
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        s = CurrentSession.getS() ; 
        dataOut = CurrentSession.getDataOut() ; 
        dataIn = CurrentSession.getDataIn() ; 
    }    

    @FXML
    private void onClickBackToLogin(ActionEvent event) {
        try {
            /*Close the socket then switch to the next scene*/
            //s.close() ; 
            MdhsClient.setRoot("Login") ;
        } catch (IOException e){
            System.out.println(e) ; 
        }
        
    }

    @FXML
    private void onClickRegister(ActionEvent event) {
        /*Error handle if the fields are empty, if they are display error and return*/
        String message = validation() ; 
        String checkMessage = null ; 
        
        if (!message.equals("")) { 
            Alert alert = new Alert(Alert.AlertType.ERROR, message) ; 
            alert.showAndWait() ; 
            return ; 
        } 
        
        encryptAndSendPassword() ; 
        
        //For now will assume username is unique, but won't error handle yet 
        
        /*Note - password field will be handled differently!*/
        String dataToSend = firstNameField.getText() + "::" + lastNameField.getText() 
                + "::" + usernameField.getText() + "::" + phoneNumberField.getText() 
                + "::" + emailAddressField.getText() + "::" + deliveryAddressField.getText() ; 
        try { 
            dataOut.writeUTF(dataToSend) ; 
            checkMessage = dataIn.readUTF() ; 
            
            if (checkMessage.equals("All good")) { 
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                    "Cusomter added, please return to login and login using "
                            + "credentials ") ; 
                alert.showAndWait() ; 
            } else { 
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error "
                        + "made when executing new customer query") ; 
                alert.showAndWait() ; 
            }
        } catch (IOException e) {System.out.println("IOException: " + e.getMessage());}
    }
    
    /** 
     * This method is used to error handle if the text fields are empty. If they 
     * are it will send a not null message which will be used in creating an alert 
     * then end the message 
     * @return message 
     */
    private String validation() { 
        String message = "" ; 
        
        if(firstNameField.getText().equals("")) { 
            message+="First name is a mandatory field\n" ;} 
        if (lastNameField.getText().equals("")) { 
            message+="Last name is a mandatory field\n" ;}
        if (usernameField.getText().equals("")) { 
            message+="Username is a mandatory field\n" ;}
        if (phoneNumberField.getText().equals("")) { 
            message+="Phone number is a mandatory field\n" ;}
        if (emailAddressField.getText().equals("")) { 
            message+="emailAddress is a mandatory field\n" ;}
        if (deliveryAddressField.getText().equals("")) { 
            message+="Delivery address is a mandatory field\n" ;}
        if (passwordField.getText().equals("")) { 
            message+="Password is a mandatory field\n" ;} 
        
        //Error handle if fields aren't good in length or int - Later, error handle 
        //for the username 
        return message ; 
    }
    
    private byte[] encrypt(String message) throws Exception { 
        Cipher cipher = Cipher.getInstance("RSA") ; 
        cipher.init(Cipher.ENCRYPT_MODE, publicKey) ; 
        
        byte[] cipherData = cipher.doFinal(message.getBytes("UTF-8")) ; 
        return cipherData ; 
    }
    
    /** 
     * Need a method to set the public key on startup of the method or just do it 
     * here are needed 
     * SAME AS LOGINCONTROLLER. GOOD IDEA TO MAKE INTO STATIC CLASS PERHAPS 
     *  
     * @return 
     */
    private void encryptAndSendPassword() { 
        String userName = usernameField.getText() ; 
        String password = passwordField.getText() ; //NEEDS SECURITY - but for now won't bother 
        
        byte[] bytesPublicKey = null ; 
        StringBuilder str = new StringBuilder() ; 
        StringBuilder messageAA = new StringBuilder() ; 
        String message ; 
        int pubKeyLength ; 
        System.out.println("TRACE: After declarations") ; 
        
        try { 
            dataOut.writeUTF("Password Check Registration") ; 
            System.out.println("TRACE: Setup socket and streams ") ; 
            
            dataOut.writeUTF(userName) ; 
            
            //message = dataIn.readUTF() ; 
            //if (message.equalsIgnoreCase("all good")) { 
                pubKeyLength = dataIn.readInt() ; 
                System.out.println("TRACE: Took in pubKeyLength " + pubKeyLength) ; 
                bytesPublicKey = new byte[pubKeyLength] ; 
                
                dataIn.readFully(bytesPublicKey, 0, pubKeyLength) ; 
                System.out.println("TRACE: Read in the public key ") ; 
            //}
            
            //Generate key specification for encoding -- SUMMARISE LATER 
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPublicKey) ; 
            KeyFactory keyFactory = KeyFactory.getInstance("RSA") ; 
            System.out.println("TRACE: X509 AND KeyFactory done\n"
                    + "PubKeySpec: " + pubKeySpec + "\n"
                            + "KeyFactory: " + keyFactory + "\n") ; 
            
            //Extract the public key - EXPLAIN LATER 
            publicKey = keyFactory.generatePublic(pubKeySpec) ; 
            System.out.println("TRACE: public key extract") ; 
            System.out.println("Public Key: " + publicKey + "\n") ; 
            
            //Encrypt the password 
            byte[] encodedMessage = encrypt(password) ; 
            //dataOut.writeUTF("Password incoming") ; 
            System.out.println("TRACE: Sent password incoming") ; 
            
            //Send length of the wncrypted pass word length 
            dataOut.writeInt(encodedMessage.length) ; 
            System.out.println("TRACE: Out pass length") ; 
            
            //Send the encrypted password in bytes 
            dataOut.write(encodedMessage, 0, encodedMessage.length) ; 
            
            System.out.println("TRACE DEBUG: Sent password encrypted: " + Arrays.toString(encodedMessage)) ; 
            
        }catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
        }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
        }catch (IOException e){System.out.println("readline:"+e.getMessage());
        }catch (NoSuchAlgorithmException e) {System.out.println("No Such Algoritm: " + e.getMessage());
        }catch (InvalidKeySpecException e) {System.out.println("Invalid Key Spc: " + e.getMessage());
        }catch (Exception e) {System.out.println("Exception: " + e.getMessage());
        }//finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
    }
}
/** 
 * try {
            System.out.println(CurrentSession.getUsername()) ; 
            /*DEBUG ERROR: Socket won't persist through FXML loading. Have to 
            consider if socket can be passed from scene to scene perhaps */
            
            //s = new Socket(hostName, serverPort) ;
            //dataOut = new DataOutputStream(s.getOutputStream()) ; 
            //dataIn = new DataInputStream(s.getInputStream()) ; 
       // } catch (IOException e) {System.out.println("readline:"+e.getMessage());}
 //*/