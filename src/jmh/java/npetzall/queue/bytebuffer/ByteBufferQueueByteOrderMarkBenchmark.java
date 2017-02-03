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
public class ByteBufferQueueByteOrderMarkBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        public final String[] strings = new String[]{"one", "two", "three", "four", "five", "six", "seven"};
    }

    @State(Scope.Thread)
    public static class BigEndianByteBufferOnHeapState {
        public final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        public BigEndianByteBufferOnHeapState() {
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        }

        @Setup(Level.Iteration)
        public void setup() {
            byteBuffer.clear();
        }
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
    public static class NativeByteBufferOnHeapState {
        public final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        public NativeByteBufferOnHeapState() {
            byteBuffer.order(ByteOrder.nativeOrder());
        }

        @Setup(Level.Iteration)
        public void setup() {
            byteBuffer.clear();
        }
    }

    @Benchmark
    public boolean orderBigEndianOnHeap(BenchmarkState benchmarkState, BigEndianByteBufferOnHeapState bigEndianByteBufferState) {
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(bigEndianByteBufferState.byteBuffer);
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
    public boolean orderLittleEndianOnHeap(BenchmarkState benchmarkState, LittleEndianByteBufferOnHeapState littleEndianByteBufferState) {
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(littleEndianByteBufferState.byteBuffer);
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
    public boolean orderNativeOnHeap(BenchmarkState benchmarkState, NativeByteBufferOnHeapState nativeByteBufferOnHeapState) {
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(nativeByteBufferOnHeapState.byteBuffer);
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
