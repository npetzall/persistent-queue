package npetzall.queue.file;

import npetzall.queue.api.ByteBufferProvider;
import npetzall.queue.api.PositionHolder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class QueueFileHandler implements ByteBufferProvider, PositionHolder {

    public static final int WRITE_POSITION_INDEX = 0;
    public static final int READ_POSITION_INDEX = 4;
    public static final int WRITER_ONE_CYCLE_AHEAD_INDEX = 8;
    private static final byte BYTE_FALSE = 0;
    private static final byte BYTE_TRUE = 1;

    public static final int DATA_OFFSET = 9;

    protected volatile int writePosition = 0;
    protected volatile int readPosition = 0;

    protected volatile boolean writerOneCycleAhead = false;

    protected final MappedByteBuffer headerBuffer;
    protected final MappedByteBuffer dataBuffer;

    protected RandomAccessFile randomAccessFile;

    public QueueFileHandler(File queueFile, int size) throws IOException {
        randomAccessFile = new RandomAccessFile(queueFile, "rw");
        randomAccessFile.setLength(size);
        headerBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, DATA_OFFSET);
        writePosition = headerBuffer.getInt(WRITE_POSITION_INDEX);
        readPosition = headerBuffer.getInt(READ_POSITION_INDEX);
        writerOneCycleAhead = headerBuffer.get(WRITER_ONE_CYCLE_AHEAD_INDEX) == BYTE_TRUE;
        dataBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, DATA_OFFSET, (long)size - DATA_OFFSET);
    }

    @Override
    public int writePosition() {
        return writePosition;
    }

    @Override
    public void writePosition(int writePosition) {
        this.writePosition = writePosition;
        headerBuffer.putInt(WRITE_POSITION_INDEX, writePosition);
    }

    @Override
    public int readPosition() {
        return readPosition;
    }

    @Override
    public void readPosition(int readPosition) {
        this.readPosition = readPosition;
        headerBuffer.putInt(READ_POSITION_INDEX, readPosition);
    }

    @Override
    public boolean writerOneCycleAhead() {
        return writerOneCycleAhead;
    }

    @Override
    public void writerOneCycleAhead(boolean isAhead) {
        writerOneCycleAhead = isAhead;
        headerBuffer.put(WRITER_ONE_CYCLE_AHEAD_INDEX, isAhead ? BYTE_TRUE : BYTE_FALSE);
    }

    @Override
    public ByteBuffer byteBuffer() {
        return dataBuffer;
    }

    @Override
    public void close() {
        headerBuffer.force();
        dataBuffer.force();
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
