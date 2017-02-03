package npetzall.queue.api;

public class NoSpaceLeftRuntimeException extends RuntimeException {
    public NoSpaceLeftRuntimeException(String message) {
        super(message);
    }
}
