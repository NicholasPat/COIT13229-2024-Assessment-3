
package client.controller;

import client.MDHSClient;
import client.Session;
import common.model.DeliverySchedule;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
 */
public class ViewScheduleFXMLController implements Initializable, SceneController {

    @FXML
    private AnchorPane contentAnchorPane;
    @FXML
    private Button dashboardButton;
    @FXML
    private TextArea mondayTextArea;
    @FXML
    private TextArea tuesdayTextArea;
    @FXML
    private TextArea wednesdayTextArea;
    @FXML
    private TextArea fridayTextArea;
    @FXML
    private TextArea thursdayTextArea;
    
    /** 
     * Clear the fields and load into the program the schedules 
     */
    @Override
    public void handleSceneChange() {
        clear();
        loadSchedule();
    }
    /**
     * Initializes the controller class.
     * 
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    /** 
     * Return to dashboard 
     * 
     * @param event 
     */
    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    /** 
     * Loads in the object with schedule from the server. Error handles for if there 
     * is nothing returned to output error and return to the Dashboard 
     */
    private void loadSchedule() {
        Session session = Session.getSession();
        try {
            session.objOut.writeObject("FullDeliverySchedule");
            ArrayList<DeliverySchedule> deliverySchedules = (ArrayList<DeliverySchedule>) session.objIn.readObject();
            
            if (deliverySchedules.isEmpty()) { 
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No entries returned from server, returning to dashboard"); 
                alert.showAndWait(); 
                MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
            }
            
            deliverySchedules.forEach(this::appendToTextArea);
            
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Exception while loading Delivery Schedule: " + ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                    "Encountered an error while loading information: " + ex.getMessage() + 
                            ".\nReturning to main dashboard"); 
            alert.showAndWait(); 
            MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
        }
    }
    /**
     * Takes each schedule object and appends each entry to the relevant text area
     * 
     * @param schedule 
     */
    private void appendToTextArea(DeliverySchedule schedule) {
        String entry = schedule.getPostcode() + "   ($" + schedule.getDeliveryCost() + ")\n";
        switch (schedule.getDeliveryDay().toLowerCase()) {
            case "monday" -> mondayTextArea.appendText(entry);
            case "tuesday" -> tuesdayTextArea.appendText(entry);
            case "wednesday" -> wednesdayTextArea.appendText(entry);
            case "thursday" -> thursdayTextArea.appendText(entry);
            case "friday" -> fridayTextArea.appendText(entry);
        }
    }
    
    /** 
     * Clears the table entries so new text can be appended
     */
    private void clear() {
        mondayTextArea.clear();
        tuesdayTextArea.clear();
        wednesdayTextArea.clear();
        thursdayTextArea.clear();
        fridayTextArea.clear();
    }
}
