/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mdhsserver;

import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * This class is used to perform all the checks for the password for the server
 * Split into a different class to attempt and obfuscate 
 * It will also generate the public and private key for the session 
 * 
 * @author linke
 */
public class PasswordDetails implements Serializable {
    /*Private variables for the class*/ 
    private PrivateKey privateK = null ; 
    private PublicKey publicK = null ; 
    private KeyPairGenerator keyPairGen = null ; 
    
    /**
     * Creates new Public and Private key for use 
     */
    public PasswordDetails() throws NoSuchAlgorithmException {
        keyPairGen =  KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGen.genKeyPair();
        privateK = keyPair.getPrivate();
        publicK = keyPair.getPublic(); 
    } 
    
    /** 
     * 
     * @return 
     */
    public PrivateKey getPrivateKey() {
        return privateK;
    }
    
    /** 
     * 
     * @return 
     */
    public PublicKey getPublicKey() {
        return publicK;
    }
    
    /** 
     * 
     * @param encodedMessage
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException 
     */
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

//System.out.println("KSH TRACE: ") ; 