package client.controller;

import client.MDHSClient;
import client.Session;
import common.UserInputException;
import common.Utility;
import common.model.DeliverySchedule;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Brodie Lucht 
 * @author Nicholas Paterno 
 * @author Christopher Cox 
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
    private int newScheduleFlag;
    private final String inputMismatchTitle = "Input mismatch occurred!"; 
    
    /**
     * Upon changing the scene to the current, perform the following steps 
     */
    @Override
    public void handleSceneChange() {
        //Set the current session, clear the fields, and then load schedules. 
        session = Session.getSession();
        clear();
        loadSchedules();
        
        deliveryDayChoiceBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        deliveryDayChoiceBox.setValue("Monday");
        
        populateForm();
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
     * Returns to the Dashboard. 
     * 
     * @param event 
     */
    @FXML
    private void dashboardButtonHandler(ActionEvent event) {
        MDHSClient.changeScene(MDHSClient.SceneType.DASHBOARD);
    }
    
    /**
     * Cycles back through the Schedule list. If at the bottom of the list, rolls 
     * over to the top of the list. 
     * 
     * @param event 
     */
    @FXML
    private void previousIndexButtonHandler(ActionEvent event) {
        //Error if there are no more than 1 entries (either null entry or it's 
        //a single result from the server 
        if (deliverySchedules.size() == 1) { 
            smallAlert(); 
            return;
        }
        
        currentScheduleIndex--;
        if (currentScheduleIndex < 0 && numberOfSchedules >= 0)
            currentScheduleIndex = numberOfSchedules-1; // if index went below 0 cycle back to end of list
        if (!deliverySchedules.isEmpty()) {
            currentSchedule = deliverySchedules.get(currentScheduleIndex);
            populateForm();
        }
    }
    
    /**
     * Cycle through the index and reach the next one. If no more, then do nothing. 
     * 
     * @param event 
     */
    @FXML
    private void nextIndexButtonHandler(ActionEvent event) {
        //Error if there are no more than 1 entries (either null entry or it's 
        //a single result from the server 
        if (deliverySchedules.size() == 1) { 
            smallAlert(); 
            return;
        }
        
        currentScheduleIndex++;
        if ( currentScheduleIndex >= numberOfSchedules ) // if index went above total number, then cycle back to first entry
            currentScheduleIndex = 0;
        if (!deliverySchedules.isEmpty()) {
            currentSchedule = deliverySchedules.get(currentScheduleIndex);
            populateForm();
        }
    }
    
    /**
     * Adds edited Schedule to the DB. 
     * 
     * @param event 
     */
    @FXML
    private void addButtonHandler(ActionEvent event) {
        try {
            //Record information for sending to the server, then ping and send to server. 
            recordDeliverySchedule();
            session.objOut.writeObject("RecordSchedule");
            session.objOut.writeObject(currentSchedule);
            String message = (String) session.objIn.readObject(); 
            
            if (message.equalsIgnoreCase("RecordScheduleSuccess")) { 
                String outcome = "Schedule was successfully added!"; 
                exceptionOutput("Successful addition of Schedule", outcome, 2); 
                
            } else { 
                String outcome = "Schedule was unsuccessfully added";
                exceptionOutput("Unsuccessful addition of Schedule!", outcome, 1); 
            }
            
        } catch (UserInputException e) { 
            String message = "Input mismatch ocurred when adding the Delivery Schedule! " + e.getMessage();
            exceptionOutput(inputMismatchTitle, message, 1); 
            return; 
            
        } catch (IOException | ClassNotFoundException ex) {
            String message = "Exception occurred while adding the schedule: " +ex.getMessage();
            exceptionOutput("General Exception occurred!", message, 1); 
            return; 
        }
        
        //Clear, load, populate. 
        clear();
        loadSchedules();
        populateForm();  
    }
    
    /**
     * Handles the updating of the Schedule. However Add and Edit use the same 
     * 'RecordSchedule' server tag, so edits can happen when adding but doesn't 
     * matter too much really, just some redundancy. <p>
     * Similar code to 'add'. 
     * 
     * @param event 
     */
    @FXML
    private void updateButtonHandler(ActionEvent event) {
        try {
            recordDeliverySchedule(); 
            session.objOut.writeObject("RecordSchedule");
            session.objOut.writeObject(currentSchedule);
            String message = (String) session.objIn.readObject();
            
            if (message.equalsIgnoreCase("RecordScheduleSuccess")) { 
                String outcome = "Schedule was successfully updated!"; 
                String title = "Successful update of the Schedule!"; 
                exceptionOutput(title, outcome, 2); 
                
            } else { 
                String outcome = "Schedule was unsuccessfully edited"; 
                String title = "Unsuccessful edit of the schedule!"; 
                exceptionOutput(title, outcome, 1); 
            }
            
        } catch (UserInputException e) { 
            String message = "Input mismatched ocurred when adding the Delivery Schedule! " + e.getMessage();
            exceptionOutput(inputMismatchTitle, message, 1); 
            
        } catch (IOException | ClassNotFoundException ex) {
            String message = "Exception occurred while updating the schedule: " +ex.getMessage();
            String title = "General Exception occurred in load Schedule";
            exceptionOutput(title, message, 1); 
        }
        
        clear();
        loadSchedules();
        populateForm();  
    }
    
    /**
     * Takes current information and deletes the associated entry. Takes the whole 
     * object for ease of coding. <p>
     * Similar code to 'add'. 
     * 
     * @param event 
     */
    @FXML
    private void deleteButtonHandler(ActionEvent event) {
        try {
            recordDeliverySchedule();
            session.objOut.writeObject("DeleteSchedule");
            session.objOut.writeObject(currentSchedule);
            String message = (String) session.objIn.readObject(); 
            
            if (message.equalsIgnoreCase("DeleteScheduleSuccess")) { 
                String outcome = "Schedule was successfully deleted!"; 
                String title = "Successful deletion of the Schedule!"; 
                exceptionOutput(title, outcome, 2); 
                
            } else { 
                String outcome = "Schedule was unsuccessfully deleted!"; 
                String title = "Unsuccessful deletion of the schedule!"; 
                exceptionOutput(title, outcome, 1); 
            }
            
        } catch (UserInputException e) { 
            String message = "Input mismatched ocurred when adding the Delivery Schedule! " + e.getMessage();
            exceptionOutput(inputMismatchTitle, message, 1); 
            
        } catch (IOException | ClassNotFoundException ex) {
            String message = "Exception has occured while deleting the schedule: " + ex.getMessage();
            String title = "General Exception occurred while deleting Schedule";
            exceptionOutput(title, message, 1); 
        }
        
        //Reset the scene 
        clear();
        loadSchedules();
        populateForm();  
    }
    
    /**
     * Sets text fields to null, and also sets a flag to make it so Update and 
     * Delete methods won't continue, ensuring no messiness with that. 
     * 
     * @param event 
     */
    @FXML
    private void newButtonHandler(ActionEvent event) {
        //Set the flag to 1 (active), and then clear the fields 
        newScheduleFlag = 1; 
        clear();
    }
    
    /**
     * Loads the current list of schedules from the server, ping then receive 
     */
    private void loadSchedules() {
        try {
            //Set flag to 0, get List from server. 
            newScheduleFlag = 0; 
            session.objOut.writeObject("FullDeliverySchedule");
            deliverySchedules = (ArrayList<DeliverySchedule>) session.objIn.readObject();
            
            //Set total number and then set current numbers and what not. 
            numberOfSchedules = deliverySchedules.size();
            if (numberOfSchedules > 0) {
                currentScheduleIndex = 0;
                currentSchedule = deliverySchedules.get(currentScheduleIndex);
            } else {
                currentSchedule = new DeliverySchedule();
                currentScheduleIndex = -1; //Should resolve as 0 then on input 
                numberOfSchedules = 0; //Redundancy, ensures is 0 
                Utility.alertGenerator("No schedules booked", "No schedules have been booked!", 
                        "No schedules have been booked, please book some", 2); 
            }
        } catch (IOException | ClassNotFoundException ex) {
            String message = "General Exception occurred while loading Delivery Schedule! " + ex.getMessage();
            String title = "General Exception occurred in load Schedule";
            exceptionOutput(title, message, 1); 
        }
    }
    
    /**
     * Takes current fields and creates an object with there information. Performs 
     * error handling. 
     * 
     * @throws UserInputException   Thrown if the user makes a misinput 
     */
    private void recordDeliverySchedule() throws UserInputException {
        //Set all variables as Strings, makes error handling much easier. 
        String deliveryDay = deliveryDayChoiceBox.getValue().toLowerCase();
        String cost = costTextField.getText().trim(); 
        String postcode = postcodeTextField.getText().trim(); 
        
        /*
        Noticed that sometimes it gets parsed as null, so force as Monday if the case 
        I think this is because setting as New sometimes breaks it, manual change will 
        make it the actual value
        */
        if (deliveryDay == null) { 
            deliveryDay = "Monday"; 
        }
        
        //Set current Schedule to what was input. Only gets to this point if no errors. 
        currentSchedule = new DeliverySchedule(postcode, deliveryDay, cost);
    }
    
    /**
     * Populates the form with current object's information 
     */
    private void populateForm() {
        deliveryDayChoiceBox.setValue(currentSchedule.getDeliveryDay());
        postcodeTextField.setText(currentSchedule.getPostcode()+"");
        costTextField.setText(currentSchedule.getDeliveryCost()+"");
        currentIndexTextField.setText(currentScheduleIndex+1+"");
        totalIndexTextField.setText(numberOfSchedules+"");
        postcodeTextField.setEditable(false); 
    }
    
    /**
     * Clears all input fields 
     */
    private void clear() {
        currentSchedule = new DeliverySchedule();
        deliverySchedules = new ArrayList<>();
        currentScheduleIndex = 0;
        numberOfSchedules = 1;
        
        deliveryDayChoiceBox.setValue("Monday");
        postcodeTextField.clear();
        costTextField.clear();
        currentIndexTextField.clear();
        totalIndexTextField.clear();
        
        postcodeTextField.setEditable(true);
    }
    
    /** 
     * Used when going too far forward or back in the cycles, creates an Alert. 
     */
    private void smallAlert() { 
        String message = """
                         No other delivery schedules in the list, please add some more to create a list.
                         Click 'New' then input desired information, and then click 'Add'"""; 
        Utility.alertGenerator("Notice, no other records", "No other records in DB", message, 2);
    }
    
    /**
     * Because error messages for exceptions are the same cookie cutter format, 
     * using this to create the alert and print the outcome to log too. Should 
     * make it cleaner. 
     * 
     * @param title     String for title and header of the Alert 
     * @param message   Body text for the Alert 
     * @param i         Identifier for which alert to output 
     */
    private void exceptionOutput(String title, String message, int i) { 
        System.out.println(message);
        Utility.alertGenerator(title, title, message, i);
    }
}