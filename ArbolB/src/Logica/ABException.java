package Logica;



/**
 * Class ABException
 * @author tnguyen
 */
public class ABException extends java.lang.Exception
{
    public ABException() {
        super();
    }

    public ABException(String message) {
        super(message);
    }

    public ABException(String message, Throwable cause) {
        super(message, cause);
    }

    public ABException(Throwable cause) {
        super(cause);
    }
}
