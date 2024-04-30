package mdhsserver;

import java.sql.*; 

/**
 * Note: SQL and Java implementation was learned during COIT12200 Software Design and 
 * Development. A lot of the code used was from there and was utilised 
 * @author linke
 */
public class DatabaseConnection {
    final String password = "AasimarWart978&*" ; //If there are issues connecting, use actual password 
    final String userName = "root" ; 
    final String MYSQL_URL = "jdbc:mysql://localhost:3306/ewmsdb" ; 
    private Connection connection = null ; 
    
    private PreparedStatement addCustomer = null ; 
    
    private PreparedStatement addProduct = null ; 
    private PreparedStatement editProduct = null ; 
    private PreparedStatement deleteProduct = null ; 
    private PreparedStatement getAllProducts = null ; 
    
    public DatabaseConnection() { 
        try { 
            connection = DriverManager.getConnection(MYSQL_URL, userName, password) ; 
        } catch (SQLException e){System.out.println("SQL Exception: " + e.getMessage());} 
    }
}
