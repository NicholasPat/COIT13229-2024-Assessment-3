
package server;

import common.Authenticator;
import common.model.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author lucht
 */
public class MHDSServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            int serverPort = 6811; 
            DatabaseConnection database = new DatabaseConnection();
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                ConnectionThread c = new ConnectionThread(clientSocket, database);
            }
        } catch(IOException e){ System.out.println("Socket Error: "+e.getMessage());}
    }
    
}

class ConnectionThread extends Thread {
    ObjectInputStream objIn;
    ObjectOutputStream objOut;
    Socket clientSocket;
    DatabaseConnection database;
    Authenticator authenticator;
    
    /**
    * Constructor for Connection
    * @param aClientSocket socket client is connecting on
    */
    public ConnectionThread (Socket aClientSocket, DatabaseConnection db) {
        try {
            clientSocket = aClientSocket;
            database = db;
            objIn = new ObjectInputStream( clientSocket.getInputStream());
            objOut = new ObjectOutputStream( clientSocket.getOutputStream());
            authenticator = new Authenticator();
            this.start();
        } catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
    }
    
    /**
     * Receive and process data from client connection.
     */
    public void run(){
        try {
            while (true){
                String option = (String) objIn.readObject();
                
                if (option.equalsIgnoreCase("PublicKey")){ 
                    PublicKey publicK = authenticator.getPublicKey();
                    System.out.println(publicK.toString());
                    objOut.writeObject(publicK);
                    
                } else if (option.equalsIgnoreCase("Login")){ 
                    System.out.println("Login");
                    
                    Account user = (Account) objIn.readObject();
                    Account acc = database.getAccountByEmail(user.getEmailAddress());

                    if (acc != null) {
                        try {
                            String pwd = Authenticator.decrypt(authenticator.getPrivateKey(), user.getPassword());
                            byte[] passwordBytes = pwd.getBytes(StandardCharsets.UTF_8);

                            if (Arrays.equals(passwordBytes, acc.getPassword())) {
                                acc.setPassword(null); // clear password before transmitting
                                objOut.writeObject(acc);
                                System.out.println("Login successful: " + acc.getEmailAddress());
                            } else {
                                objOut.writeObject(null);
                                System.out.println("Login failed: Password mismatch");
                            }

                        } catch (NoSuchAlgorithmException ex) {System.out.println("Algorithm: " + ex.getMessage());
                        } catch (NoSuchPaddingException ex) {System.out.println("Padding: " + ex.getMessage());
                        } catch (InvalidKeyException ex) {System.out.println("Invalid key: " + ex.getMessage());
                        } catch (InvalidAlgorithmParameterException ex) {System.out.println("Invalid parameter: " + ex.getMessage());
                        } catch (IllegalBlockSizeException ex) {System.out.println("Block size: " + ex.getMessage());
                        } catch (BadPaddingException ex) {System.out.println("Bad padding: " + ex.getMessage());
                        }
                    } else {
                        System.out.println("Login failed: Account not found.");
                        objOut.writeObject(null);
                    }
                    
                } else if (option.equalsIgnoreCase("Register")){ 
                    System.out.println("Register");
                    
                    Account acc = (Account) objIn.readObject();
                    
                    try {
                        String pwd = Authenticator.decrypt(authenticator.getPrivateKey(), acc.getPassword());
                        byte[] passwordBytes = pwd.getBytes(StandardCharsets.UTF_8);
                        acc.setPassword(passwordBytes);
                        
                        // insert account
                        Boolean accAdded = database.addAccount(acc);
                        if (accAdded) {
                            acc.setPassword(null); // clear password before transmitting
                            objOut.writeObject(acc);
                            System.out.println("Registration successful: " + acc.getEmailAddress());
                        } else {
                            objOut.writeObject(null);
                            System.out.println("Registration failed.");
                        }
                    } catch (NoSuchAlgorithmException ex) {System.out.println("Algorithm: " + ex.getMessage());
                    } catch (NoSuchPaddingException ex) {System.out.println("Padding: " + ex.getMessage());
                    } catch (InvalidKeyException ex) {System.out.println("Invalid key: " + ex.getMessage());
                    } catch (InvalidAlgorithmParameterException ex) {System.out.println("Invalid parameter: " + ex.getMessage());
                    } catch (IllegalBlockSizeException ex) {System.out.println("Block size: " + ex.getMessage());
                    } catch (BadPaddingException ex) {System.out.println("Bad padding: " + ex.getMessage());
                    }
                    
                } else if (option.equalsIgnoreCase("AllOrders")){ 
                    
                } else if (option.equalsIgnoreCase("AllCustomers")){ 
                    
                } else if (option.equalsIgnoreCase("AllProducts")){ 
                    
                } else if (option.equalsIgnoreCase("DeliverySchedule")){ 
                    
                } else if (option.equalsIgnoreCase("Placeorder")){ 
                    
                } else if (option.equalsIgnoreCase("NewProduct")){ 
                    
                } else if (option.equalsIgnoreCase("EditProduct")){ 
                    
                } else if (option.equalsIgnoreCase("RemoveProduct")){ 
                    
                } else if (option.equalsIgnoreCase("NewSchedule")){ 
                    
                } else if (option.equalsIgnoreCase("EditSchedule")){ 
                    
                } else if (option.equalsIgnoreCase("RemoveSchedule")){ 
                    
                } 
            }
        } catch (IOException e){ System.out.println(/*close failed*/);
        } catch(ClassNotFoundException ex){System.out.println("CNF Error:"+ex.getMessage());
        } finally{ try {objIn.close();clientSocket.close();}catch (IOException e){/*close failed*/}}
    }
}