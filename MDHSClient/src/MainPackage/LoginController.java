
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javax.crypto.Cipher;

/**
 * FXML Controller class
 *
 * @author linke
 */
public class LoginController implements Initializable {

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;
    
    /*Other declarations - not related to FXML*/
    private Socket s = null ; 
    private DataOutputStream dataOut = null ; 
    private DataInputStream dataIn = null ; 
    private final String hostName = "localhost" ; 
    private final int serverPort = 6464 ; 
    private static PublicKey publicKey ; 
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            s = new Socket(hostName, serverPort) ;
            dataOut = new DataOutputStream(s.getOutputStream()) ; 
            dataIn = new DataInputStream(s.getInputStream()) ;
        } catch (IOException e) {System.out.println("readline:"+e.getMessage());}
    }    

    @FXML
    private void clickToLogin(ActionEvent event) {
        encryptAndSendPassword() ; 
    }
    
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
            //out.writeUTF("PasswordCheck") ; 
            System.out.println("TRACE: Setup socket and streas ") ; 
            
            //message = in.readUTF() ; 
            //if (message.equalsIgnoreCase("all good")) { 
                //out.writeUTF(userName + "::" + password) ; 
                //dataOut.writeUTF("Public key?"); 
                //System.out.println("TRACE: Sent query public key?") ; 
                
                pubKeyLength = dataIn.readInt() ; 
                System.out.println("TRACE: Took in pubKeyLength " + pubKeyLength) ; 
                bytesPublicKey = new byte[pubKeyLength] ; 
                
                dataIn.readFully(bytesPublicKey, 0, pubKeyLength) ; 
                System.out.println("TRACE: Read in the public key ") ; 
            //}
            
            //Generate key specification for encoding -- SUMMARISE LATER 
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPublicKey) ; 
            KeyFactory keyFactory = KeyFactory.getInstance("RSA") ; 
            System.out.println("TRACE: X509 AND KeyFactory done") ; 
            
            //Extract the public key - EXPLAIN LATER 
            publicKey = keyFactory.generatePublic(pubKeySpec) ; 
            System.out.println("TRACE: public key extract") ; 
            
            //Encrypt the password 
            byte[] encodedMessage = encrypt(password) ; 
            dataOut.writeUTF("Password incoming") ; 
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
        }finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
    }

    private byte[] encrypt(String message) throws Exception { 
        Cipher cipher = Cipher.getInstance("RSA") ; 
        cipher.init(Cipher.ENCRYPT_MODE, publicKey) ; 
        
        byte[] cipherData = cipher.doFinal(message.getBytes("UTF-8")) ; 
        return cipherData ; 
    }
    
    @FXML
    private void clickToRegister(ActionEvent event) {
        try {
           MdhsClient.setRoot("Registration") ;
        } catch (IOException e){
           System.out.println(e) ; 
        }
    }
    
}
