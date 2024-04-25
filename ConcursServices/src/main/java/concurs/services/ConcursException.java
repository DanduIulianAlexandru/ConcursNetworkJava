package concurs.services;

public class ConcursException extends Exception{
    public ConcursException(){

    }
    public ConcursException(String message) {
        super(message);
    }

    public ConcursException(String message, Throwable cause) {
        super(message, cause);
    }
}
