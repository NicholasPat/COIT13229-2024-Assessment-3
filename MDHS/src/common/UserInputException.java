
package common;

public class UserInputException extends RuntimeException {

    /**
     * Creates a new instance of <code>UserInputException</code> without detail message.
     */
    public UserInputException() {
        this ("Erros: No details provided");
    }
    /**
     * Constructs an instance of <code>UserInputException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UserInputException (String msg) {
        super (msg);
    }
    /**
     * Constructs an instance of <code>UserInputException</code> with the specified detail message and cause.
     *
     * @param msg the detail message.
     * @param cause the cause.
     */
    public UserInputException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
