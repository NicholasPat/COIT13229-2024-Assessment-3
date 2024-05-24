package server;

import java.sql.*;
import common.model.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    
    //Database Queries -- DeliverySchedule related 
    private PreparedStatement addDeliverySchedule = null; 
    private PreparedStatement getAllDeliverySchedules = null; 
    
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
        
        //Product related queries
        addProduct = connection.prepareStatement("INSERT INTO Product "
                + "(productName, quantity, unit, price, ingredients) "
                + "VALUES(?, ?, ?, ?, ?)");
        editProduct = connection.prepareStatement("UPDATE Product "
                + "SET productName = ?, quantity = ?, unit = ?, price = ?, ingredients = ? "
                + "WHERE productId = ?"); //productName is another option, but error handle uniqueness 
        getAllProducts = connection.prepareStatement("SELECT * "
                + "FROM Products");
        getProductById = connection.prepareStatement("SELECT * "
                + "FROM Product "
                + "WHERE productId = ?");
        productExists = connection.prepareStatement("SELECT COUNT(*) "
                + "FROM Product "
                + "WHERE productName = ? AND quantity = ? AND unit = ? AND price = ? AND ingredients = ?");
        //Don't know how to handle delete product right now, so will leave for later 
        
        
        //Order related queries 
        addOrder = connection.prepareStatement("INSERT INTO `Order` "
                + "(deliveryTime, totalCost, postcode, accountId) "
                + "VALUES(?, ?, ?, ?)");
        getAllOrders = connection.prepareStatement("SELECT * FROM `Order`");
        getOrderByCustomerId = connection.prepareStatement("SELECT * "
                + "FROM `Order` "
                + "WHERE customerId = ?");
        getOrderByPostcode = connection.prepareStatement("SELECT * "
                + "FROM `Order` "
                + "WHERE postcode = ?"); 
        addOrderItem = connection.prepareStatement("INSERT INTO OrderItems "
                + "(orderId, productId, quantity, cost) "
                + "VALUES(?, ?, ?, ?)");
        
        //Delivery Schedule related queries 
        addDeliverySchedule = connection.prepareStatement("INSERT INTO DeliverySchedule "
                + "(postcode, deliveryDay, deliveryCost)"
                + "VALUES(?, ?, ?)"); 
        getAllDeliverySchedules = connection.prepareStatement("SELECT * "
                + "FROM DeliverySchedule");
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
    
   
}
