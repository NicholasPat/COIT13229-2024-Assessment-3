
package server;

import common.Authenticator;
import java.net.*;
import java.io.*;

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
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                ConnectionThread c = new ConnectionThread(clientSocket, database);
            }
        } catch(IOException e){ System.out.println("Socket Error: "+e.getMessage());}
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
                
                } else if (option.equalsIgnoreCase("Login")){ 
                    
                } else if (option.equalsIgnoreCase("Register")){ 
                    
                } else if (option.equalsIgnoreCase("AllOrders")){ 
                    
                } else if (option.equalsIgnoreCase("AllCustomers")){ 
                    
                } else if (option.equalsIgnoreCase("AllProducts")){ 
                    
                } else if (option.equalsIgnoreCase("DeliverySchedule")){ 
                    
                } else if (option.equalsIgnoreCase("Placeorder")){ 
                    
                } else if (option.equalsIgnoreCase("NewProduct")){ 
                    
                } else if (option.equalsIgnoreCase("EditProduct")){ 
                    
                } else if (option.equalsIgnoreCase("RemoveProduct")){ 
                    
                } else if (option.equalsIgnoreCase("NewSchedule")){ 
                    
                } else if (option.equalsIgnoreCase("EditSchedule")){ 
                    
                } else if (option.equalsIgnoreCase("RemoveSchedule")){ 
                    
                } 
            }
        } catch (IOException e){ System.out.println(/*close failed*/);
        } catch(ClassNotFoundException ex){System.out.println("CNF Error:"+ex.getMessage());
        } finally{ try {objIn.close();clientSocket.close();}catch (IOException e){/*close failed*/}}
    }
}