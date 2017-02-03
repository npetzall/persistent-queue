package npetzall.queue.api;

public class CapacityMismatchException extends RuntimeException {
    public CapacityMismatchException(String message) {
        super(message);
    }
}
