package server;

import java.sql.*;
import common.model.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author lucht
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost/mdhsDB";
    private static final String USERNAME = "root";
    private static final String PASSWORD = System.getenv("DBMSpassword"); //your own password to Root account of MySQL
    private Connection connection = null;
    
    //private PreparedStatement 
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
    private PreparedStatement deleteProduct = null; 
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
    private PreparedStatement updateOrder = null;
    private PreparedStatement updateOrderItem = null;
    
    //Database Queries -- DeliverySchedule related 
    private PreparedStatement addDeliverySchedule = null; 
    private PreparedStatement getAllDeliverySchedules = null; 
    private PreparedStatement getDeliveryScheduleByPostcode = null;
    
    public DatabaseConnection () {
        try {
            // attempt to establish dmbs connection
            connection = DriverManager.getConnection( URL,USERNAME,PASSWORD );
            prepareDatabaseQueries(); 
        } catch (SQLException sqlException ) {
            //System.out.println("Database connection failure. \nPlease check your dbms password.");   
            //sqlException.printStackTrace();
            System.out.println("SQLException, error message: " + sqlException);
            sqlException.printStackTrace();
            System.exit( 1 ); // exit if couldnt connect to db
        }
    }
    
    /** 
     * This method prepares the queries for use with program
     * @throws SQLException 
     */
    private void prepareDatabaseQueries() throws SQLException{ 
        // = connection.prepareStatement("");
        
        //Account related queries
        addAccount = connection.prepareStatement("INSERT INTO Account "
                + "(firstName, lastName, emailAddress, `password`, phoneNumber, deliveryAddress, postcode, isAdmin)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        getAccountByEmail = connection.prepareStatement("SELECT * "
                + "FROM Account "
                + "WHERE emailAddress = ?");
        getAllAccounts = connection.prepareStatement("SELECT * "
                + "FROM account");
        getCustomerById = connection.prepareStatement("SELECT * "
                + "FROM account "
                + "WHERE accountId = ?");
        
        //Product related queries
        addProduct = connection.prepareStatement("INSERT INTO Product "
                + "(productName, quantity, unit, price, ingredients) "
                + "VALUES(?, ?, ?, ?, ?)");
        editProduct = connection.prepareStatement("UPDATE Product "
                + "SET productName = ?, quantity = ?, unit = ?, price = ?, ingredients = ? "
                + "WHERE productId = ?"); //productName is another option, but error handle uniqueness 
        getAllProducts = connection.prepareStatement("SELECT * "
                + "FROM Product");
        getProductById = connection.prepareStatement("SELECT * "
                + "FROM Product "
                + "WHERE productId = ?");
        productExists = connection.prepareStatement("SELECT COUNT(*) "
                + "FROM Product "
                + "WHERE productName = ? AND quantity = ? AND unit = ? AND price = ? AND ingredients = ?");
        // TODO: handle delete product right now, so will leave for later 
        
        
        //Order related queries 
        addOrder = connection.prepareStatement("INSERT INTO `Order` "
                + "(accountId, deliveryTime, totalCost) "
                + "VALUES(?, ?, ?)", 
                Statement.RETURN_GENERATED_KEYS);
        addOrderItem = connection.prepareStatement("INSERT INTO OrderItems "
                + "(orderId, productId, quantity, cost) "
                + "VALUES(?, ?, ?, ?)");
        
        getAllOrders = connection.prepareStatement("SELECT * FROM `Order`");
        getOrderByCustomerId = connection.prepareStatement("SELECT * "
                + "FROM `Order` "
                + "WHERE accountId = ?"); 
        getOrderitemsByOrderId = connection.prepareStatement("SELECT * "
                + "FROM `OrderItems` "
                + "WHERE orderId = ?"); 
        
        deleteOrder = connection.prepareStatement("DELETE FROM `Order` WHERE orderId = ?");
        deleteOrderItems = connection.prepareStatement("DELETE FROM `OrderItems` WHERE orderId = ?");

        updateOrder = connection.prepareStatement("UPDATE `Order` SET accountId = ?, deliveryTime = ?, totalCost = ? WHERE orderId = ?");
        updateOrderItem = connection.prepareStatement("UPDATE `OrderItems` SET productId = ?, quantity = ?, cost = ? WHERE orderId = ? AND productId = ?");


        //Delivery Schedule related queries 
        addDeliverySchedule = connection.prepareStatement("INSERT INTO DeliverySchedule "
                + "(postcode, deliveryDay, deliveryCost)"
                + "VALUES(?, ?, ?)"); 
        getAllDeliverySchedules = connection.prepareStatement("SELECT * "
                + "FROM DeliverySchedule");
        getDeliveryScheduleByPostcode = connection.prepareStatement("SELECT * "
                + "FROM `DeliverySchedule` "
                + "WHERE postcode = ? ");
    }
    
    public Account getAccountByEmail(String email) { 
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
    
    public Boolean addAccount(Account acc) { 
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
     * @param products ArrayList of products to be added to the database
     */
    public void addProductsFromFile(ArrayList<Product> products) { 
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
     * 
     * @param product to check if already in database
     * @return true if the product exists, false otherwise
     */
    private boolean productExists(Product product) {
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
    
    public ArrayList<DeliverySchedule> getDeliverySchedules() {
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
   
    public DeliverySchedule getDeliveryScheduleByPostcode(int postcode) {
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
   
    public List<Product> getAllProducts() {
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
   
    public Order getOrderByCustomerId(int custId){
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
   
    public void deleteOrder(int orderId) {
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

    public void saveOrder(Order order) {
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
        } catch (SQLException sqlException) {System.out.println("SQL Exception: " + sqlException.getMessage());
        } 
    }
    
    /** 
    * 
    * @return 
    */
   public List<Account> getAllAccounts() { 
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
   
   public List<Order> getAllOrders() { 
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
   
   public Product getProductById(int productId) { 
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
   
   public Customer getCustomerById(int customerId) { 
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
}
