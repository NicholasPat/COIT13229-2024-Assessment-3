package mdhsserver;

import dataclasses.*;
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
    
    //Queries regarding customers 
    private PreparedStatement addCustomer = null ; 
    private PreparedStatement getCustomer = null ; 
    private PreparedStatement getAllCustomers = null ; 
    
    //Queries regarding products 
    private PreparedStatement addProductFromFile = null ; 
    private PreparedStatement addProduct = null ; 
    private PreparedStatement editProduct = null ; 
    private PreparedStatement deleteProduct = null ; 
    private PreparedStatement getProduct = null ; 
    private PreparedStatement getAllProducts = null ; 
    private PreparedStatement decreaseProductQuantity = null ; 
    
    //Queries regarding deliveries 
    private PreparedStatement getCustomerDeliverySchedule = null ; 
    private PreparedStatement getAllDeliverySchedules = null ; 
    private PreparedStatement getCustomerOrderProducts = null ; 
    
    //Queries regarding Customer specific deliveries 
    private PreparedStatement addCustomerDeliverySchedule = null ; 
    private PreparedStatement addCustomerOrder = null ; 
    private PreparedStatement getCustomerOrderId = null ; 
    private PreparedStatement addCustomerOrderProduct = null ; 
    
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
            addProductFromFile = connection.prepareStatement(
                    "INSERT INTO pproducts (product_name, unit, quantity, "
                            + "price, ingredients) VALUES (?, ?, ?, ?, ?)") ; 
            addProduct = connection.prepareStatement("INSERT INTO products "
                    + "(product_name, unit, quantity, price, ingredients) "
                    + "VALUES (?, ?, ?, ?, ?)") ; 
            //editProduct = connection.prepareStatement("") ; 
            deleteProduct = connection.prepareStatement("DELETE FROM products "
                    + "WHERE product_id = ?") ; 
            getAllProducts = connection.prepareStatement("SELECT * "
                    + "FROM products") ; 
            decreaseProductQuantity = connection.prepareStatement(
                "") ; 
            
            //Delivery Schedule queries 
            getCustomerDeliverySchedule = connection.prepareStatement(
                    "SELECT * FROM delivery_schedule WHERE customer_id "
                            + "= ?") ; 
            getAllDeliverySchedules = connection.prepareStatement("SELECT * "
                    + "FROM delivery_schedule") ; 
            getCustomerOrderProducts = connection.prepareStatement( 
            "SELECT * FROM order_contents WHERE order_id = ?") ; 
            
            addCustomerOrder = connection.prepareStatement("INSERT INTO orders "
                    + "(customer_id) VALUES (?)") ; 
            getCustomerOrderId = connection.prepareStatement("SELECT order_id"
                    + "FROM orders WHERE customer_id = ?") ; 
            addCustomerOrderProduct = connection.prepareStatement(
                    "INSERT INTO order_contents (order_id, product_id, "
                            + "quantity) VALUES (?, ?, ?)") ; 
            //addCustomerDeliverySchedule = connection.prepareStatement(
               // "") ; 
        } catch (SQLException e){System.out.println("SQL Exception: " + e.getMessage());} 
    } 
    
    public String addCustomer(String dataBlock, String password) { 
        String[] split = dataBlock.split("::") ; 
        int resultSet ; 
        String finalMessage ; 
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
    
    /** 
     * 
     * @param username
     * @return 
     */
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
    
    /** 
     * 
     * @param customerId
     * @return 
     */
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
    
    /** 
     * Gets all products. Used for initial startup AND when called for by admin 
     * or the client to get the list 
     * @return 
     */
    public ArrayList<Product> getAllProducts() { 
        ArrayList<Product> products = null ; 
        ResultSet resultSet ; 
        
        try { 
            resultSet = getAllProducts.executeQuery() ; 
            
            //If the resultSet is null, or no results 
            if (!resultSet.isBeforeFirst()) { 
                System.out.println("No entries in products table") ; 
                return new ArrayList<>() ; 
            }
            
            while (resultSet.next()) { 
                products.add(new Product(
                        resultSet.getInt("product_id"),
                        resultSet.getString("product_name"), 
                        resultSet.getString("unit"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price"),
                        resultSet.getString("Ingredients")
                )) ; 
            }
        } catch (SQLException e) { 
            System.out.println("Error reading all products \nMessage provided: " + e.getMessage()) ; 
        }
        
        
        return products ; 
    }
    
    /** 
     * 
     * @param products 
     */
    public void writeProductsFromFile(ArrayList<Product> products) { 
        int result ; 
        
        for (int i = 0; i > products.size(); i++) { 
            String productName =    products.get(i).getProductName() ; 
            String unit =           products.get(i).getUnit() ; 
            int quantity =          products.get(i).getTotalQuantity() ; 
            double price =          products.get(i).getPrice() ; 
            String ingredients =    products.get(i).getIngredients() ; 
            
            try { 
                addProductFromFile.setString(1, productName) ; 
                addProductFromFile.setString(2, unit) ; 
                addProductFromFile.setInt(3, quantity) ; 
                addProductFromFile.setDouble(4, price) ; 
                addProductFromFile.setString(5, ingredients) ; 

                result = addProductFromFile.executeUpdate() ; 
                System.out.println("Added product with name: " + products.get(0).getProductName()) ; 

            } catch (SQLException e) { 
                System.out.println("Something went wrong with executing update to add "
                        + "products from file\nMessage: " + e.getMessage()) ;
            }
        }
        
    }
    
    /** 
     * 
     * @param name
     * @param productId
     * @return 
     */
    public Product getSingularProduct(String name, int productId) { 
        /* 
        Baasing check off the name. Make sure names are input correctly first 
        also maybe implement error handling to ensure no double ups 
        
        With error handling: 
        Get all products, invoke that method. Then get the names and check each 
            time, have boolean that upon "result.equals(name)" then change to 
            true. If true, then send back to server string stating "name of product
            already exists" which will send back to the client as an error message. 
            Then if flase, get the whole product and return it, since multiple 
            applications 
        Then that's it for this method. ID is also given as it will be used for 
            another method. 0 if using name, null string is using ID 
        */
        return new Product() ; 
    }
    
    /** 
     * 
     * @param productId 
     * @param name 
     * @param unit 
     * @param quantity 
     * @param price 
     * @param ingredients 
     */
    public void editProduct(int productId, String name, String unit, int quantity,
            double price, String ingredients) { 
        /* 
        Simply just editing, nothing too fancy. Use the product id gotten from 
            getSingularProduct(name, ID) method (Probs using ID since would have 
            gotten them all) 
        Then pass through actual changes and then commit 
        */
    }
    
    /** 
     * 
     * @param productId 
     */
    public void deleteProduct(int productId) { 
        /* 
        Just good old delete. Won't bother with product id reshuffling, would 
        mess things up. Don't implement yet until know we need to since would create
        a headache upon needing error handlng 
        */
    }
    
    /** 
     * Adding customer order. Will take: 
     * CustomerId, array of ProductId + one for quantity of each, Date, Time, Cost
     * @param customerId    
     * @param productId     
     * @param quantity      
     * @param date          
     * @param time          
     * @param cost          
     */
    public void addCustomerOrder(int customerId, int[] productId, int quantity, 
            String date, String time, double cost) { 
        ResultSet resultSet = null ; 
        int result = 0 ; 
        
        try { 
            addCustomerOrder.setInt(1, customerId) ; 
        }catch (SQLException e) { 
                System.out.println("Something went wrong with executing update to add "
                        + "customer order\nMessage: " + e.getMessage()) ;
            }
        
        /* 
        //INT CustomerId
            addCustomerOrder = connection.prepareStatement("INSERT INTO orders "
                    + "(customer_id) VALUES (?)") ; 
        
        //INT OrderID 
            getCustomerOrderId = connection.prepareStatement("SELECT order_id"
                    + "FROM orders WHERE customer_id = ?") ; 
        //INT OrderID, INT ProductID, INT Quantity
            addCustomerOrderProduct = connection.prepareStatement(
                    "INSERT INTO order_contents (order_id, product_id, "
                            + "quantity) VALUES (?, ?, ?)") ; 
        */
        
        /* 
        Add the order first since it just needs the customerID 
        Afterwards take the arrays of productIds and quantities and then 
            add to the orderId that will be gotten via query (would need to go 
            through the list btw and make sure that the latest order is gotten 
            if multiple are returned) - This will then make the attached products 
        After that take the CustomerId, OrderId, day, time, cost and then insert 
            into the database 
        Send back all good confirmation to the server, which will send it back 
            to the client which will prompt an info message, if error, then error
            message 
        
        */
    }
    
}
