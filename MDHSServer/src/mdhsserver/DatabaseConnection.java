package mdhsserver;

import java.sql.*; 
import java.util.ArrayList;

/**
 * Note: SQL and Java implementation was learned during COIT12200 Software Design and 
 * Development. A lot of the code used was from there and was utilised 
 * @author linke
 */
public class DatabaseConnection {
    final String password = "AasimarWart978&*" ; //If there are issues connecting, use actual password 
    final String userName = "root" ; 
    final String MYSQL_URL = "jdbc:mysql://localhost:3306/mdhsdb" ; 
    private Connection connection = null ; 
    
    private PreparedStatement addCustomer = null ; 
    private PreparedStatement getCustomer = null ; 
    
    private PreparedStatement addProduct = null ; 
    private PreparedStatement editProduct = null ; 
    private PreparedStatement deleteProduct = null ; 
    private PreparedStatement getAllProducts = null ; 
    
    private PreparedStatement getCustomerDeliverySchedule = null ; 
    private PreparedStatement getAllDeliverySchedules = null ; 
    private PreparedStatement getCustomerOrderProducts = null ; 
    
    public DatabaseConnection() { 
        try { 
            connection = DriverManager.getConnection(MYSQL_URL, userName, password) ; 
            
            // = connection.prepareStatement("") ; 
            //Below are all the customer queries 
            addCustomer = connection.prepareStatement("INSERT INTO customers (first_name,"
                    + "last_name, username, mobile, email, address, password_field) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?)") ; 
            getCustomer = connection.prepareStatement("SELECT * "
                    + "FROM customers WHERE username = ?") ; 
            
            //Below are all the product queries 
            //addProduct = connection.prepareStatement("") ; 
            //editProduct = connection.prepareStatement("") ; 
            //deleteProduct = connection.prepareStatement("") ; 
            getAllProducts = connection.prepareStatement("SELECT * "
                    + "FROM products") ; 
            
            //Delivery Schedule queries 
            getCustomerDeliverySchedule = connection.prepareStatement(
                    "SELECT * FROM delivery_schedule WHERE customer_id "
                            + "= ?") ; 
            //getAllDeliverySchedules = connection.prepareStatement("") ; 
            getCustomerOrderProducts = connection.prepareStatement( 
            "SELECT * FROM order_contents WHERE order_id = ?") ; 
        } catch (SQLException e){System.out.println("SQL Exception: " + e.getMessage());} 
    } 
    
    public String addCustomer(String dataBlock, String password) { 
        String[] split = dataBlock.split("::") ; 
        int resultSet = 0 ; 
        String finalMessage = null ; 
        try { 
            addCustomer.setString(1, split[0]) ; 
            addCustomer.setString(2, split[1]) ; 
            addCustomer.setString(3, split[2]) ; 
            addCustomer.setString(4, split[3]) ; 
            addCustomer.setString(5, split[4]) ; 
            addCustomer.setString(6, split[5]) ; 
            addCustomer.setString(7, password) ; 
            
            resultSet = addCustomer.executeUpdate() ; 
            
            System.out.println("AddCustomer Query Rows Affected: " + resultSet) ; 
            
            //0 is thrown if no rows are updated, so if successful, should result 
            //in a number that is not o
            if (resultSet != 0) { 
                finalMessage = "No issues" ; 
            } else { 
                finalMessage = "Invalid" ; 
            }
            
        } catch (SQLException sqlException) { 
            System.out.println("SQL Exception: " + sqlException.getMessage()) ; 
            finalMessage = "Invalid" ; 
        }
        
        System.out.println("AddCustomer method Final Message: " + finalMessage) ; 
        return finalMessage ; 
    }
    
    public Customer getCustomer(String username) { 
        //Assumption - 1 result as cannot have two usernames 
        ResultSet results = null ; 
        Customer customer = new Customer() ; 
        
        try{ 
            getCustomer.setString(1, username) ; 
            results = getCustomer.executeQuery() ; 
            
            while (results.next()) { 
                customer.setCustomerId(results.getInt("customer_id")) ; 
                customer.setFirstName(results.getString("first_name")) ; 
                customer.setLastName(results.getString("last_name")) ; 
                customer.setUserName(results.getString("username")) ; 
                customer.setPhoneNumber(results.getInt("mobile")) ; 
                customer.setEmailAddress(results.getString("email")) ; 
                customer.setDeliveryAddress(results.getString("address")) ; 
                customer.setPassword(results.getString("password_field")) ; 
                customer.setAdministrator(results.getInt("administrator")) ; 
            } 
        } catch (SQLException sqlException) {System.out.println("SQL Exception: " + sqlException.getMessage());
        } finally {try {results.close() ;}catch (SQLException sqlException){System.out.println("SQL Exception: " + sqlException.getMessage());}}
        return customer;
    }
    
    public ArrayList<OrderInformation> getCustomerDeliverySchedule(int customerId) { 
        ArrayList<OrderInformation> customerOrders = new ArrayList<>() ; 
        ResultSet resultSet = null ; 
        String[] products = null ; 
        
        try { 
            getCustomerDeliverySchedule.setInt(1, customerId) ; 
            resultSet = getCustomerDeliverySchedule.executeQuery() ; 
            
            //This checks if the returned results are null, if it is, send 
            //a new ArrayList<>() as null would cause issues, learned from 
            //experience 
            if (!resultSet.isBeforeFirst()) { 
                System.out.println("No entries within the order table") ; 
                return new ArrayList<>() ; 
            }
            
            while (resultSet.next()) { 
                customerOrders.add(new OrderInformation(
                    resultSet.getInt("delivery_id"), 
                    resultSet.getString("delivery_day"),
                    resultSet.getString("delivery_time"),
                    resultSet.getDouble("delivery_cost"),
                    resultSet.getInt("order_id")
                )) ; 
            }
            
            //Do something with order_id and join order_contents with yada yada
            //Then just add checks to get those names for the products 
            
        }catch (SQLException sqlException) { 
            System.out.println("SQL Exception: " + sqlException.getMessage()) ; 
        }
        return customerOrders ; 
    }
    
    
    
}
