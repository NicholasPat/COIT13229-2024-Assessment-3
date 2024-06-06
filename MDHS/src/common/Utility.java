package common;

import common.model.OrderItem;
import common.model.Product;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

/** 
 * This class is used to validate certain things. Common reference methods are 
 * placed here as such. 
 * 
 * @author Brodie Lucht 
 * @author Nicholas Paterno
 */
public class Utility {
    /**
     * Check if a string is empty. 
     * 
     * @param str   String to check
     * @return      TRUE - if empty / FALSE - contains content
     */
    public static boolean isEmpty(String str) {
        return str.equals("");    
    }
    
    /** 
     * Generates an Alert and displays it for the user. Passing information to 
     * generate the right Alert for the situation. 
     * 
     * @param title     Title for the Alert window popup 
     * @param header    Message to be output in the header of the Alert 
     * @param message   Message to be output in the body of the Alert
     * @param i         1 - ERROR alert type / 2 - INFORMATION Alert type
     */
    public static void alertGenerator(String title, String header, String message, int i) { 
        Alert alert = null; 
        switch (i) {
            case 1 -> alert = new Alert(Alert.AlertType.ERROR);
            case 2 -> alert = new Alert(Alert.AlertType.INFORMATION);
            default -> {
            }
        }
        alert.setTitle(title); 
        alert.setHeaderText(header);
        alert.setContentText(message); 
        alert.showAndWait(); 
    }
    
    /**
     * Check if a string is valid. 
     * 
     * @param str       String to check
     * @param minLength Minimum length allowed
     * @param maxLength Maximum length allowed
     * @param canContainNumbers Is allowed to have integer values 
     * @return          TRUE - valid string / FALSE - invalid string
    */
    public static boolean isValidString(String str, int minLength, int maxLength, boolean canContainNumbers) {
        boolean valid = true;
        if (str.length() < minLength)
            valid = false;
        if (str.length() > maxLength)
            valid = false;
        
	for (int i = 0; i < str.length(); i++) {
    	    if (Character.isDigit(str.charAt(i))&& ! canContainNumbers) // cannot contain numbers
		valid = false;
	}
        return valid;
    }
    
    /** 
     * Method to test if the phone number matches 10 digit requirement. 
     * 
     * @param numberToTest Number which is to be tested as a phone number
     * @return             TRUE - Valid phone number / FALSE - Invalid phone number
     */
    public static boolean isValidPhoneNumber(String numberToTest) { 
        boolean result = true; 
        String regexStr = "^[0-9]{10}$"; 
        if (!numberToTest.matches(regexStr)) 
            result = false;
        return result; 
    }
    
    /** 
     * Check if a string is an email (contains '@'). 
     * 
     * @param str   String to check 
     * @return      TRUE - valid email / FALSE - invalid email 
     */
    public static boolean isValidEmail(String str) {      
	boolean valid = false;
        for (int i = 0; i < str.length(); i++) {
    	    if (str.charAt(i) == '@') // must contain @ symbol
		valid = true;
	}
        return valid;
    }
    
   /**
     * Check if a string is numeric. 
     * 
     * @param str   String to check
     * @return      TRUE - is numeric / FALSE - not numeric
     */
    public static boolean isStringNumeric(String str) {
        boolean valid = true;
	for (int i = 0; i < str.length(); i++) {
    	    if (!Character.isDigit(str.charAt(i)))
		valid = false;
	}
        return valid;
    }
    
    /**
     * Check if Number is between 2 values. <p>
     * Number is parsed as long as to check it is less than max int. 
     * 
     * @param num   long to check
     * @param lower Lower limit
     * @param upper Upper limit
     * @return      TRUE - if between lower & upper values / FALSE - if outside
     */
    public static boolean isValidInt(long num, int lower, int upper) {
        boolean valid = true;
        if (num < lower)
            valid = false;
        else if (num > upper)
            valid = false;
        
        return valid;
    }
    
    /**
     * Check if string is a valid option from an array. 
     * 
     * @param str       string to check
     * @param options   array of options
     * @return          TRUE - In array / FALSE - Not in array
     */
    public static boolean isValidOption(String str, String[] options) {
        boolean valid = false;
        for (String o : options) {
            if (str.equals(o)) {
                valid = true;
                break;
            }
        }
        return valid;        
    }
    
    /** 
     * Checks where in array the desired String is. Determining index (position). 
     * 
     * @param e     String to check array for 
     * @param arr   Array for checking where String is 
     * @return      Index of where the String is 
     */
    public static int indexOf(String e, String[] arr) {
        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(e)) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    /** 
     * Checks if the time value given is valid in 12 hour format. 
     * 
     * @param time  Value to check 
     * @return      TRUE - Pass / FALSE - Fail
     */
    private static boolean isValid12HourFormat(String time) {
        // Regular expression to match the 12-hour format (HH:MMam/pm)
        String regex = "(0?[1-9]|1[0-2]):[0-5][0-9]\\s?[AaPp][Mm]";
        return time.matches(regex);
    }
    
    /** 
     * Checks if the time value given is valid in 24 hour format. 
     * 
     * @param time  Value to check 
     * @return      TRUE - Pass / FALSE - Fail
     */
    private static boolean isValid24HourFormat(String time) {
        // Regular expression to match the 24-hour format (HH:MM)
        String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        return time.matches(regex);
    }
    
    /** 
     * Determines if the time matches the time format desired. 
     * 
     * @param time  Time to test if valid formatting 
     * @return      Whether it passes the test 
     */
    public static boolean isValidTime(String time) {
        return isValid12HourFormat(time) || isValid24HourFormat(time);
    }
    
    /** 
     * Converts 12 hour time to 24 hours. 
     * 
     * @param time  Time value to be converted 
     * @return      Returns the time in new 24 hour format 
     * @throws UserInputException   If there is a mis-input from the user. 
     */
    public static String convertTo24HourFormat(String time) throws UserInputException{
        if (isValid12HourFormat(time)) {
             // Convert 12-hour format to 24-hour format
            SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
            try {
                // reformat time from 12hr to 24hr
                java.util.Date utilDate = inputFormat.parse(time);
                return outputFormat.format(utilDate);
            } catch (ParseException e) {
                throw new UserInputException("Invalid Time");
            }
        }
        return time;
    }
    
    /** 
     * This is meant to check each of the Order Items against the products. This 
     * is to determine if there is a missing product, and if there is, remove it 
     * from the list. Won't commit to the server, but the Customer can then do that.
     * Or admin can do it via observing in Order list. 
     * 
     * @param orderItemList List of OrderItems 
     * @param productList   List of Products to compare with OrderItems 
     * @return list         List of OrderItems that were to be removed. 
     */
    public static List<OrderItem> checkIfProductExists(List<OrderItem> orderItemList, List<Product> productList) {
        List<OrderItem> list = new ArrayList<>(); //Index counter         
        if (!orderItemList.isEmpty()) {
            
            for (int n=0; n < orderItemList.size(); n++) {
                int a = 0; 
                
                for (int i=0; i < productList.size(); i++) {    
                    
                    if (orderItemList.get(n).getProductId() == productList.get(i).getProductId()) { 
                        a++; 
                        break; 
                    }
                }
                if (a == 0) {list.add(orderItemList.get(n));}
            }
        }
        
        //If list isn't empty, then remove items from orderItems
        if (!list.isEmpty()) {orderItemList.removeAll(list);}
        
        return orderItemList; 
    }
}
