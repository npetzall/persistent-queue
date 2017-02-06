package npetzall.queue.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class QueueFileHandler {

    public static final int WRITE_OFFSET_POSITION = 0;
    public static final int READ_OFFSET_POSITION = 4;

    public static final int DATA_OFFSET_POSITION = 8;

    protected volatile int writeOffset = 0;
    protected volatile int readOffset = 0;

    protected final MappedByteBuffer headerBuffer;
    protected final MappedByteBuffer dataBuffer;

    protected final int size;
    protected RandomAccessFile randomAccessFile;

    public QueueFileHandler(File queueFile, int size) throws IOException {
        this.size = size;
        randomAccessFile = new RandomAccessFile(queueFile, "rw");
        randomAccessFile.setLength(this.size);
        headerBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, DATA_OFFSET_POSITION);
        writeOffset = headerBuffer.getInt(WRITE_OFFSET_POSITION);
        readOffset = headerBuffer.getInt(READ_OFFSET_POSITION);
        dataBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, DATA_OFFSET_POSITION, (long)this.size - DATA_OFFSET_POSITION);
    }

    public int getSize() {
        return size - DATA_OFFSET_POSITION;
    }

    public int getWriteOffset() {
        return writeOffset;
    }

    public void setWriteOffset(int writeOffset) {
        this.writeOffset = writeOffset;
        headerBuffer.putInt(WRITE_OFFSET_POSITION, writeOffset);
    }

    public int getReadOffset() {
        return readOffset;
    }

    public void setReadOffset(int readOffset) {
        this.readOffset = readOffset;
        headerBuffer.putInt(READ_OFFSET_POSITION, readOffset);
    }

    public ByteBuffer getDataByteBuffer() {
        return dataBuffer;
    }

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
