package learn.BasicAuthentication.exception.custom;

public class AdminDoesNotExistException extends Exception{
    public AdminDoesNotExistException(String message) {
        super(message);
    }
}
