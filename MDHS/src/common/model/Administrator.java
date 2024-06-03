package common.model;

/**
 * Administrator.java, extends Account.java. <p>
 * This class is the administrator part of the Account superclass. It is responsible 
 * for regulating what the admin person is able to do with the program. 
 * 
 * @author Brodie Lucht
 * @author Nicholas Paterno
 * @author Christopher Cox
 * @see Account 
 */
public class Administrator extends Account{
    private boolean isAdmin;
    
    /**
     * Empty constructor to create an empty Administration object 
     */
    public Administrator() {
        //No code
    }
    
    /**
     * No error handling because the label for "isAdmin" is a boolean, should 
     * not error out. 
     * 
     * @param isAdmin       Tag for if admin is true 
     * @param firstName     First name of the admin staff member 
     * @param lastName      Last name of the admin staff member 
     * @param emailAddress  Email address of the admin staff member in 
     *                      '[name]@[email].com' format 
     * @param password      Password of the user in bytes. Encrypted via public 
     *                      key, to be decrypted by server's private key 
     */
    public Administrator(boolean isAdmin, String firstName, String lastName, String emailAddress, byte[] password) {
        super(firstName, lastName, emailAddress, password);
        this.isAdmin = isAdmin;
    }
    
    /**
     * Used by the server to generate a complete Administration object. 
     * 
     * @param isAdmin       Tag for if admin is true 
     * @param accountId     Id of the admin staff as determined by DB 
     * @param firstName     First name of the admin staff 
     * @param lastName      Last name of the admin staff
     * @param emailAddress  Email address of the admin staff in 
     *                      '[name]@[email].com' format 
     * @param password      Password of the user in bytes. Encrypted via public 
     *                      key, to be decrypted by the server's private key 
     */
    public Administrator(boolean isAdmin, int accountId, String firstName, String lastName, String emailAddress, byte[] password) {
        super(accountId, firstName, lastName, emailAddress, password);
        this.isAdmin = isAdmin;
    }
    
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public boolean getIsAdmin() {
        return isAdmin;
    }
}
