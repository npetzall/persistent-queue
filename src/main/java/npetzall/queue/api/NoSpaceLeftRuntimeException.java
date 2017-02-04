package npetzall.queue.api;

public class NoSpaceLeftRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoSpaceLeftRuntimeException(String message) {
        super(message);
    }
}
