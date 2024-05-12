package mdhsserver;

import dataclasses.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


/**
 *
 * @author linke
 */
public class MDHSServer {
    private static PublicKey publicK ; 
    private static PrivateKey privateK ; 
    private static PasswordDetails passwordDetails ; 
    
    /**
     * 
     * @param args the command line arguments
     * @throws java.security.NoSuchAlgorithmException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        int threadCount = 0 ; 
        int serverPort = 6464 ; 
        
        /* 
        Check if passworddetailsObject exists 
        If not, then create a new object and serialise it to file immediately 
        If it does, then deserialise from file and assign to passwordDetails object
        
        Fucking giving up. I don't think I will bother saving Private Key to 
        Key Store. Because it ain't working, so will be a new key each time
        */        
        passwordDetails = new PasswordDetails() ; 
        privateK = passwordDetails.getPrivateKey() ; 
        publicK = passwordDetails.getPublicKey() ; 
        
        //For debugging purposes
        System.out.println("Public Key: \n" + passwordDetails.getPublicKey()) ; 
        System.out.println("Private Key: \n" + passwordDetails.getPrivateKey()) ; 
        
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
    private DataInputStream dataIn ; 
    private DataOutputStream dataOut ; 
    private ObjectInputStream objIn ; 
    private ObjectOutputStream objOut ; 
    private Socket clientSocket ; 
    private int threadNumber ; 
    private PublicKey publicKey ; 
    private PrivateKey privateKey ; 
    private PasswordDetails passwordClass ; 
    private DatabaseConnection databaseConnection ; 
    
    /** 
     * Initialises the current thread with the following parameters. Not sure 
     * about having the private key passed here, it probably should be obfuscated 
     * into another class 
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
        passwordClass = passwordDetails ; 
        databaseConnection = new DatabaseConnection() ; 
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
        boolean state = true ; 
        while (state) { 
            try { 
                String data1 = dataIn.readUTF() ;  
                if (data1.equalsIgnoreCase("Password check")) { 
                    passwordMethod(1) ;
                } 
                if (data1.equalsIgnoreCase("Public key please")) { 
                    sendPublicKey() ; 
                } 
                if (data1.equalsIgnoreCase("Password Check Registration")) { 
                    passwordMethod(2) ; 
                }
            } catch (IOException ex){
                System.out.println("Listen: " + ex.getMessage() 
                        + ". For thread number: " + threadNumber);
                state = false ; 
            }
        } 
    }
    
    /** 
     * REMEMBER TO COMMENT AND REMOVE THE TRACE COMMANDS 
     * @throws IOException 
     * @param i 
     */
    private void passwordMethod(int i) throws IOException { 
        String data ; 
        String message = null ; 
        String format = null ; 
        String encodedKey = null ; 
        String publicKeyString = null ; 
        String messageString = null ; 
        X509EncodedKeySpec pubKeySpec = null ; 
        String decryptedPassword = null ; 
        byte[] privEncryptPass = null ; 
        
        String username = dataIn.readUTF() ; 
        
        //If needed, rest of method can go back here 
        
        //Send size of encrypted message to be sent from client 
        int messageLength = dataIn.readInt() ; 
        byte[] encodedMessage = new byte[messageLength] ; 
        System.out.println("TRACE: Message Length: " + messageLength + "\n" + 
                "Encoded Message Length: " + encodedMessage.length + "\n") ; 

        //Read the encrypted password sent from the client 
        dataIn.read(encodedMessage, 0, encodedMessage.length) ; 

        try { 
            decryptedPassword = passwordClass.decrypt(encodedMessage) ; 
            
            privEncryptPass = passwordClass.encrypt(decryptedPassword) ; 
            String encryptedPass = Arrays.toString(privEncryptPass) ; 
            
            if (i==1) { 
                checkUser(username, encryptedPass) ;
            } 
            if (i==2) { 
                registerCustomer(encryptedPass) ; 
            }
        } catch (NoSuchAlgorithmException e) {System.out.println("No Algorithm: " + e.getMessage()) ; 
        } catch (NoSuchPaddingException e) {System.out.println("No Padding: " + e.getMessage()) ; 
        } catch (InvalidKeyException e) {System.out.println("Invalid Key: " + e.getMessage()) ; 
        } catch (InvalidAlgorithmParameterException e) {System.out.println("Invalid Alg Para: " + e.getMessage()) ; 
        } catch (IllegalBlockSizeException e) {System.out.println("Illegal Block Size: " + e.getMessage()) ; 
        } catch (BadPaddingException e) {System.out.println("Bad Padding: " + e.getMessage()) ; 
        } catch (Exception e) {System.out.println("Exception: " + e.getMessage()); }
        
        System.out.println("Encrypted byte[]: " + Arrays.toString(encodedMessage)) ; 
        System.out.println("Username: " + username + "\nDecrypted password: " + decryptedPassword) ; 
        
    }
    
