package npetzall.queue.doubles;

import npetzall.queue.api.ByteBufferProvider;

import java.nio.ByteBuffer;

public class ByteBufferProviderDouble implements ByteBufferProvider {

    private ByteBuffer byteBuffer;

    public ByteBufferProviderDouble(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public ByteBuffer byteBuffer() {
        return byteBuffer;
    }

    @Override
    public void close() {
        byteBuffer = null;
    }
}
