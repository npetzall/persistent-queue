package npetzall.queue.api;

public class CapacityMismatchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CapacityMismatchException(String message) {
        super(message);
    }
}