    /** 
     * 
     * @throws IOException 
     */
    private void sendPublicKey() throws IOException { 
        //Generate the encoded key to be used 
        byte[] bytesPubKey = publicKey.getEncoded() ; 
        System.out.println("PublicKey in bytes: " + bytesPubKey.length) ; 
        
        //Send the size of the key 
        dataOut.writeInt(bytesPubKey.length) ; 
        System.out.println("TRACE: Sent out pub key length") ; 
        
        //Send the key bytes 
        dataOut.write(bytesPubKey, 0, bytesPubKey.length) ; 
        System.out.println("TRACE: Sent the bytesPubeKey") ;
    }
    
    /** 
     * 
     * @param username
     * @param password
     * @throws IOException 
     */
    private void checkUser(String username, String password) throws IOException { 
        Customer customer = databaseConnection.getCustomer(username) ; 
        if (password.equals(customer.getPassword())) { 
            String message = customer.getFirstName() + "::" + customer.getLastName() 
                    + "::" + customer.getUserName() + "::" + customer.getPhoneNumber() 
                    + "::" + customer.getEmailAddress() + "::" 
                    + customer.getDeliveryAddress() ; 
            
            dataOut.writeUTF(message) ;
        } else { 
            dataOut.writeUTF("Invalid") ; 
        }
    }
    
    /** 
     * 
     * @param password
     * @throws IOException 
     */
    private void registerCustomer(String password) throws IOException { 
        String dataToSend = dataIn.readUTF() ; 
        String status = databaseConnection.addCustomer(dataToSend, password) ; 
        if (status.equals("No issues")) { 
            //dataout all good - remember to add corresponding to client 
            dataOut.writeUTF("All good") ; 
        } else { 
            //dataout error - client will display an error message 
            dataOut.writeUTF("Invalid") ; 
        }
    }
    
    /** 
     * Since CSV (Comma Separated Value), just need to split with "," 
     * Invoked upon startup. Also place check for if the products are in there already
     * 
     */
    private void readProductFile() { 
        String fileName = "A3-products.csv" ; 
        File file = new File(fileName) ; 
        ArrayList<Product> productList = new ArrayList<>() ; 
        String entries ; 
        String[] splitEntries ; 
        
        try { 
            Scanner fileInput = new Scanner(file) ; 
            
            //If nothing read in *if* the file was correctly found, then just 
            //return an empty ArrayList 
            if (!fileInput.hasNext()) { 
                return ; 
            }
            
            //product name, unit, quantity, price, ingredients 
            //name, unit, (int) quant, (double) price, ingredients
            
            while (fileInput.hasNextLine()) { 
                entries = fileInput.nextLine() ; 
                splitEntries = entries.split(",") ; 
                int quantity = Integer.parseInt(splitEntries[2]) ; 
                double price = Double.parseDouble(splitEntries[3]) ; 
                
                try { 
                    productList.add(new Product( 
                        splitEntries[0], splitEntries[1], quantity,
                        price, splitEntries[4]
                    )) ; 
                } catch (Exception e) { 
                    //This should just happen on the first line since it's the 
                    //definitions of the variables, should skip 
                } 
            }
        } catch (FileNotFoundException e) { 
            System.out.println("File with name: " + fileName + " does not exist, creating file") ; 
            //Do nothing because it does not matter. Will just create it as needed 
        } 
        
        
        //Assumption: Won't check if products exist... if have time will add error 
        //handling 
        
        //Take the ArrayList and send to the method for Writing Products 
        
        
    }
    
}