
package common;

import java.sql.Date;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utility {
    /**
     * Check if a string is empty
     * @param str String to check
     * @return TRUE - if empty / FALSE - contains content
     */
    public static boolean isEmpty(String str) {
        if (str.equals(""))
            return true;
        else
            return false;    
    }
    
    /**
     * Check if a string is valid
     * @param str String to check
     * @param minLength Minimum length allowed
     * @param maxLength Maximum length allowed
     * @param canContainNumbers Is allowed to have integer values 
     * @return TRUE - valid string / FALSE - invalid string
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
     * Check if a string is an email (contains '@')
     * @param str String to check 
     * @return TRUE - valid email / FALSE - invalid email 
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
     * Check if a string is numeric
     * @param str String to check
     * @return TRUE - is numeric / FALSE - not numeric
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
     * Check if Number is between 2 values
     * Number is parsed as long as to check it is less than max int
     * @param num long to check
     * @param lower Lower limit
     * @param upper Upper limit
     * @return TRUE - if between lower & upper values / FALSE - if outside
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
     * Check if string is a valid option from an array
     * @param str string to check
     * @param options array of options
     * @return TRUE - In array / FALSE - not in array
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
     * 
     * @param e
     * @param arr
     * @return 
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
    
    private static boolean isValid12HourFormat(String time) {
        // Regular expression to match the 12-hour format (HH:MMam/pm)
        String regex = "(0?[1-9]|1[0-2]):[0-5][0-9]\\s?[AaPp][Mm]";
        return time.matches(regex);
    }
    
    private static boolean isValid24HourFormat(String time) {
        // Regular expression to match the 24-hour format (HH:MM)
        String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        return time.matches(regex);
    }
    
    public static boolean isValidTime(String time) {
        return isValid12HourFormat(time) || isValid24HourFormat(time);
    }
    
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

}
