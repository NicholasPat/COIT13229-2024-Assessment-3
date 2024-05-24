
package server;

import common.Authenticator;
import common.model.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
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
            loadProductFile(database);
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                ConnectionThread c = new ConnectionThread(clientSocket, database);
            }
        } catch(IOException e){ System.out.println("Socket Error: "+e.getMessage());}
    }
    
    
    /** 
     * Loads products from a CSV file and inserts into database.
     * Invoked upon startup.
     * 
     */
    private static void loadProductFile(DatabaseConnection database) { 
        String fileName = "A3-products.csv" ; 
        File file = new File(fileName) ; 
        
        ArrayList<Product> productList = new ArrayList<>() ; 
        String[] splitEntries ; 
        
        try (Scanner fileInput = new Scanner(file)) {
            while (fileInput.hasNextLine()) {
                String line = fileInput.nextLine();

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                Product product = parseProductLine(line);
                if (product != null) {
                    productList.add(product);
                }
            }
            
            database.addProductsFromFile(productList);
        } catch (FileNotFoundException e) { 
            System.out.println("File with name: " + fileName + " does not exist, creating file") ; 
        } 
    }
    
    /**
     * Parses a line from the CSV file into a Product object.
     *
     * @param line the CSV line to parse
     * @return a Product object, or null if the line is invalid
     */
    private static Product parseProductLine(String line) {
        String[] splitEntries = line.split(",");

        // Ensure the line has the correct number of entries
        if (splitEntries.length != 5) {
            System.out.println("Invalid line format: " + line);
            return null;
        }

        try {
            String name = splitEntries[0].trim();
            String unit = splitEntries[1].trim();
            int quantity = Integer.parseInt(splitEntries[2].trim());
            double price = Double.parseDouble(splitEntries[3].trim());
            String ingredients = splitEntries[4].trim();

            return new Product(name, quantity, unit, price, ingredients);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing line: " + line + " - " + e.getMessage());
            return null;
        }
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
                    System.out.println("AllOrders");
                    
                } else if (option.equalsIgnoreCase("AllCustomers")){ 
                    System.out.println("AllCustomers");
                    
                } else if (option.equalsIgnoreCase("AllProducts")){ 
                    System.out.println("AllProducts");
                    
                } else if (option.equalsIgnoreCase("DeliverySchedule")){ 
                    System.out.println("DeliverySchedule");
                    ArrayList<DeliverySchedule> deliverySchedules = database.loadDeliverySchedules();
                    objOut.writeObject(deliverySchedules);

                    
                } else if (option.equalsIgnoreCase("Placeorder")){ 
                    System.out.println("Placeorder");
                    
                } else if (option.equalsIgnoreCase("NewProduct")){ 
                    System.out.println("NewProduct");
                    
                } else if (option.equalsIgnoreCase("EditProduct")){ 
                    System.out.println("EditProduct");
                    
                } else if (option.equalsIgnoreCase("RemoveProduct")){ 
                    System.out.println("RemoveProduct");
                    
                } else if (option.equalsIgnoreCase("NewSchedule")){ 
                    System.out.println("NewSchedule");
                    
                } else if (option.equalsIgnoreCase("EditSchedule")){ 
                    System.out.println("EditSchedule");
                    
                } else if (option.equalsIgnoreCase("RemoveSchedule")){ 
                    System.out.println("RemoveSchedule");
                    
                } 
            }
        } catch (IOException e){ System.out.println(/*close failed*/);
        } catch(ClassNotFoundException ex){System.out.println("CNF Error:"+ex.getMessage());
        } finally{ try {objIn.close();clientSocket.close();}catch (IOException e){/*close failed*/}}
    }
    
}