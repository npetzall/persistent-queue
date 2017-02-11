package npetzall.queue.api;

/**
 * Created by Nosse on 2017-02-11.
 */
public interface PositionHolder {
    int writePosition();

    void writePosition(int writePosition);

    int readPosition();

    void readPosition(int readPosition);
}
