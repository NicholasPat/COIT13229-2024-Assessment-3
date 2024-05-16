
package server;

import java.sql.*;
import javafx.scene.control.Alert;

/**
 *
 * @author lucht
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost/mdhsDB";
    private static final String USERNAME = "root";
    private static final String PASSWORD = System.getenv("DBMSpassword"); //your own password to Root account of MySQL
    private Connection connection = null;
    
    public DatabaseConnection () {
        try {
            // attempt to establish dmbs connection
            connection = DriverManager.getConnection( URL,USERNAME,PASSWORD );
        } catch (SQLException sqlException ) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database connection failure. \nPlease check your dbms password.");   
            alert.showAndWait();
            sqlException.printStackTrace();
            System.exit( 1 ); // exit if couldnt connect to db
        }
    }
    
    // Database Queries
}
