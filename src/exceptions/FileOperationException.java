package exceptions;

public class FileOperationException extends RuntimeException {
    public FileOperationException(String message) {
        super(message);
    }
}
