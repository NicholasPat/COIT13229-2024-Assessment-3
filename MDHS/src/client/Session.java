package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import common.model.Account;
/**
 * This class is used like a session handler, or a cookie. It will hold current
 * information sent from the server plus any other flags needed. 
 */
public class Session {
    /* Session Information */
    private Account user;
    private PublicKey publicKey;
    
    /* Connection */
    private static final int serverPort = 6811;
    private static final String hostName = "localhost" ; 
    
    private Socket socket ; 
    private ObjectOutputStream objOut; 
    private ObjectInputStream objIn; 
    
    
    
    private final static Session instance = new Session();
    public static Session getSession() {
        return instance;
    }
    
    /**
     * Upon initializing the program, will attempt connecting to the server and if 
     * it fails, then throw an error and then exit the program
     */
    public Session() { 
        initialiseConnection();
    }
    
    public void initialiseConnection() { 
        try {
            socket = new Socket(hostName, serverPort);
            objOut =new ObjectOutputStream( socket.getOutputStream() );
            objIn = new ObjectInputStream( socket.getInputStream() );

            if (publicKey == null) {
                objOut.writeObject("PublicKey");
                setPublicKey();
            }

        } catch (UnknownHostException e){ System.out.println("Sock:"+e.getMessage()); 
        } catch (EOFException e){  System.out.println("EOF: Did not recieve responce from server.");
        } catch (IOException e){ System.out.println("IO:"+e.getMessage());
        }
    }
    
    private void setPublicKey() { 
        try { 
            //dataOut.writeUTF("Public key please") ; 
            int pubKeyLength = objIn.readInt() ; 
            byte[] bytesPublicKey = new byte[pubKeyLength] ; 
            objIn.readFully(bytesPublicKey, 0, pubKeyLength) ; 
            
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPublicKey) ; 
            KeyFactory keyFactory = KeyFactory.getInstance("RSA") ; 
            
            publicKey = keyFactory.generatePublic(pubKeySpec) ; 
            
            System.out.println("Public Key from the server: " + publicKey) ; 
        } catch (IOException e) {System.out.println("readline:"+e.getMessage());
        } catch (NoSuchAlgorithmException ex) {ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {ex.printStackTrace();}
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }
}
