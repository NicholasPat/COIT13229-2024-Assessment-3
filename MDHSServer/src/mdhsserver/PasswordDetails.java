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
    /*Public variables for the class*/ 
    private PrivateKey privateKey = null ;
    private PublicKey publicKey = null ;
    private PrivateKey privateK = null ; 
    private PublicKey publicK = null ; 
    private KeyPairGenerator keyPairGen = null ; 
    
    /**
     * Creates new Public and Private key for use 
     */
    public PasswordDetails() throws NoSuchAlgorithmException {
        keyPairGen =  KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGen.genKeyPair();
        this.privateK = keyPair.getPrivate();
        this.publicK = keyPair.getPublic(); 
        //keyPairGen =  KeyPairGenerator.getInstance("RSA");
        //KeyPair keyPair = keyPairGen.genKeyPair();
        //this.privateKey = keyPair.getPrivate();
        //this.publicKey = keyPair.getPublic(); 
    } 
    
    /** 
     * This is used to set for when the keys are already known. So saved to file 
     * then on startup would be invoked from reading a file. To set the key used 
     * to encrypt a password 
     * 
     * @param privKey
     * @param pubKey 
     */
    public PasswordDetails(PrivateKey privKey, PublicKey pubKey) { 
        privateKey = privKey ; 
        publicKey = pubKey ; 
    }
    
    /** 
     * 
     * @return 
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
    
    /** 
     * 
     * @return 
     */
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

//System.out.println("KSH TRACE: ") ; 