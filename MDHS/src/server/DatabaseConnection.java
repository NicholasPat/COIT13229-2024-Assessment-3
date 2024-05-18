package server;

import java.sql.*;

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
                + "(firstName, lastName, emailAddress, `password`, phoneNumber, deliveryAddress, isAdmin)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?)");
        getAccountByEmail = connection.prepareStatement("SELECT * "
                + "FROM Account "
                + "WHERE email = ?");
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
}
