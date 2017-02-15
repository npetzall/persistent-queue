package npetzall.queue.api;

public class Peek {

    private final byte[] data;
    private final int position;

    public Peek(int position, byte[] data) {
        this.position = position;
        this.data = data;
    }

    public int getPosition() {
        return position;
    }

    public byte[] getData() {
        return data;
    }

}
