package npetzall.queue.bytebuffer;

import npetzall.queue.api.*;
import npetzall.queue.peek.DataPeeks;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ByteBufferQueue implements Queue<byte[]> {

    protected final ByteBuffer writeBuffer;
    protected volatile int writePosition;

    protected final ByteBuffer readBuffer;
    protected volatile int readPosition;

    protected volatile boolean availableSpaceIsToReader = false;

    public ByteBufferQueue(ByteBuffer byteBuffer) {
        writeBuffer = byteBuffer.duplicate();
        readBuffer = byteBuffer.asReadOnlyBuffer();
        writePosition = 0;
        readPosition = 0;
    }

    public ByteBufferQueue(ByteBuffer byteBuffer, int writeIndex, int readIndex) {
        writeBuffer = byteBuffer.duplicate();
        readBuffer = byteBuffer.asReadOnlyBuffer();
        writeBuffer.position(writeIndex);
        syncWritePosition();
        readBuffer.position(readIndex);
        syncReadPosition();
    }

    protected void syncWritePosition() {
        writePosition = writeBuffer.position();
    }

    protected void syncReadPosition() {
        readPosition = readBuffer.position();
    }

    @Override
    public void enqueue(byte[] element) {
        if (getAvailableSpace() < getLength(element)) {
            if (moveWriterToBeginningOfBuffer()) {
                if (getAvailableSpace()>= 4) {
                    writeBuffer.putInt(-1);
                }
                writeBuffer.position(0);
                syncWritePosition();
                availableSpaceIsToReader = true;
                enqueue(element);
            } else {
                throw new NoSpaceLeftRuntimeException("Out of space");
            }
        } else {
            writeBuffer.putInt(element.length);
            writeBuffer.put(element);
            syncWritePosition();
        }
    }

    protected int getAvailableSpace() {
        if (availableSpaceIsToReader) {
            return readPosition - writePosition;
        } else {
            return writeBuffer.remaining();
        }
    }

    protected int getLength(byte[] element) {
        return element.length + 4;
    }

    protected boolean moveWriterToBeginningOfBuffer() {
        return !availableSpaceIsToReader;
    }

    public int getWriteIndex() {
        return writePosition;
    }

    @Override
    public byte[] dequeue() {
        if (readPosition == writePosition) {
            return new byte[0];
        }
        if (readBuffer.remaining() >= 4) {
            int length = readBuffer.getInt();
            if (length > 0) {
                byte[] element = new byte[length];
                readBuffer.get(element);
                syncReadPosition();
                return element;
            }
        }
        readBuffer.position(0);
        syncReadPosition();
        availableSpaceIsToReader = false;
        return dequeue();
    }

    @Override
    public void skip() {
        if(readPosition == writePosition){
            return;
        }
        if(readBuffer.remaining() >= 4) {
            int length = readBuffer.getInt();
            if (length > 0) {
                readBuffer.position(readBuffer.position() + length);
                syncReadPosition();
                return;
            }
        }
        readBuffer.position(0);
        syncReadPosition();
        availableSpaceIsToReader = false;
        skip();

    }

    @Override
    public void skip(Peeks<?> peeks) {
        Iterator<Peek> peekIterator = peeks.peekIterator();
        while(peekIterator.hasNext()) {
            Peek peek = peekIterator.next();
            if(peek.getPosition() == readPosition) {
                skip();
            }
        }
    }

    public int getReadIndex() {
        return readPosition;
    }

    @Override
    public byte[] peek() {
        if (readPosition == writePosition) {
            return new byte[0];
        }
        int position = readBuffer.position();
        byte[] element = peekElement().getData();
        readBuffer.position(position);
        return element;
    }

    @Override
    public DataPeeks peek(int maxNumber) {
        int position = readBuffer.position();
        List<Peek> peeks = new ArrayList<>();
        for(int i = 0; i < maxNumber; i++) {
            Peek peek = peekElement();
            if (peek == null) {
                break;
            }
            peeks.add(peek);
        }
        readBuffer.position(position);
        return new DataPeeks(peeks);
    }

    protected Peek peekElement() {
        if (readBuffer.position() == writePosition) {
            return null;
        }
        if (readBuffer.remaining() >= 4) {
            int elementPosition = readBuffer.position();
            int length = readBuffer.getInt();
            if (length > 0) {
                byte[] data = new byte[length];
                readBuffer.get(data);
                return new Peek(elementPosition, data);
            }
        }
        readBuffer.position(0);
        return peekElement();
    }

    @Override
    public void clear() {
        readBuffer.position(0);
        syncReadPosition();
        writeBuffer.position(0);
        syncWritePosition();
        availableSpaceIsToReader = false;
    }

    public ByteBufferQueue copyTo(ByteBuffer destinationBuffer) {
        if (destinationBuffer.capacity() != readBuffer.capacity()) {
            throw new CapacityMismatchException("Capacity mismatch src is '" + readBuffer.capacity() + "' and dst is '"+ destinationBuffer.capacity() + "'");
        }
        readBuffer.position(0);
        destinationBuffer.put(readBuffer);
        readBuffer.position(readPosition);
        return new ByteBufferQueue(destinationBuffer, writePosition, readPosition);
    }

    @Override
    public void close() {
        //no-op;
    }
}
