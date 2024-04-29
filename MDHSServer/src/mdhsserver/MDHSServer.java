package mdhsserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author linke
 */
public class MDHSServer {
    
    /**
     * 
     * @param args the command line arguments
     * @throws java.security.NoSuchAlgorithmException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        int threadCount = 1 ; 
        int serverPort = 6464 ; 
        PasswordDetails passwordDetails = new PasswordDetails() ; 
        PublicKey publicK = passwordDetails.getPublicKey() ; 
        PrivateKey privateK = passwordDetails.getPrivateKey() ; 
        
        //For debugging 
        System.out.println("Public Key: " + passwordDetails.getPublicKey()) ; 
        System.out.println("Private Key: " + passwordDetails.getPrivateKey()) ; 
        
        try { 
            ServerSocket listenSocket = new ServerSocket(serverPort) ; 
            
            while (true) { 
                Socket clientSocket = listenSocket.accept() ; 
                MainConnection c = new MainConnection(clientSocket, 
                        threadCount++, publicK, privateK, passwordDetails) ; 
                System.out.println("Thread started: " + threadCount) ; 
                c.start() ; 
            } 
        }catch(IOException e){System.out.println("Listen :"+e.getMessage());}
    }
    
}

/** 
 * 
 * @author linke
 */
class MainConnection extends Thread { 
    DataInputStream dataIn ; 
    DataOutputStream dataOut ; 
    ObjectInputStream objIn ; 
    ObjectOutputStream objOut ; 
    Socket clientSocket ; 
    int threadNumber ; 
    PublicKey publicKey ; 
    PrivateKey privateKey ; 
    PasswordDetails passwordDetails ; 
    
    /** 
     * 
     * @param aSocket
     * @param count 
     * 
     * @throws IOException 
     */
    public MainConnection(Socket aSocket, int count, PublicKey key, 
            PrivateKey privKey, PasswordDetails passwordDetails) { 
        threadNumber = count ; 
        clientSocket = aSocket ; 
        publicKey = key ; 
        privateKey = privKey ; 
        this.passwordDetails = passwordDetails ; 
        try { 
            dataIn = new DataInputStream(clientSocket.getInputStream()) ; 
            dataOut = new DataOutputStream(clientSocket.getOutputStream()) ; 
            
            //Obj streams break it. Gonna have to figure out how to make it choose which stream to make 
            //objIn = new ObjectInputStream(clientSocket.getInputStream()) ; 
            //objOut = new ObjectOutputStream(clientSocket.getOutputStream()) ; 
        }catch(IOException e){System.out.println("Connection: "+e.getMessage());}
    }
    
    /** 
     * 
     */
    @Override 
    public void run() { 
        try { 
            String data1 = dataIn.readUTF() ;  
            if (data1.equalsIgnoreCase("Password check")) { 
                //dataOut.writeUTF("All good") ; 
                passwordMethod() ;
            } 
        } catch (IOException ex){System.out.println("Listen: " + ex.getMessage());}
    }
    
    //NEEDS PROPER COMMENTING 
    private void passwordMethod() throws IOException { 
        //DEBUG 
        System.out.println("Public Key: " + passwordDetails.getPublicKey()) ; 
        System.out.println("Private Key: " + passwordDetails.getPrivateKey()) ; 
        
        String data ; 
        String message = null ; 
        String format = null ; 
        String encodedKey = null ; 
        String publicKeyString = null ; 
        String messageString = null ; 
        X509EncodedKeySpec pubKeySpec = null ; 
        String decryptedPassword = null ; 
        
        //Generate the encoded key to be used 
        byte[] bytesPubKey = publicKey.getEncoded() ; 
        System.out.println("PublicKey in bytes: " + bytesPubKey.length) ; 
        
        //Send the size of the key 
        dataOut.writeInt(bytesPubKey.length) ; 
        System.out.println("TRACE: Sent out pub key length") ; 
        
        //Send the key bytes 
        dataOut.write(bytesPubKey, 0, bytesPubKey.length) ; 
        System.out.println("TRACE: Sent the bytesPubeKey") ;
        
        //Send size of encrypted message to be sent from client 
        int messageLength = dataIn.readInt() ; 
        byte[] encodedMessage = new byte[messageLength] ; 
        System.out.println("TRACE: Message Length: " + messageLength + "\n" + 
                "Encoded Message Length: " + encodedMessage.length + "\n") ; 

        //Read the encrypted password sent from the client 
        dataIn.read(encodedMessage, 0, encodedMessage.length) ; 

        try { 
            decryptedPassword = passwordDetails.decrypt(encodedMessage) ; 
        } catch (NoSuchAlgorithmException e) {System.out.println("No Algorithm: " + e.getMessage()) ; 
        } catch (NoSuchPaddingException e) {System.out.println("No Padding: " + e.getMessage()) ; 
        } catch (InvalidKeyException e) {System.out.println("Invalid Key: " + e.getMessage()) ; 
        } catch (InvalidAlgorithmParameterException e) {System.out.println("Invalid Alg Para: " + e.getMessage()) ; 
        } catch (IllegalBlockSizeException e) {System.out.println("Illegal Block Size: " + e.getMessage()) ; 
        } catch (BadPaddingException e) {System.out.println("Bad Padding: " + e.getMessage()) ; 
        } 
        
        System.out.println("Encrypted byte[]: " + Arrays.toString(encodedMessage)) ; 
        System.out.println("Decrypted password: " + decryptedPassword) ; 
        
    }
}

//Trace command 
//System.out.println("TRACE: ") ; 

//In run() {}
//String data = null ; 
/*try {
data = dataIn.readUTF() ;
System.out.println(data) ;
}catch(IOException e){System.out.println("Connection: "+e.getMessage());}

if (data.equalsIgnoreCase("Password Check")) {
try {
passwordMethod() ;
dataOut.writeUTF("All good");
}catch(IOException e){System.out.println("Connection: "+e.getMessage());}
        }*/

/* Original while block for the data 
        while ((data=dataIn.readUTF())!=null){
            System.out.println("Message from the client: " + data) ; 
            if (data.equalsIgnoreCase("Public key?")) { 
                // Send the key size 
                dataOut.writeInt(bytesPubKey.length) ; 
                System.out.println("TRACE: Sent out pub key length") ; 
                
                //Send the key in bytes 
                dataOut.write(bytesPubKey, 0, bytesPubKey.length) ; 
                System.out.println("TRACE: Sent out pub key") ; 
            } 
            if (data.equalsIgnoreCase("Password incoming")) { 
                System.out.println("TRACE: Password incoming ") ; 
                break ; 
            }
        }
        System.out.println("TRACE: Outside while") ; 
*/