package com.mycompany.mdhs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * 
 * @author linke
 */
public class LoginController implements Initializable {

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField userNameField;
    
    /*Other declarations - not related to FXML*/
    private Socket s = null ; 
    private DataOutputStream out = null ; 
    private DataInputStream in = null ; 
    private final String hostName = "localHost" ; 
    private final int serverPort = 6464 ; 
    
    /** 
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    /** 
     * 
     * @param event 
     */
    @FXML
    private void ClicktoRegister(ActionEvent event) {
        try {
           App.setRoot("Registration") ;
        } catch (IOException e){
           System.out.println(e) ; 
        }
    }
    /** 
     * 
     * @param event 
     */
    @FXML
    private void clickToSignIn(ActionEvent event) {
        String userName = userNameField.getText() ; 
        String password = passwordField.getText() ; //NEEDS SECURITY - but for now won't bother 
        try { 
            s = new Socket(hostName, serverPort) ; 
            out = new DataOutputStream(s.getOutputStream()) ; 
            in = new DataInputStream(s.getInputStream()) ; 
            out.writeUTF("PasswordCheck") ; 
            
            String message = in.readUTF() ; 
            if (message.equals("all good")) { 
                out.writeUTF(userName + "::" + password) ; 
            }
        }catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
        }catch (EOFException e){System.out.println("EOF:"+e.getMessage());
        }catch (IOException e){System.out.println("readline:"+e.getMessage());
        }finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
        
    }
}
