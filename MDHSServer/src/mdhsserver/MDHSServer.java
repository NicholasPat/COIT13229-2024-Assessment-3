package mdhsserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author linke
 */
public class MDHSServer {
    
    /**
     * 
     * @param args the command line arguments
     * @throws java.security.NoSuchAlgorithmException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        int threadCount = 1 ; 
        int serverPort = 6464 ; 
        PasswordDetails passwordDetails = new PasswordDetails() ; 
        PublicKey publicK = passwordDetails.getPublicKey() ; 
        PrivateKey privateK = passwordDetails.getPrivateKey() ; 
        
        try { 
            ServerSocket listenSocket = new ServerSocket(serverPort) ; 
            
            while (true) { 
                Socket clientSocket = listenSocket.accept() ; 
                MainConnection c = new MainConnection(clientSocket, 
                        threadCount++, publicK, privateK) ; 
                c.start() ; 
            }
            
        }catch(IOException e){System.out.println("Listen :"+e.getMessage());}
    }
    
}

/** 
 * 
 * @author linke
 */
class MainConnection extends Thread { 
    DataInputStream dataIn ; 
    DataOutputStream dataOut ; 
    ObjectInputStream objIn ; 
    ObjectOutputStream objOut ; 
    Socket clientSocket ; 
    int threadNumber ; 
    String hostName = "localhost" ; 
    PublicKey publicKey ; 
    PrivateKey privateKey ; 
    
    /** 
     * 
     * @param aSocket
     * @param count 
     * 
     * @throws IOException 
     */
    public MainConnection(Socket aSocket, int count, PublicKey key, PrivateKey privKey) { 
        threadNumber = count ; 
        clientSocket = aSocket ; 
        publicKey = key ; 
        privateKey = privKey ; 
        try { 
            dataIn = new DataInputStream(clientSocket.getInputStream()) ; 
            dataOut = new DataOutputStream(clientSocket.getOutputStream()) ; 
            objIn = new ObjectInputStream(clientSocket.getInputStream()) ; 
            objOut = new ObjectOutputStream(clientSocket.getOutputStream()) ; 
        }catch(IOException e){System.out.println("Connection: "+e.getMessage());}
    }
    
    /** 
     * 
     */
    @Override 
    public void run() { 
        String data = null ; 
        try { 
            data = dataIn.readUTF() ;
        }catch(IOException e){System.out.println("Connection: "+e.getMessage());}
        
        if (data.equals("Password Check")) { 
            try {
                dataOut.writeUTF("All good");
            }catch(IOException e){System.out.println("Connection: "+e.getMessage());}
        }
    }
}