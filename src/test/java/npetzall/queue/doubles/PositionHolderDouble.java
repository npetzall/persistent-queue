package npetzall.queue.doubles;

import npetzall.queue.api.PositionHolder;

import java.util.Arrays;

public class PositionHolderDouble implements PositionHolder {

    private volatile int writePosition;
    private volatile int readPosition;

    public PositionHolderDouble() {
        this(0,0);
    }

    public PositionHolderDouble(int writePosition, int readPosition) {
        this.writePosition = writePosition;
        this.readPosition = readPosition;
    }

    @Override
    public int writePosition() {
        return writePosition;
    }

    @Override
    public void writePosition(int writePosition) {
        this.writePosition = writePosition;
    }

    @Override
    public int readPosition() {
        return readPosition;
    }

    @Override
    public void readPosition(int readPosition) {
        this.readPosition = readPosition;
    }
}
