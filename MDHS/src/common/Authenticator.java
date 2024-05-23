
package common;

import java.nio.charset.StandardCharsets;
import java.security.*;
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
    
    public static String decrypt(PrivateKey prK, byte[] encodedMessage) throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, 
            IllegalBlockSizeException, BadPaddingException 
    { 
        Cipher cipher = Cipher.getInstance("RSA") ; 
        cipher.init(Cipher.DECRYPT_MODE, prK, cipher.getParameters()) ; 
        return new String(cipher.doFinal(encodedMessage)) ; 
    }
    
    public static byte[] encrypt(PublicKey puK, String message) throws Exception { 
        Cipher cipher = Cipher.getInstance("RSA") ; 
        cipher.init(Cipher.ENCRYPT_MODE, puK) ; 
        
        byte[] cipherData = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8)) ; 
        return cipherData ; 
    }
}
