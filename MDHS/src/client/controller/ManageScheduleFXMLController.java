
package client.controller;

import client.MDHSClient;
import client.Session;
import common.UserInputException;
import common.Utility;
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
    
    /**
     * Upon changing the scene to the current, perfoem the following steps 
     */
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
     * 
     * @param event 
     */
    @FXML
    private void addButtonHandler(ActionEvent event) {
        //Necessitates the requirement of flag set to 1, othetwise error is thrown 
        //and the method ends early. Avoids accidental doubling up 
        String errorMessage = errorHandleButton(3);
        if (!errorMessage.equals("")) { 
            alertError(errorMessage); 
            return;
        }
        
        
        try {
            recordDeliverySchedule();
            session.objOut.writeObject("RecordSchedule");
            session.objOut.writeObject(currentSchedule);
            String message = (String) session.objIn.readObject(); 
            
            if (message.equalsIgnoreCase("AddScheduleSuccess")) { 
                alertInformation("Schedule with postcode: " + currentSchedule.getPostcode() + 
                        " was successfully added");
            } else { 
                alertError("Schedule with postcode: " + currentSchedule.getPostcode() + 
                        " was unsuccessfully added"); 
            }
        } catch (UserInputException e) { 
            String message = "Exception occured in creating the Product, message: " + e.getMessage(); 
            System.out.println(message); 
            Utility.alertGenerator("Input mismatch", "Input mismatch as occured!", message, 1); 
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Exception while adding schedule: " + ex.getMessage());
            alertError("Exception occured while adding schedule: " + ex.getMessage()); 
        }
        clear();
        loadSchedules();
        populateForm();  
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void updateButtonHandler(ActionEvent event) {
        String errorMessage = errorHandleButton(2);
        if (!errorMessage.equals("")) { 
            alertError(errorMessage); 
            return;
        }
        
        
        try {
            recordDeliverySchedule(); 
            session.objOut.writeObject("RecordSchedule");
            session.objOut.writeObject(currentSchedule);
            String message = (String) session.objIn.readObject();
            
            if (message.equalsIgnoreCase("EditScheduleSuccess")) { 
                alertInformation("Schedule with postcode: " + currentSchedule.getPostcode() + 
                        " was successfully updated");
            } else { 
                alertError("Schedule with postcode: " + currentSchedule.getPostcode() + 
                        " was unsuccessfully updated"); 
            }
            
        } catch (UserInputException e) { 
            
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Exception while updating schedule: " + ex.getMessage());
            alertError("Exception occured while updating schedule: " + ex.getMessage()); 
        }
        
        clear();
        loadSchedules();
        populateForm();  
    }
    
    /**
     * Takes current information and deletes the associated entry. Takes the whole 
     * object for ease
     * @param event 
     */
    @FXML
    private void deleteButtonHandler(ActionEvent event) {
        String errorMessage = errorHandleButton(1);
        if (!errorMessage.equals("")) { 
            alertError(errorMessage); 
            return;
        }
        recordDeliverySchedule();
        
        try {
            session.objOut.writeObject("DeleteSchedule");
            session.objOut.writeObject(currentSchedule);
            String message = (String) session.objIn.readObject(); 
            
            if (message.equalsIgnoreCase("DeleteScheduleSuccess")) { 
                alertInformation("Schedule was successfully deleted");
            } else { 
                alertError("Schedule was unsuccessfully deleted");
            }
            
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Exception while deleting schedule: " + ex.getMessage());
        }
        
        //Reset the scene 
        clear();
        loadSchedules();
        populateForm();  
    }
    
    /**
     * Sets text fields to null, and also sets a flag to make it so Update and 
     * Delete methods won't continue, ensuring no messiness with that 
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
            newScheduleFlag = 0; 
            session.objOut.writeObject("FullDeliverySchedule");
            deliverySchedules = (ArrayList<DeliverySchedule>) session.objIn.readObject();
            
            numberOfSchedules = deliverySchedules.size();
            if (numberOfSchedules > 0) {
                currentScheduleIndex = 0;
                currentSchedule = deliverySchedules.get(currentScheduleIndex);
            } else {
                currentSchedule = new DeliverySchedule();
                currentScheduleIndex = -1; //Should resolve as 0 then on input 
                numberOfSchedules = 0; //Redundancy, ensures is 0 
                alertInformation("No schedules have been booked");
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Exception while loading Delivery Schedule: " + ex.getMessage());
            alertError("Error occured while loading Schedule list: " + ex.getMessage()); 
        }
    }
    
    /**
     * Takes current fields and creates an object with there information. Performs 
     * error handling 
     * @throws UserInputException 
     */
    private void recordDeliverySchedule() throws UserInputException {
        String deliveryDay = deliveryDayChoiceBox.getValue().toLowerCase();
        String cost = costTextField.getText().trim(); 
        String postcode = postcodeTextField.getText().trim(); 
        
        //Noticed that sometimes it gets parsed as null, so force as Monday if the case 
        //Normally it will be 
        if (deliveryDay == null) { 
            deliveryDay = "Monday"; 
        }
        
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
        
        //Why was it false? 
        postcodeTextField.setEditable(true);
    }
    
    /** 
     * Creates an error Alert. Saves having to create a new Alert object in each 
     * catch{} block
     * @param message 
     */
    private void alertError(String message) { 
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait(); 
    }
    
    /** 
     * Creates an information Alert. Saves having to create a new one each time 
     * @param message 
     */
    private void alertInformation(String message) { 
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
    
    /** 
     * Used when going too far forward or back in the cycles, creates an Alert. 
     */
    private void smallAlert() { 
        String message = "No other delivery schedules in the list, please add some more to create a list.\n"
                + "Click 'New' then input desired information, and then click 'Add'"; 
        Utility.alertGenerator("Notice, no other records", "No other records in DB", message, 2);
    }
    
    /** 
     * 
     * @param i
     * @return 
     */
    private String errorHandleButton(int i) { 
        String message = ""; 
        
        //Deletion case 
        if (newScheduleFlag == 1 && i == 1) 
            message += "Please search for a Schedule for deletion first.\n";
        
        //Editing case
        if (newScheduleFlag == 1 && i == 2) 
            message += "Please search for a Schedule for editing first.\n";
        
        //Adding case (If haven't pressed 'New', then throw error message) 
        if (newScheduleFlag == 0 && i == 3) 
            message += "Please click the 'New' button before adding a new Schedule.\n";
        
        return message; 
    }
    
}

/* Old newButtonHandler() method 
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
*/

    /** 
     * Unsure if it works like this, but it kinda works, it just won't get the 
     * proper state (response statement won't work) 
     * @param message
     * @return 
     */
/*
    private boolean alertConfirmation(String message) { 
        boolean state = false; 
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.NO); 
        Optional<ButtonType> response = alert.showAndWait();
        if (response.get().equals(ButtonType.OK)) { 
            state = true; 
        }
        return state; 
    }
*/