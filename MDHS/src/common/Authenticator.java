
package common;

import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;

/**
 *
 * @author lucht
 */
public class Authenticator {
    private PrivateKey privateK = null ; 
    private PublicKey publicK = null ; 
    private KeyPairGenerator keyPairGen = null ; 
    
    public Authenticator() {
        try {
            keyPairGen =  KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyPairGen.genKeyPair();
            privateK = keyPair.getPrivate();
            publicK = keyPair.getPublic(); 
        } catch (NoSuchAlgorithmException ex) {System.out.println("Error: "+ex.getMessage());System.exit( 1 );}
    }
    
    public PrivateKey getPrivateKey() {
        return privateK;
    }
    
    public PublicKey getPublicKey() {
        return publicK;
    }
    
    public String decrypt(byte[] encodedMessage) throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, 
            IllegalBlockSizeException, BadPaddingException 
    { 
        Cipher cipher = Cipher.getInstance("RSA") ; 
        cipher.init(Cipher.DECRYPT_MODE, privateK, cipher.getParameters()) ; 
        return new String(cipher.doFinal(encodedMessage)) ; 
    }
    
    public byte[] encrypt(String message) throws Exception { 
        Cipher cipher = Cipher.getInstance("RSA") ; 
        cipher.init(Cipher.ENCRYPT_MODE, privateK) ; 
        
        byte[] cipherData = cipher.doFinal(message.getBytes("UTF-8")) ; 
        return cipherData ; 
    }
}
