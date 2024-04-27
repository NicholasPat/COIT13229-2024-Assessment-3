/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mdhsserver;

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
 * 
 * @author linke
 */
public class PasswordDetails {
    /**/
    private KeyPairGenerator keyPairGen = null ;
    private PrivateKey privateKey = null ;
    private PublicKey publicKey = null ;
    
    /**
     * 
     */
    public PasswordDetails() throws NoSuchAlgorithmException {
        keyPairGen =  KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGen.genKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic(); 
    } 
    
    public KeyPairGenerator getKeyPairGen() {
        return keyPairGen;
    }
    
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
    
    public PublicKey getPublicKey() {
        return publicKey;
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
        cipher.init(Cipher.DECRYPT_MODE, privateKey, cipher.getParameters()) ; 
        return new String(cipher.doFinal(encodedMessage)) ; 
    }
    
}
