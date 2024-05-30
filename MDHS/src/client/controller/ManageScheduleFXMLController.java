
package client.controller;

import client.MDHSClient;
import client.Session;
import common.UserInputException;
import common.model.DeliverySchedule;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author lucht
 */
public class ManageScheduleFXMLController implements Initializable, SceneController {

    @FXML
    private AnchorPane contentAnchorPane;
    @FXML
    private Button dashboardButton;
    @FXML
    private TextField postcodeTextField;
    @FXML
    private TextField costTextField;
    @FXML
    private ChoiceBox<String> deliveryDayChoiceBox;
    @FXML
    private Button previousIndexButton;
    @FXML
    private Button nextIndexButton;
    @FXML
    private TextField currentIndexTextField;
    @FXML
    private TextField totalIndexTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button newButton;

    private Session session; 
    
    private DeliverySchedule currentSchedule;
    private List<DeliverySchedule> deliverySchedules;
    private int currentScheduleIndex;
    private int numberOfSchedules;
    
    @Override
    public void handleSceneChange() {
        session = Session.getSession();
        clear();
        loadSchedules();
        
        deliveryDayChoiceBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        deliveryDayChoiceBox.setValue("Monday");
        
        populateForm();
        
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

    @FXML
    private void previousIndexButtonHandler(ActionEvent event) {
        currentScheduleIndex--;
        if (currentScheduleIndex < 0 && numberOfSchedules >= 0)
            currentScheduleIndex = numberOfSchedules-1; // if index went below 0 cycle back to end of list
        if (!deliverySchedules.isEmpty()) {
            currentSchedule = deliverySchedules.get(currentScheduleIndex);
            populateForm();
        }
    }

    @FXML
    private void nextIndexButtonHandler(ActionEvent event) {
        currentScheduleIndex++;
        if ( currentScheduleIndex >= numberOfSchedules ) // if index went above total number, then cycle back to first entry
            currentScheduleIndex = 0;
        if (!deliverySchedules.isEmpty()) {
            currentSchedule = deliverySchedules.get(currentScheduleIndex);
            populateForm();
        }
    }

    @FXML
    private void addButtonHandler(ActionEvent event) {
        recordDeliverySchedule();
        try {
            session.objOut.writeObject("RecordSchedule");
            session.objOut.writeObject(currentSchedule);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while adding schedule: " + ex.getMessage());
        }

        clear();
        loadSchedules();
        populateForm();  
    }

    @FXML
    private void updateButtonHandler(ActionEvent event) {
        recordDeliverySchedule();
        
        try {
            session.objOut.writeObject("RecordSchedule");
            session.objOut.writeObject(currentSchedule);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while updating schedule: " + ex.getMessage());
        }
        
        clear();
        loadSchedules();
        populateForm();  
    }

    @FXML
    private void deleteButtonHandler(ActionEvent event) {
        recordDeliverySchedule();
        
        try {
            session.objOut.writeObject("DeleteSchedule");
            session.objOut.writeObject(currentSchedule);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while deleting schedule: " + ex.getMessage());
        }
        
        clear();
        loadSchedules();
        populateForm();  
    }


    @FXML
    private void newButtonHandler(ActionEvent event) {
        clear();
        loadSchedules();
        postcodeTextField.setEditable(true);
        try {
            numberOfSchedules++;
            currentScheduleIndex = numberOfSchedules-1;
            currentSchedule = new DeliverySchedule();
            populateForm();
        } catch (UserInputException ie) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ie.getMessage());   
            alert.showAndWait();
        } 
    }
    
    private void loadSchedules() {
        try {
            session.objOut.writeObject("FullDeliverySchedule");
            deliverySchedules = (ArrayList<DeliverySchedule>) session.objIn.readObject();
            
            numberOfSchedules = deliverySchedules.size();
            if (numberOfSchedules > 0) {
                currentScheduleIndex = 0;
                currentSchedule = deliverySchedules.get(currentScheduleIndex);
            } else {
                currentSchedule = new DeliverySchedule();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception while loading Delivery Schedule: " + ex.getMessage());
        }
        
    }
    
    private void recordDeliverySchedule() {
        double cost = Double.parseDouble(costTextField.getText());
        int postcode = Integer.parseInt(postcodeTextField.getText());
        String deliveryDay = deliveryDayChoiceBox.getValue();
        currentSchedule = new DeliverySchedule(postcode, deliveryDay, cost);
    }
    
    private void populateForm() {
        deliveryDayChoiceBox.setValue(currentSchedule.getDeliveryDay());
        postcodeTextField.setText(currentSchedule.getPostcode()+"");
        costTextField.setText(currentSchedule.getDeliveryCost()+"");
        currentIndexTextField.setText(currentScheduleIndex+1+"");
        totalIndexTextField.setText(numberOfSchedules+"");
    }
    
    private void clear() {
        currentSchedule = new DeliverySchedule();
        deliverySchedules = new ArrayList<>();
        currentScheduleIndex = 0;
        numberOfSchedules = 1;
        
        deliveryDayChoiceBox.setValue(null);
        postcodeTextField.clear();
        costTextField.clear();
        currentIndexTextField.clear();
        totalIndexTextField.clear();
        postcodeTextField.setEditable(false);
    }
}
