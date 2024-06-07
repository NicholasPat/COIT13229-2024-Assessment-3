
package server;

import common.Authenticator;
import common.model.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Main Server class which will listen to server port for a connection. Most of 
 * the logic is in the Connection class. 
 * 
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
 */
public class MHDSServer {

    /**
     * Main method to initialise the server. Server listens onto port 6811 and 
     * will establish a connection when a client requests to connect. 
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int threadCount = 0; 
        int serverPort = 6811; 
        
        try{
            DatabaseConnection database = new DatabaseConnection();
            loadProductFile(database);
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                ConnectionThread c = new ConnectionThread(clientSocket, database, threadCount++);
                c.start(); 
            }
        } catch(IOException e){ System.out.println("Socket Error: "+e.getMessage());}
    }
    
    
    /** 
     * Loads products from a CSV file and inserts into database.
     * Invoked upon startup.
     * 
     * @param database  Database object used to insert Products into the DB 
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
     * @param line  the CSV line to parse
     * @return a    Product object, or null if the line is invalid
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

/** 
 * ConnectionThread class, extends Thread. <p> 
 * This class is responsible for all the connectivity between client and server. 
 * This is following the thread connection server model. Uses Object Streams 
 * since it is easier to work with. I.e., can send Integers and Strings as Objects 
 * as well. Data would be severely limiting in needing to prepare Objects as Strings 
 * then send and then piece together. 
 * 
 * @author Nicholas Paterno 
 * @author Brodie Lucht 
 * @author Christopher Cox 
 * @see Thread 
 */
class ConnectionThread extends Thread {
    ObjectInputStream objIn; 
    ObjectOutputStream objOut; 
    Socket clientSocket; 
    DatabaseConnection database; 
    Authenticator authenticator; 
    int threadCount; 
    String thdString; 
    
    /**
    * Constructor for Connection. 
    * 
    * @param aClientSocket  Socket client is connecting on
    * @param db             Database object for client to use 
    * @param threadCount    Thread count as determined via the Server 
    */
    public ConnectionThread (Socket aClientSocket, DatabaseConnection db, int threadCount) {
        try {
            clientSocket = aClientSocket;
            database = db;
            objIn = new ObjectInputStream( clientSocket.getInputStream());
            objOut = new ObjectOutputStream( clientSocket.getOutputStream());
            authenticator = new Authenticator();
            this.threadCount = threadCount;
            thdString = "Thread: " + threadCount + " ";
        } catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
    }
    
