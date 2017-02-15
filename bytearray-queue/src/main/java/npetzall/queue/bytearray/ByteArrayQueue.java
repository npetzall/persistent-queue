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

    protected final AvailableSpaceSupplier availableSpaceRemaining;
    protected final AvailableSpaceSupplier availableSpaceToReader;

    protected AvailableSpaceSupplier availableSpace;

    interface AvailableSpaceSupplier {
        int getAvailableSpace();
    }

    class AvailableSpaceRemaning implements AvailableSpaceSupplier {

        private final ByteBuffer byteBuffer;

        public AvailableSpaceRemaning(ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
        }

        @Override
        public int getAvailableSpace() {
            return byteBuffer.remaining();
        }
    }

    class AvailableSpaceToReader implements AvailableSpaceSupplier {

        private final PositionHolder positionHolder;

        public AvailableSpaceToReader(PositionHolder positionHolder) {
            this.positionHolder = positionHolder;
        }

        @Override
        public int getAvailableSpace() {
            return positionHolder.readPosition() - positionHolder.writePosition();
        }
    }

    public ByteArrayQueue(ByteBufferProvider byteBufferProvider, PositionHolder positionHolder) {
        this.byteBufferProvider = byteBufferProvider;
        this.positionHolder = positionHolder;
        writeBuffer = byteBufferProvider.byteBuffer().duplicate();
        readBuffer = byteBufferProvider.byteBuffer().asReadOnlyBuffer();
        availableSpaceRemaining = new AvailableSpaceRemaning(writeBuffer);
        availableSpaceToReader = new AvailableSpaceToReader(positionHolder);
        availableSpace = positionHolder.writerOneCycleAhead() ? availableSpaceToReader : availableSpaceRemaining;
    }

    protected void syncWritePosition() {
        positionHolder.writePosition(writeBuffer.position());
    }

    protected void syncReadPosition() {
        positionHolder.readPosition(readBuffer.position());
    }

    @Override
    public boolean enqueue(byte[] element) {
        int storageSize = getLength(element);
        int availableSpace = getAvailableSpace();
        if (availableSpace < storageSize) {
            if (canCycle() && positionHolder.readPosition() > storageSize) {
                cycleWriter(availableSpace >= 4);
            } else {
                return false;
            }
        }
        writeElement(element);
        return true;
    }

    protected int getAvailableSpace() {
        return availableSpace.getAvailableSpace();
    }

    protected int getLength(byte[] element) {
        return element.length + 4;
    }

    protected boolean canCycle() {
        return !positionHolder.writerOneCycleAhead();
    }

    protected void cycleWriter(boolean writeMarker) {
        if (writeMarker) {
            writeBuffer.putInt(-1);
        }
        writeBuffer.position(0);
        positionHolder.writerOneCycleAhead(true);
        availableSpace = availableSpaceToReader;
    }

    protected void writeElement(byte[] element) {
        writeBuffer.putInt(element.length).put(element);
        syncWritePosition();
    }

    @Override
    public byte[] deque() {
        if (noMoreElements()) {
            return new byte[0];
        }
        if (readBuffer.remaining() >= 4) {
            int length = readBuffer.getInt();
            if (length > 0) {
                return readElement(length);
            }
        }
        cycleReader();
        return deque();
    }

    private byte[] readElement(int length) {
        byte[] element = new byte[length];
        readBuffer.get(element);
        syncReadPosition();
        return element;
    }

    @Override
    public void skip() {
        if (noMoreElements()) {
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
        cycleReader();
        skip();
    }

    private void cycleReader() {
        readBuffer.position(0);
        syncReadPosition();
        positionHolder.writerOneCycleAhead(false);
        availableSpace = availableSpaceRemaining;
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
        if (noMoreElements()) {
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
        if (noMoreElements()) {
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

    private boolean noMoreElements() {
        return readBuffer.position() == positionHolder.writePosition()
                && !positionHolder.writerOneCycleAhead();
    }

    @Override
    public void clear() {
        readBuffer.position(0);
        syncReadPosition();
        writeBuffer.position(0);
        syncWritePosition();
        positionHolder.writerOneCycleAhead(false);
    }

    @Override
    public void close() {
        byteBufferProvider.close();
    }
}
