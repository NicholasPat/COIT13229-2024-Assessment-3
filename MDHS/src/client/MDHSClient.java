
package client;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import client.controller.*;

/**
 *
 * @author lucht
 */
public class MDHSClient extends Application {
    private static Stage stage;
    
    // Define enum for scenes
    public enum SceneType {
        DASHBOARD("/client/view/DashboardFXML.fxml"),
        LOGIN("/client/view/LoginFXML.fxml"),
        REGISTER("/client/view/RegisterFXML.fxml"),
        VIEW_SCHEDULE("/client/view/ViewScheduleFXML.fxml"),
        VIEW_PRODUCTS("/client/view/ViewProductFXML.fxml"),
        PLACE_ORDER("/client/view/PlaceOrderFXML.fxml"),
        MANAGE_PRODUCTS("/client/view/ManageProductFXML.fxml"),
        MANAGE_SCHEDULE("/client/view/ManageScheduleFXML.fxml"),
        VIEW_CUSTOMERS("/client/view/ViewCustomerFXML.fxml"),
        VIEW_ORDERS("/client/view/ViewOrderFXML.fxml");

        private final String resourcePath;

        SceneType(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        public String getResourcePath() {
            return resourcePath;
        }
    }
    
    // Define FXML scenes array
    private static Scene[] scenes = new Scene[SceneType.values().length];

    // Define controller array
    private static Object[] controllers = new Object[SceneType.values().length];

    @Override
    public void start(Stage stage) throws Exception {
        for (SceneType sceneType : SceneType.values()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneType.getResourcePath()));
            Parent root = loader.load();
            scenes[sceneType.ordinal()] = new Scene(root);
            controllers[sceneType.ordinal()] = loader.getController();
        }
        
        this.stage = stage;
        //Sets the current scene and makes the stage visible
        this.stage.setScene(scenes[SceneType.DASHBOARD.ordinal()]);
        this.stage.show();
    }

    /**
     * Main Method
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Session session = Session.getSession(); // initialise session (server connection)
        launch(args);
    }
    
    /**
     * Change between scenes
     * @param sc SceneType enum of target scene (SceneType.DASHBOARD)
     */
    public static void changeScene(SceneType sc) {
        switch(sc) {
            case DASHBOARD:
                stage.setScene(scenes[SceneType.DASHBOARD.ordinal()]);
                ((DashboardFXMLController) controllers[SceneType.DASHBOARD.ordinal()]).handleSceneChange();
                break;
            case LOGIN:
                stage.setScene(scenes[SceneType.LOGIN.ordinal()]);
                ((LoginFXMLController) controllers[SceneType.LOGIN.ordinal()]).handleSceneChange();
                break;
            case REGISTER:
                stage.setScene(scenes[SceneType.REGISTER.ordinal()]);
                ((RegisterFXMLController) controllers[SceneType.REGISTER.ordinal()]).handleSceneChange();
                break;
            case VIEW_SCHEDULE:
                stage.setScene(scenes[SceneType.VIEW_SCHEDULE.ordinal()]);
                ((ViewScheduleFXMLController) controllers[SceneType.VIEW_SCHEDULE.ordinal()]).handleSceneChange();
                break;
            case VIEW_PRODUCTS:
                stage.setScene(scenes[SceneType.VIEW_PRODUCTS.ordinal()]);
                ((ViewProductFXMLController) controllers[SceneType.VIEW_PRODUCTS.ordinal()]).handleSceneChange();
                break;
            case PLACE_ORDER:
                stage.setScene(scenes[SceneType.PLACE_ORDER.ordinal()]);
                ((PlaceOrderFXMLController) controllers[SceneType.PLACE_ORDER.ordinal()]).handleSceneChange();
                break;
            case MANAGE_PRODUCTS:
                stage.setScene(scenes[SceneType.MANAGE_PRODUCTS.ordinal()]);
                ((ManageProductFXMLController) controllers[SceneType.MANAGE_PRODUCTS.ordinal()]).handleSceneChange();
                break;
            case MANAGE_SCHEDULE:
                stage.setScene(scenes[SceneType.MANAGE_SCHEDULE.ordinal()]);
                ((ManageScheduleFXMLController) controllers[SceneType.MANAGE_SCHEDULE.ordinal()]).handleSceneChange();
                break;
            case VIEW_CUSTOMERS:
                stage.setScene(scenes[SceneType.VIEW_CUSTOMERS.ordinal()]);
                ((ViewCustomerFXMLController) controllers[SceneType.VIEW_CUSTOMERS.ordinal()]).handleSceneChange();
                break;
            case VIEW_ORDERS:
                stage.setScene(scenes[SceneType.VIEW_ORDERS.ordinal()]);
                ((ViewOrderFXMLController) controllers[SceneType.VIEW_ORDERS.ordinal()]).handleSceneChange();
                break;
            default:
                // Handle default case if needed
                break;
        }
    }

}
