package services;

public class InscriereException extends Exception {
    public InscriereException() {}

    public InscriereException(String message) {
        super(message);
    }

    public InscriereException(String message, Throwable cause) {
        super(message, cause);
    }
}