    /**
     * Receive and process data from client connection. while loops until the 
     * client disconnects. 
     */
    @Override
    public void run(){
        try {
            while (true){
                String option = (String) objIn.readObject();
                
                //Client asks for public key and it's sent to them 
                if (option.equalsIgnoreCase("PublicKey")){ 
                    PublicKey publicK = authenticator.getPublicKey();
                    System.out.println(publicK.toString());
                    objOut.writeObject(publicK);
                    System.out.println("");
                    
                //Client is requesting to login, invoke login() method 
                } else if (option.equalsIgnoreCase("Login")){ 
                    System.out.println(thdString + "Login");
                    login();
                    System.out.println("");
                    
                //Client is requesting to register, invoke register() method 
                } else if (option.equalsIgnoreCase("Register")){ 
                    System.out.println(thdString + "Register");
                    register();
                    System.out.println("");
                    
                //Client is requesting all Orders, prepare objects to send 
                } else if (option.equalsIgnoreCase("AllOrders")){ 
                    System.out.println(thdString + "AllOrders");
                    List<Order> allOrders = database.getAllOrders(); 
                    objOut.writeObject(allOrders); 
                    
                    //Including all relevant customers, and products 
                    List<Account> allAccounts = database.getAllAccounts(); 
                    objOut.writeObject(allAccounts); 
                    
                    List<Product> allProducts = database.getAllProducts(); 
                    objOut.writeObject(allProducts); 
                    
                    ArrayList<DeliverySchedule> deliverySchedules = database.getDeliverySchedules();
                    objOut.writeObject(deliverySchedules);
                    
                    System.out.println("");
                    
                //Client requesting list of all Customers (more so all Accounts) 
                } else if (option.equalsIgnoreCase("AllCustomers")){ 
                    System.out.println(thdString + "AllCustomers");
                    List<Account> allAccounts = database.getAllAccounts();
                    objOut.writeObject(allAccounts); 
                    System.out.println("");
                    
                //Client requesting all Products 
                } else if (option.equalsIgnoreCase("AllProducts")){ 
                    System.out.println(thdString + "AllProducts");
                    List<Product> productlist = database.getAllProducts();
                    objOut.writeObject(productlist);
                    System.out.println("");
                    
                //Client requesting all Delivery Schedules 
                } else if (option.equalsIgnoreCase("FullDeliverySchedule")){ 
                    System.out.println(thdString + "FullDeliverySchedule");
                    ArrayList<DeliverySchedule> deliverySchedules = database.getDeliverySchedules();
                    objOut.writeObject(deliverySchedules);
                    System.out.println("");
                    
                //Client requesting specific Schedule via postcode, so postcode is incoming too 
                } else if (option.equalsIgnoreCase("DeliveryScheduleByPostcode")) {
                    System.out.println(thdString + "DeliveryScheduleByPostcode");
                    int postcode = (Integer) objIn.readObject();
                    DeliverySchedule schedule = database.getDeliveryScheduleByPostcode(postcode);
                    objOut.writeObject(schedule);
                    System.out.println("");
                    
                //Client is requesting to place an Order, expecting Order object 
                } else if (option.equalsIgnoreCase("PlaceOrder")){ 
                    System.out.println(thdString + "Placeorder");
                    Order order = (Order) objIn.readObject();
                    database.saveOrder(order);
                    System.out.println("");
                    
                //Client requesting order for specific Customer, expecting ID 
                } else if (option.equalsIgnoreCase("GetCustomerOrder")){ 
                    System.out.println(thdString + "GetCustomerOrder");
                    int customerId = (Integer) objIn.readObject();
                    Order order = database.getOrderByCustomerId(customerId);
                    objOut.writeObject(order);
                    System.out.println("");
                    
                //
                } else if (option.equalsIgnoreCase("CancelOrder")){ 
                    System.out.println(thdString + "CancelOrder");
                    int customerId = (Integer) objIn.readObject();
                    Order order = database.getOrderByCustomerId(customerId);
                    int orderId = order.getOrderId(); // TODO: Order not null
                    database.deleteOrder(orderId);
                    System.out.println("");
                    
                //Client wishes to record a Product, expecting Product object 
                } else if (option.equalsIgnoreCase("RecordProduct")){ 
                    System.out.println(thdString + "RecordProduct"); 
                    Product product = (Product) objIn.readObject();
                    boolean check = database.addProduct(product); 
                    if (check) { 
                        objOut.writeObject("RecordProductSuccess");
                    } else { 
                        objOut.writeObject("RecordProductFail");
                    }
                    System.out.println("");
                    
                //Client requesting to delete Product, expecting an int ID
                } else if (option.equalsIgnoreCase("DeleteProduct")){ 
                    System.out.println(thdString + "DeleteProduct");
                    int productId = (Integer) objIn.readObject(); 
                    boolean check = database.deleteProduct(productId);
                    if (check) { 
                        objOut.writeObject("DeleteProductSuccess");
                    } else { 
                        objOut.writeObject("DeleteProductFail");
                    }
                    System.out.println("");
                    
                //Client requesting to input or edit a schedule 
                } else if (option.equalsIgnoreCase("RecordSchedule")){ 
                    System.out.println(thdString + "RecordSchedule");
                    DeliverySchedule schedule = (DeliverySchedule) objIn.readObject();
                    boolean check = database.recordDeliverySchedule(schedule);
                    if (check) { 
                        objOut.writeObject("RecordScheduleSuccess"); 
                    } else { 
                        objOut.writeObject("RecordScheduleFail"); 
                    }
                    System.out.println("");
                    
                //Client requesting to delete a schedule 
                } else if (option.equalsIgnoreCase("DeleteSchedule")){ 
                    System.out.println(thdString + "DeleteSchedule");
                    DeliverySchedule schedule = (DeliverySchedule) objIn.readObject();
                    boolean check = database.deleteDeliverySchedule(schedule); 
                    if (check) { 
                        objOut.writeObject("DeleteScheduleSuccess");
                    } else { 
                        objOut.writeObject("DeleteScheduleFail");}
                    System.out.println("");
                    
                //Client requesting to edit a Product 
                } else if (option.equalsIgnoreCase("EditProduct")) { 
                    System.out.println(thdString + "EditProduct"); 
                    Product product = (Product) objIn.readObject();
                    boolean check = database.updateProduct(product); 
                    if (check) { 
                        objOut.writeObject("UpdateProductSuccess");
                    } else { 
                        objOut.writeObject("UpdateProductFail");}
                    System.out.println("");
                    
                //Client requesting to edit a Schedule, might be deprecated
                } else if (option.equalsIgnoreCase("EditSchedule")) { 
                    System.out.println(thdString + "EditSchedule"); 
                    DeliverySchedule schedule = (DeliverySchedule) objIn.readObject();
                    boolean check = database.recordDeliverySchedule(schedule); 
                    if (check) { 
                        objOut.writeObject("EditScheduleSuccess"); 
                    } else { 
                        objOut.writeObject("EditScheduleFail"); 
                    }
                    System.out.println("");
                }
            }
        } catch(IOException e){System.out.println(/*close failed*/);
        } catch(ClassNotFoundException ex){System.out.println("CNF Error:"+ex.getMessage());
        } finally{try{objIn.close();clientSocket.close();}catch(IOException e){/*close failed*/}}
    }
    
    /** 
     * Beings in the user account for Login and checks the password after decrypting 
     * with the password from the same email account from the DB. 
     * 
     * @throws IOException              Input / Output exception occurred 
     * @throws ClassNotFoundException   Class defined by the Object Streams is 
     *                                  not findable, or is a mismatch 
     */
    private void login() throws IOException, ClassNotFoundException {
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
            } catch (IOException ex) {System.out.println("IO: " + ex.getMessage());
            } 
        } else {
            System.out.println("Login failed: Account not found.");
            objOut.writeObject(null);
        }
    }
    
    /** 
     * Used during registration. Will create the account and add it to the DB. 
     * After adding it will set a null password for the account and send it back 
     * to the client. 
     * 
     * @throws IOException              Input / Output exception has occurred 
     * @throws ClassNotFoundException   Class defined by the Object Streams is 
     *                                  not findable, or is a mismatch 
     */
    private void register() throws IOException, ClassNotFoundException {        
        Account acc = (Account) objIn.readObject();
        try {
            // Check if an account with the email already exists
            Account existingAccount = database.getAccountByEmail(acc.getEmailAddress());
            if (existingAccount != null) {
                // Account with this email already exists
                objOut.writeObject(null);
                System.out.println("Registration failed: Account with this email already exists.");
                return;
            }
        
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
        } catch (IOException ex) {System.out.println("IO: " + ex.getMessage());
        }
    }
}