
package MainPackage;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author linke
 */
public class MdhsClient extends Application {

    private static Scene scene; 

    /** 
     * 
     * @param stage
     * @throws IOException 
     */
    @Override
    public void start(Stage stage) throws IOException {
        //Parent root = FXMLLoader.load(getClass().getResource("/MainPackage/fxml_resources/Login.fxml"));
        scene = new Scene(loadFXML("Login"));
        stage.setScene(scene);
        stage.show();
    }
    
    /** 
     * 
     * @param fxml
     * @throws IOException 
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    
    /** 
     * Technically not needed! 
     * 
     * @param fxml
     * @return
     * @throws IOException 
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MdhsClient.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /** 
     * 
     * @param args 
     */
    public static void main(String[] args) {
        launch();
    }
    
    
    
}
