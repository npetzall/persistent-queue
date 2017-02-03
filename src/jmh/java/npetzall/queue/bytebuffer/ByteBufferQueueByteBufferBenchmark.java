package npetzall.queue.bytebuffer;

import org.openjdk.jmh.annotations.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Fork(value = 2)
@Warmup(iterations = 3)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ByteBufferQueueByteBufferBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        public final String[] strings = new String[]{"one", "two", "three", "four", "five", "six", "seven"};
    }
    @State(Scope.Thread)
    public static class LittleEndianByteBufferOnHeapState {
        public final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        public LittleEndianByteBufferOnHeapState() {
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        @Setup(Level.Iteration)
        public void setup() {
            byteBuffer.clear();
        }
    }

    @State(Scope.Thread)
    public static class LittleEndianByteBufferOffHeapState {
        public final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

        public LittleEndianByteBufferOffHeapState() {
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        @Setup(Level.Iteration)
        public void setup() {
            byteBuffer.clear();
        }
    }

    @Benchmark
    public boolean onHeapByteBuffer(BenchmarkState benchmarkState, LittleEndianByteBufferOnHeapState littleEndianByteBufferOnHeapState) {
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(littleEndianByteBufferOnHeapState.byteBuffer);
        int added = 0;
        for(String str : benchmarkState.strings) {
            byteBufferQueue.enqueue(str.getBytes(StandardCharsets.UTF_8));
            added++;
        }
        int retrieved = 0;
        while(byteBufferQueue.dequeue().length > 0) {
            retrieved++;
        }
        return retrieved == added;
    }

    @Benchmark
    public boolean offHeapByteBuffer(BenchmarkState benchmarkState, LittleEndianByteBufferOffHeapState littleEndianByteBufferOffHeapState) {
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(littleEndianByteBufferOffHeapState.byteBuffer);
        int added = 0;
        for(String str : benchmarkState.strings) {
            byteBufferQueue.enqueue(str.getBytes(StandardCharsets.UTF_8));
            added++;
        }
        int retrieved = 0;
        while(byteBufferQueue.dequeue().length > 0) {
            retrieved++;
        }
        return retrieved == added;
    }

}
