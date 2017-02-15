package npetzall.queue.api;

import java.nio.ByteBuffer;

public interface ByteBufferProvider {

    ByteBuffer byteBuffer();
    void close();

}
