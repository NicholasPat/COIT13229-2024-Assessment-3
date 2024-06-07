package server;

import java.sql.*;
import common.model.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
/**
 * DatabaseConnection.java class.<p> 
 * This class is responsible for communicating with the Database (DB). This is 
 * done via Prepared Statements which are more secure as using Strings leads the 
 * ability for malicious actors to insert their own MySQL code into the queries 
 * and potentially damage the DB. 
 * 
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost/mdhsDB";
    private static final String USERNAME = "root";
    private static final String PASSWORD = System.getenv("DBMSpassword"); //your own password to Root account of MySQL
    private Connection connection = null;
    
    //Database Queries -- Account related 
    private PreparedStatement addAccount = null; 
    private PreparedStatement getAccountByEmail = null; 
    private PreparedStatement getAllAccounts = null; 
    private PreparedStatement getCustomerById = null; 
    
    //Database Queries -- Product related 
    private PreparedStatement addProduct = null; 
    private PreparedStatement editProduct = null; 
    private PreparedStatement getAllProducts = null; 
    private PreparedStatement getProductById = null; 
    private PreparedStatement deleteProductById = null; 
    private PreparedStatement productExists = null;
    
    //Database Queries -- Order related 
    private PreparedStatement addOrder = null; 
    private PreparedStatement getAllOrders = null; 
    private PreparedStatement getOrderByCustomerId = null; 
    private PreparedStatement getOrderByPostcode = null; 
    private PreparedStatement addOrderItem = null; 
    private PreparedStatement getOrderitemsByOrderId = null; 
    private PreparedStatement deleteOrder = null;
    private PreparedStatement deleteOrderItems = null;
    private PreparedStatement deleteOrderItem = null; 
    private PreparedStatement updateOrder = null;
    private PreparedStatement updateOrderItem = null;
    
    //Database Queries -- DeliverySchedule related 
    private PreparedStatement addDeliverySchedule = null; 
    private PreparedStatement getAllDeliverySchedules = null; 
    private PreparedStatement getDeliveryScheduleByPostcode = null;
    private PreparedStatement checkDeliverySchedule = null;
    private PreparedStatement insertDeliverySchedule = null;
    private PreparedStatement updateDeliverySchedule = null;
    private PreparedStatement deleteDeliverySchedule = null;
    
    private PreparedStatement disableKeyChecks = null; 
    private PreparedStatement enableKeyChecks = null; 
    
    /** 
     * Prepares the connection. Upon successful connection calls for the method to 
     * prepare all the statements for use with the program 
     */
    public DatabaseConnection () {
        try {
            connection = DriverManager.getConnection( URL,USERNAME,PASSWORD );
            prepareDatabaseQueries(); 
        } catch (SQLException sqlException ) {
            System.out.println("SQLException, error message: " + sqlException);
            System.exit( 1 ); // exit if couldnt connect to db
        }
    }
    
    /** 
     * This method prepares the queries for use with the program. 
     * @throws SQLException Throws if there is an issue preparing the statements 
     *                      or if there is an issue with the connection for whatever reason. 
     */
    private void prepareDatabaseQueries() throws SQLException{
        //Account related queries
        addAccount = connection.prepareStatement("INSERT INTO Account (firstName, lastName, emailAddress, `password`, phoneNumber, deliveryAddress, postcode, isAdmin) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        getAccountByEmail = connection.prepareStatement("SELECT * FROM Account WHERE emailAddress = ?");
        getAllAccounts = connection.prepareStatement("SELECT * FROM account");
        getCustomerById = connection.prepareStatement("SELECT * FROM account WHERE accountId = ?");
        
        //Product related queries
        addProduct = connection.prepareStatement("INSERT INTO Product (productName, quantity, unit, price, ingredients) "
                + "VALUES(?, ?, ?, ?, ?)");
        editProduct = connection.prepareStatement("UPDATE Product SET productName = ?, quantity = ?, unit = ?, price = ?, ingredients = ? "
                + "WHERE productId = ?"); 
        getAllProducts = connection.prepareStatement("SELECT * FROM Product");
        getProductById = connection.prepareStatement("SELECT * FROM Product WHERE productId = ?");
        productExists = connection.prepareStatement("SELECT COUNT(*) FROM Product "
                + "WHERE productName = ? AND quantity = ? AND unit = ? AND price = ? AND ingredients = ?");
        deleteProductById = connection.prepareStatement("DELETE FROM Product WHERE productId = ?"); 
        editProduct = connection.prepareStatement("UPDATE `Product` SET productName = ?, quantity = ?, unit = ?, price = ?, ingredients = ? "
                + "WHERE productId = ?");
        
        //Order related queries 
        addOrder = connection.prepareStatement("INSERT INTO `Order` (accountId, deliveryTime, totalCost) "
                + "VALUES(?, ?, ?)", 
                Statement.RETURN_GENERATED_KEYS);
        addOrderItem = connection.prepareStatement("INSERT INTO OrderItems (orderId, productId, quantity, cost) "
                + "VALUES(?, ?, ?, ?)");
        getAllOrders = connection.prepareStatement("SELECT * FROM `Order`");
        getOrderByCustomerId = connection.prepareStatement("SELECT * FROM `Order` WHERE accountId = ?"); 
        getOrderitemsByOrderId = connection.prepareStatement("SELECT * FROM `OrderItems` WHERE orderId = ?"); 
        
        deleteOrder = connection.prepareStatement("DELETE FROM `Order` WHERE orderId = ?");
        deleteOrderItems = connection.prepareStatement("DELETE FROM `OrderItems` WHERE orderId = ?");
        deleteOrderItem = connection.prepareStatement("DELETE FROM `OrderItems` WHERE orderId = ? AND productId = ?"); 
        
        updateOrder = connection.prepareStatement("UPDATE `Order` SET accountId = ?, deliveryTime = ?, totalCost = ? WHERE orderId = ?");
        updateOrderItem = connection.prepareStatement("UPDATE `OrderItems` SET productId = ?, quantity = ?, cost = ? WHERE orderId = ? AND productId = ?");
        
        //Delivery Schedule related queries 
        addDeliverySchedule = connection.prepareStatement("INSERT INTO DeliverySchedule (postcode, deliveryDay, deliveryCost VALUES(?, ?, ?)"); 
        getAllDeliverySchedules = connection.prepareStatement("SELECT * FROM DeliverySchedule");
        getDeliveryScheduleByPostcode = connection.prepareStatement("SELECT * FROM `DeliverySchedule` WHERE postcode = ? ");
        checkDeliverySchedule = connection.prepareStatement("SELECT COUNT(*) FROM DeliverySchedule WHERE postcode = ?");
        insertDeliverySchedule = connection.prepareStatement("INSERT INTO DeliverySchedule (postcode, deliveryDay, deliveryCost) VALUES (?, ?, ?)");
        updateDeliverySchedule = connection.prepareStatement("UPDATE DeliverySchedule SET deliveryDay = ?, deliveryCost = ? WHERE postcode = ?");
        deleteDeliverySchedule = connection.prepareStatement("DELETE FROM DeliverySchedule WHERE postcode = ?");
        
        disableKeyChecks = connection.prepareStatement("SET foreign_key_checks = 0;"); 
        enableKeyChecks = connection.prepareStatement("SET foreign_key_checks = 1"); 
    }
    
    /** 
     * Gets an account via email. This is used for the login of the Customer / 
     * Admin. 
     * 
     * @param email Searching for user via their email 
     * @return      An Account with the linked email, or if no match simply returns a null object
     */
    public synchronized Account getAccountByEmail(String email) { 
        //Assumption - 1 result as cannot have two usernames 
        ResultSet results = null ; 
        Account acc = null ; 
        
        try{ 
            getAccountByEmail.setString(1, email) ; 
            results = getAccountByEmail.executeQuery() ; 
            
            while (results.next()) { 
                int isAdmin = results.getInt("isAdmin");
                if (isAdmin == 1) {
                    acc = new Administrator();
                } else {
                    acc = new Customer();
                }
                
                acc.setAccountId(results.getInt("accountId"));
                acc.setFirstName(results.getString("firstName"));
                acc.setLastName(results.getString("lastName"));
                acc.setEmailAddress(results.getString("emailAddress"));
                // Convert the password to byte array
                String passwordString = results.getString("password");
                byte[] passwordBytes = passwordString.getBytes(StandardCharsets.UTF_8);
                acc.setPassword(passwordBytes);
                
                if (isAdmin == 1) {
                    ((Administrator) acc).setIsAdmin(true);
                } else {
                    ((Customer) acc).setPhoneNumber(results.getInt("phoneNumber"));
                    ((Customer) acc).setDeliveryAddress(results.getString("deliveryAddress"));
                    ((Customer) acc).setPostcode(results.getInt("postcode"));
                }

            } 
        } catch (SQLException sqlException) {System.out.println("SQL Exception: " + sqlException.getMessage());
        } finally {try {results.close() ;}catch (SQLException sqlException){System.out.println("SQL Exception: " + sqlException.getMessage());}}
        //System.out.println(acc);
        return acc;
    }
    
    /** 
     * Attempts to add an Account to the DB using parameters determined by the client. 
     * 
     * @param acc   Account object being added to the DB 
     * @return      TRUE - Successful addition of Account / 
     *              FALSE - Unsuccessful addition of Account
     */
    public synchronized Boolean addAccount(Account acc) { 
        try { 
            String password = new String(acc.getPassword(), StandardCharsets.UTF_8);
            
            addAccount.setString(1, acc.getFirstName());
            addAccount.setString(2, acc.getLastName());
            addAccount.setString(3, acc.getEmailAddress());
            addAccount.setString(4, password);
                
            if (acc instanceof Administrator) {
                addAccount.setNull(5, Types.INTEGER);
                addAccount.setNull(6, Types.VARCHAR); 
                addAccount.setNull(7, Types.INTEGER);
                addAccount.setInt(8, 1);
            } else if (acc instanceof Customer) {
                Customer customer = (Customer) acc;
                addAccount.setInt(5, customer.getPhoneNumber());
                addAccount.setString(6, customer.getDeliveryAddress());
                addAccount.setInt(7, customer.getPostcode());
                addAccount.setInt(8, 0); 
            } else {
                return null;
            }
           
            int resultSet = addAccount.executeUpdate() ; 
            
            //0 is thrown if no rows are updated, so if successful, should result in a number that is not 0
            if (resultSet != 0) { 
                return true;
            } else { 
                return false; 
            }
            
        } catch (SQLException ex) { 
            System.out.println("SQL Exception: " + ex.getMessage()) ; 
            return false;
        }
    }
    
    /** 
     * Adds a list of products to the database from a given ArrayList. 
     * 
     * @param products ArrayList of products to be added to the database
     */
    public synchronized void addProductsFromFile(ArrayList<Product> products) { 
        for (Product product : products) {
            String productName = product.getProductName();
            String unit = product.getUnit();
            int quantity = product.getQuantity();
            double price = product.getPrice();
            String ingredients = product.getIngredients();
            
            if (!productExists(product)) {
                try{ 
                    addProduct.setString(1, productName);
                    addProduct.setInt(2, quantity);
                    addProduct.setString(3, unit);
                    addProduct.setDouble(4, price);
                    addProduct.setString(5, ingredients);

                    int result = addProduct.executeUpdate();
                    System.out.println("Added product with name: " + productName);
                } catch (SQLException e) {
                    System.out.println("Error executing update to add product: " + productName + "\nMessage: " + e.getMessage());
                }
            } else {
                System.out.println("Product: " + product.toString() + " already exists. Skipping insertion.");
            }
        }
    }
    
    /**
     * Checks if the product in question exists already or not 
     * 
     * @param product to check if already in database
     * @return true if the product exists, false otherwise
     */
    private synchronized boolean productExists(Product product) {
        String productName = product.getProductName();
        String unit = product.getUnit();
        int quantity = product.getQuantity();
        double price = product.getPrice();
        String ingredients = product.getIngredients();
        
        try {
            productExists.setString(1, productName);
            productExists.setInt(2, quantity);
            productExists.setString(3, unit);
            productExists.setDouble(4, price);
            productExists.setString(5, ingredients);
            try (ResultSet resultSet = productExists.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking product existence for: " + productName + "\nMessage: " + e.getMessage());
        }
        return false;
    }
    
    /** 
     * Creates an ArrayList with all of the Delivery Schedules. 
     * 
     * @return ArrayList of the Delivery Schedules 
     */
    public synchronized ArrayList<DeliverySchedule> getDeliverySchedules() {
        ArrayList<DeliverySchedule> deliverySchedules = new ArrayList<>();

        try (ResultSet resultSet = getAllDeliverySchedules.executeQuery()) {
            while (resultSet.next()) {
                int postcode = resultSet.getInt("postcode");
                String deliveryDay = resultSet.getString("deliveryDay");
                double deliveryCost = resultSet.getDouble("deliveryCost");

                deliverySchedules.add(new DeliverySchedule(postcode, deliveryDay, deliveryCost));
            }
        } catch (SQLException e) {
            System.err.println("Error loading delivery schedules: " + e.getMessage());
        }

        return deliverySchedules;
    }
    
    /** 
     * Gets any and all Delivery Schedules using postcode identifier. 
     * 
     * @param postcode  Used to find a relevant Delivery Schedule 
     * @return          Either found Delivery Schedule or a null object 
     */
    public synchronized DeliverySchedule getDeliveryScheduleByPostcode(int postcode) {
        ResultSet resultSet = null ; 
        DeliverySchedule schedule = null ; 
        
        try{ 
            getDeliveryScheduleByPostcode.setInt(1, postcode) ; 
            resultSet = getDeliveryScheduleByPostcode.executeQuery() ; 
            
            while (resultSet.next()) { 
                String deliveryDay = resultSet.getString("deliveryDay");
                double deliveryCost = resultSet.getDouble("deliveryCost");

                schedule = new DeliverySchedule(postcode, deliveryDay, deliveryCost);

            } 
        } catch (SQLException sqlException) {System.out.println("SQL Exception: " + sqlException.getMessage());
        } 
        
        return schedule;
    }
    
    /** 
     * Get all Products from the DB. 
     * 
     * @return List of all the Products 
     */
    public synchronized List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        try (ResultSet resultSet = getAllProducts.executeQuery()) {
            while (resultSet.next()) {
                int productId = resultSet.getInt("productId");
                String productName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");
                String unit = resultSet.getString("unit");
                double price = resultSet.getDouble("price");
                String ingredients = resultSet.getString("ingredients");

                products.add(new Product(productId, productName, quantity, unit, price, ingredients));
            }
        } catch (SQLException e) {
            System.err.println("Error loading delivery schedules: " + e.getMessage());
        }

        return products;
    }
    
    /** 
     * Gets the Order via Customer ID. 
     * 
     * @param custId    Customer ID used to get the Order in question 
     * @return          Return the Order in relation to the Customer ID 
     */
    public synchronized Order getOrderByCustomerId(int custId){
        ResultSet resultSet = null ; 
        Order order = null ; 
        List<OrderItem> orderItems = new ArrayList<>();
        
        int orderId = -1;
        int accountId = -1;
        String deliveryTime = null;
        double totalCost = -1;
        
        try{ 
            getOrderByCustomerId.setInt(1, custId) ; 
            resultSet = getOrderByCustomerId.executeQuery() ; 
            
            while (resultSet.next()) { 
                orderId = resultSet.getInt("orderId");
                accountId = resultSet.getInt("accountId");
                deliveryTime = resultSet.getString("deliveryTime");
                totalCost = resultSet.getDouble("totalCost");
            } 
            
            if (orderId != -1) {
                getOrderitemsByOrderId.setInt(1, orderId);
                resultSet = getOrderitemsByOrderId.executeQuery() ; 

                while (resultSet.next()) { 
                    int productId = resultSet.getInt("productId");
                    int quantity = resultSet.getInt("quantity");
                    double cost = resultSet.getDouble("cost");
                    
                    orderItems.add(new OrderItem(orderId, productId, quantity, cost));
                } 
                
                order = new Order(orderId, accountId, deliveryTime, orderItems, totalCost);
            }
            
        } catch (SQLException sqlException) {System.out.println("SQL Exception: " + sqlException.getMessage());
        } 
        
        return order;
   }
    
    /** 
     * Deletes an Order in question. 
     * 
     * @param orderId   Order ID used to identify what Order to delete 
     */
    public synchronized void deleteOrder(int orderId) {
       try {
            // Delete all order items associated with the order
            deleteOrderItems.setInt(1, orderId);
            deleteOrderItems.executeUpdate();

            // Delete the order
            deleteOrder.setInt(1, orderId);
            deleteOrder.executeUpdate();
        } catch (SQLException sqlException) {System.out.println("SQL Exception: " + sqlException.getMessage());
        } 
    }
    
    /** 
     * Adds an Order to the DB based on Customer variables specified. 
     * 
     * @param order Order object used to add to the DB 
     */
    public synchronized void saveOrder(Order order) {
        try {
            if (order.getOrderId() != 0) {
                // Update existing order
                updateOrder.setInt(1, order.getCustomerId());
                updateOrder.setString(2, order.getDeliveryTime());
                updateOrder.setDouble(3, order.getTotalCost());
                
                updateOrder.setInt(4, order.getOrderId());
                updateOrder.executeUpdate();

                // Delete existing order items
                deleteOrderItems.setInt(1, order.getOrderId());
                deleteOrderItems.executeUpdate();

                // Insert updated order items
                for (OrderItem item : order.getProductList()) {
                    addOrderItem.setInt(1, order.getOrderId());
                    addOrderItem.setInt(2, item.getProductId());
                    addOrderItem.setInt(3, item.getQuantity());
                    addOrderItem.setDouble(4, item.getCost());
                    addOrderItem.executeUpdate();
                }
                
            } else {
                // Insert new order
                addOrder.setInt(1, order.getCustomerId());
                addOrder.setString(2, order.getDeliveryTime());
                addOrder.setDouble(3, order.getTotalCost());
                addOrder.executeUpdate();

                ResultSet generatedKeys = addOrder.getGeneratedKeys(); // Get the generated order ID
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);

                    // Insert order items
                    for (OrderItem item : order.getProductList()) {
                        addOrderItem.setInt(1, orderId);
                        addOrderItem.setInt(2, item.getProductId());
                        addOrderItem.setInt(3, item.getQuantity());
                        addOrderItem.setDouble(4, item.getCost());
                        addOrderItem.executeUpdate();
                    }
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        } catch (SQLException sqlException) {
            System.out.println("SQL Exception: " + sqlException.getMessage());
        } 
    }
    
    /** 
    * Gets all accounts from the DB. 
    * 
    * @return List of all Accounts 
    */
    public synchronized List<Account> getAllAccounts() { 
       List<Account> accounts = new ArrayList<>(); 
       
       try (ResultSet resultSet = getAllAccounts.executeQuery()) { 
           while (resultSet.next()) { 
               int accountId = resultSet.getInt("accountId"); 
               String firstName = resultSet.getString("firstName"); 
               String lastName = resultSet.getString("lastName"); 
               String emailAddress = resultSet.getString("emailAddress"); 
               //Not bothering with password, not a proper value to pass 
               int phoneNumber = resultSet.getInt("phoneNumber"); 
               String address = resultSet.getString("deliveryAddress"); 
               int postcode = resultSet.getInt("postcode");
               int isAdmin = resultSet.getInt("isAdmin"); 
               
               if (isAdmin == 1) {
                   accounts.add(new Administrator(true, accountId, firstName, 
                       lastName, emailAddress, null)); 
               } else {
                   accounts.add(new Customer(phoneNumber, address, postcode, accountId,
                       firstName, lastName, emailAddress, null)); 
               } 
           }
       } catch (SQLException e) {
            System.err.println("Error loading delivery schedules: " + e.getMessage());
        }
       
       return accounts;
    }
    
    /** 
     * Gets all Orders from the DB. 
     * 
     * @return List of all Orders 
     */
    public synchronized List<Order> getAllOrders() { 
       List<Order> orders = new ArrayList<>();
       
       try (ResultSet resultSet = getAllOrders.executeQuery()) {
           while (resultSet.next()) { 
               //Getting initial order information
               int orderId = resultSet.getInt("orderId"); 
               int customerId = resultSet.getInt("accountId");
               String deliveryTime = resultSet.getString("deliveryTime");
               double totalCost = resultSet.getDouble("totalCost"); 
               
               //Getting orderItems 
               List<OrderItem> orderItems = new ArrayList<>(); 
               getOrderitemsByOrderId.setInt(1, orderId);
               ResultSet orderItemsResults = getOrderitemsByOrderId.executeQuery();
               
               //Assuming always have an OrderItem. Should also iterate over all the 
               //OrderItems that exist 
               while (orderItemsResults.next()) { 
                   int productId = orderItemsResults.getInt("productId"); 
                   int quantity = orderItemsResults.getInt("quantity"); 
                   double cost = orderItemsResults.getDouble("cost"); 
                   orderItems.add(new OrderItem(orderId, productId, quantity, cost)); 
               }
               orders.add(new Order(customerId, deliveryTime, orderItems, totalCost)); 
           }
       }catch (SQLException e) {
            System.err.println("Error in `getAllOrders()`: " + e.getMessage());
        }
       
       return orders; 
    }
    
    /** 
     * Gets a singular Product from the DB based on the Product ID. 
     * 
     * @param productId ID to be used for the entry search 
     * @return Singular Product object 
     */
    public synchronized Product getProductById(int productId) { 
       Product currentProduct = null; 
       
       try { 
           getProductById.setInt(1, productId);
           ResultSet resultSet = getProductById.executeQuery(); 
           
           //No results. Return an empty object which will error handle 
           if (!resultSet.isBeforeFirst()) { 
               return currentProduct;
           }
           
           //Should return just 1 result, or none, error handle none done above
           while (resultSet.next()) { 
               String productName = resultSet.getString("productName"); 
               int quantity = resultSet.getInt("quantity"); 
               String unit = resultSet.getString("unit"); 
               double price = resultSet.getDouble("price"); 
               String ingredients = resultSet.getString("ingredients"); 
               
               currentProduct = new Product(productId, productName, quantity, 
                       unit, price, ingredients); 
           }
       }catch (SQLException e) {
            System.err.println("Error in `getProductById`: " + e.getMessage());
        }
       
       return currentProduct; 
    }
    
    /** 
     * Gets Customer object via ID. 
     * 
     * @param customerId    ID to search for Customer with 
     * @return              Single Customer object 
     */
    public synchronized Customer getCustomerById(int customerId) { 
       Customer currentCustomer = null; 
       
       try { 
           getCustomerById.setInt(1, customerId); 
           ResultSet resultSet = getCustomerById.executeQuery(); 
           
           //Should always return a valid customer 
           while (resultSet.next()) { 
               String firstName = resultSet.getString("firstName"); 
               String lastName = resultSet.getString("lastName"); 
               String email = resultSet.getString("emailAddress"); 
               byte[] password = null; 
               int phoneNumber = resultSet.getInt("phoneNumber"); 
               String deliveryAddress = resultSet.getString("deliveryAddress");
               int postcode = resultSet.getInt("postcode"); 
               
               currentCustomer = new Customer(phoneNumber, deliveryAddress, postcode, 
                       customerId, firstName, lastName, email, password);
           }
       }catch (SQLException e) {
            System.err.println("Error in `getCustomerById()`: " + e.getMessage());
        }
       
       return currentCustomer; 
    }
    
    /** 
     * Adds Delivery Schedule or updates if it already exists. 
     * 
     * @param schedule  Object with information for addition or editing 
     * @return          TRUE - Successful add or edit / FALSE - Unsuccessful add 
     *                  or edit 
     */
    public synchronized boolean recordDeliverySchedule(DeliverySchedule schedule) {
        try {
            //Check if the Delivery Schedule Exists 
            checkDeliverySchedule.setInt(1, schedule.getPostcode());
            ResultSet resultSet = checkDeliverySchedule.executeQuery();
            
            //If it does, set the state to true, if not then keep as false 
            boolean exists = false; 
            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
            
            //Perform an update or addition 
            if (exists) {
                System.out.println("Update: " + schedule.getPostcode());
                updateDeliverySchedule.setString(1, schedule.getDeliveryDay());
                updateDeliverySchedule.setDouble(2, schedule.getDeliveryCost());
                updateDeliverySchedule.setInt(3, schedule.getPostcode());
                updateDeliverySchedule.executeUpdate();
            } else {
                System.out.println("Insert: " + schedule.getPostcode());
                insertDeliverySchedule.setInt(1, schedule.getPostcode());
                insertDeliverySchedule.setString(2, schedule.getDeliveryDay());
                insertDeliverySchedule.setDouble(3, schedule.getDeliveryCost());
                insertDeliverySchedule.executeUpdate();
            }
        } catch (SQLException ex) {
            System.err.println("Error in `recordDeliverySchedule()`: " + ex.getMessage());
            return false; 
        }
        
        return true; 
    }
    
    /** 
     * Deletes a Schedule based on whole object. Mainly using postcode since 
     * Schedule can only have one per postcode. 
     * 
     * @param schedule  Schedule object to get the postcode for deletion 
     * @return          TRUE - Successful deletion / FALSE - Unsuccessful deletion 
     */
    public synchronized boolean deleteDeliverySchedule(DeliverySchedule schedule) {
        try {
            checkDeliverySchedule.setInt(1, schedule.getPostcode());
            ResultSet resultSet = checkDeliverySchedule.executeQuery();
            if (resultSet.next()) {
                disableKeyChecks.executeUpdate(); 
                deleteDeliverySchedule.setInt(1, schedule.getPostcode());
                deleteDeliverySchedule.executeUpdate();
                enableKeyChecks.executeUpdate(); 
            }
        } catch (SQLException ex) {
            System.err.println("Error in `deleteDeliverySchedule()`: " + ex.getMessage());
            return false;
        }
        
        //Assuming all good even at this point 
        return true; 
    }
    
    /** 
     * Updates the DON'T DEPRECATE YET! 
     * 
     * @param schedule  
     * @return          
     */
    public synchronized boolean updateDeliverySchedule(DeliverySchedule schedule) { 
        int postcode = schedule.getPostcode(); 
        String day = schedule.getDeliveryDay(); 
        double cost = schedule.getDeliveryCost(); 
        
        int result = 0; 
        
        try { 
            updateDeliverySchedule.setString(1, day); 
            updateDeliverySchedule.setDouble(2, cost); 
            updateDeliverySchedule.setInt(3, postcode); 
            
            result = updateDeliverySchedule.executeUpdate(); 
            
            if (result == 1) { 
                System.out.println("Successfully edited Delivery Schedule with postcode: " + postcode);
                return true; 
            } else { 
                System.out.println("Unsuccessfully edited Delivery Schedule with postcode: " + postcode); 
                return false; 
            }
            
        } catch (SQLException ex) {
            System.err.println("Error in `recordDeliverySchedule()`: " + ex.getMessage());
        }
        
        return true; 
    }
    
    /**
     * Adds product to DB. Used Add function in the client ManageProductController.java. 
     * 
     * @param product   Product to be added to the DB
     * @return          Identifier for if addition was successful 
     */
    public synchronized boolean addProduct(Product product) { 
        String productName = product.getProductName(); 
        String unit = product.getUnit(); 
        int quantity = product.getQuantity(); 
        double price = product.getPrice(); 
        String ingredients = product.getIngredients(); 
        
        int result; 
        
        try { 
            addProduct.setString(1, productName); 
            addProduct.setInt(2, quantity); 
            addProduct.setString(3, unit); 
            addProduct.setDouble(4, price); 
            addProduct.setString(5, ingredients); 
            
            result = addProduct.executeUpdate(); 
            
            if (result == 1) { 
                System.out.println("Successfully added product");
                return true; 
            } else { 
                System.out.println("Addition of product failed"); 
                return false; 
            }
        } catch (SQLException e) {
            System.err.println("Error adding Product: " + e.getMessage());
            return false; 
        }
    }
    
    /** 
     * Received product ID from the client and then actions deletion of the 
     * product from the DB. 
     * 
     * @param productId ID used to identify Product to delete. 
     * @return          TRUE - Successful deletion / FALSE - Unsuccessful deletion. 
     */
    public synchronized boolean deleteProduct(int productId) {
        int result; 
        try { 
            disableKeyChecks.executeUpdate(); 
            deleteProductById.setInt(1, productId); 
            result = deleteProductById.executeUpdate(); 
            enableKeyChecks.executeUpdate(); 
            
            if (result == 1) { 
                System.out.println("Successfully deleted product (code: " + result + ")");
                return true; 
            } else { 
                System.out.println("Unsuccessfully deleted product (code: " + result + ")"); 
                return false; 
            }
            
        } catch (SQLException ex) {
            System.err.println("Error in `deleteProduct()`: " + ex.getMessage());
            return false; 
        }
    }
    
    /** 
     * Edits the product completely, won't bother checking if values are varied.
     * This is so the server won't be bogged down trying to figure out what is 
     * different as well. Plus saves prepared statement space as would need one 
     * for each table column. 
     * 
     * @param product   Product object with variables to allow for update 
     * @return          TRUE - Successful update / FALSE - Unsuccessful update 
     */
    public synchronized boolean updateProduct(Product product) { 
        String productName = product.getProductName(); 
        int quantity = product.getQuantity(); 
        String unit = product.getUnit(); 
        double price = product.getPrice(); 
        String ingredients = product.getIngredients(); 
        int productId = product.getProductId(); 
        
        int result = 0; 
        
        try { 
            editProduct.setString(1, productName); 
            editProduct.setInt(2, quantity); 
            editProduct.setString(3, unit); 
            editProduct.setDouble(4, price); 
            editProduct.setString(5, ingredients); 
            editProduct.setInt(6, productId);
            
            result = editProduct.executeUpdate(); 
            
            if (result == 1) { 
                System.out.println("Successfully edited product with ID: " + productId);
                return true; 
            } else { 
                System.out.println("Unsuccessfully edited product with ID: " + productId); 
                return false; 
            }
            
        }catch (SQLException e) {
            System.err.println("Error varying Product: " + e.getMessage());
            return false; 
        }
    }
    
    
}
