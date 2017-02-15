package npetzall.queue.api;

public interface PositionHolder {
    int writePosition();

    void writePosition(int writePosition);

    int readPosition();

    void readPosition(int readPosition);

    boolean writerOneCycleAhead();

    void writerOneCycleAhead(boolean isAhead);
}
