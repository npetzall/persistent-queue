package npetzall.queue.bytearray;

import npetzall.queue.api.*;
import npetzall.queue.peek.DataPeeks;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ByteArrayQueue implements Queue<byte[]> {

    protected final ByteBufferProvider byteBufferProvider;
    protected final PositionHolder positionHolder;

    protected final ByteBuffer writeBuffer;

    protected final ByteBuffer readBuffer;

    protected volatile boolean availableSpaceIsToReader = false;

    public ByteArrayQueue(ByteBufferProvider byteBufferProvider, PositionHolder positionHolder) {
        this.byteBufferProvider = byteBufferProvider;
        this.positionHolder = positionHolder;
        writeBuffer = byteBufferProvider.byteBuffer().duplicate();
        readBuffer = byteBufferProvider.byteBuffer().asReadOnlyBuffer();
    }

    protected void syncWritePosition() {
        positionHolder.writePosition(writeBuffer.position());
    }

    protected void syncReadPosition() {
        positionHolder.readPosition(readBuffer.position());
    }

    @Override
    public boolean enqueue(byte[] element) {
        if (getAvailableSpace() < getLength(element)) {
            if (moveWriterToBeginningOfBuffer()) {
                if (getAvailableSpace()>= 4) {
                    writeBuffer.putInt(-1);
                }
                writeBuffer.position(0);
                syncWritePosition();
                availableSpaceIsToReader = true;
                 return enqueue(element);
            } else {
                return false;
            }
        } else {
            writeBuffer.putInt(element.length);
            writeBuffer.put(element);
            syncWritePosition();
            return true;
        }
    }

    protected int getAvailableSpace() {
        if (availableSpaceIsToReader) {
            return positionHolder.readPosition() - positionHolder.writePosition();
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

    @Override
    public byte[] dequeue() {
        if (positionHolder.readPosition() == positionHolder.writePosition()) {
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
        if(positionHolder.readPosition() == positionHolder.writePosition()){
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
            if(peek.getPosition() == positionHolder.readPosition()) {
                skip();
            }
        }
    }

    @Override
    public byte[] peek() {
        if (positionHolder.readPosition() == positionHolder.writePosition()) {
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
        if (readBuffer.position() == positionHolder.writePosition()) {
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

    @Override
    public void close() {
        byteBufferProvider.close();
    }
}
