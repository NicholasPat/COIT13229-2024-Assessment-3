
package MainPackage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author linke
 */
public class LoginController implements Initializable {

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void clickToLogin(ActionEvent event) {
        
    }

    @FXML
    private void clickToRegister(ActionEvent event) {
        try {
           MdhsClient.setRoot("Registration") ;
        } catch (IOException e){
           System.out.println(e) ; 
        }
    }
    
}
