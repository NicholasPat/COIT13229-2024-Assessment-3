
package client.controller;

import client.MDHSClient;
import client.Session;
import common.model.DeliverySchedule;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author lucht
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

    @Override
    public void handleSceneChange() {
        clear();
        loadSchedule();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    private void loadSchedule() {
        Session session = Session.getSession();
        try {
            session.objOut.writeObject("FullDeliverySchedule");

            ArrayList<DeliverySchedule> deliverySchedules = (ArrayList<DeliverySchedule>) session.objIn.readObject();
                    
            deliverySchedules.forEach(this::appendToTextArea);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while loading Delivery Schedule: " + ex.getMessage());
        }
    }
    private void appendToTextArea(DeliverySchedule schedule) {
        String entry = schedule.getPostcode() + "   ($" + schedule.getDeliveryCost() + ")\n";

        switch (schedule.getDeliveryDay().toLowerCase()) {
            case "monday":
                mondayTextArea.appendText(entry);
                break;
            case "tuesday":
                tuesdayTextArea.appendText(entry);
                break;
            case "wednesday":
                wednesdayTextArea.appendText(entry);
                break;
            case "thursday":
                thursdayTextArea.appendText(entry);
                break;
            case "friday":
                fridayTextArea.appendText(entry);
                break;
        }
    }
    
    private void clear() {
        mondayTextArea.clear();
        tuesdayTextArea.clear();
        wednesdayTextArea.clear();
        thursdayTextArea.clear();
        fridayTextArea.clear();
    }
}
