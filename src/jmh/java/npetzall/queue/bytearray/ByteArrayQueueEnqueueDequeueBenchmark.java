package npetzall.queue.bytearray;

import npetzall.queue.doubles.ByteBufferProviderDouble;
import npetzall.queue.doubles.PositionHolderDouble;
import npetzall.queue.file.QueueFileHandler;
import npetzall.queue.helpers.SizeHelper;
import org.junit.rules.TemporaryFolder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Fork(value = 2)
@Warmup(iterations = 10)
@Measurement(iterations = 20)
@BenchmarkMode({Mode.Throughput, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ByteArrayQueueEnqueueDequeueBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        public byte[] data = "This is a long line of text that should be stored as an element in the bytebufferqueue"
                .getBytes(StandardCharsets.UTF_8);
    }

    @State(Scope.Thread)
    public static class OnHeapQueue {
        public ByteArrayQueue queue = new ByteArrayQueue(new ByteBufferProviderDouble(ByteBuffer.allocate(SizeHelper.parse("900m"))), new PositionHolderDouble());

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            queue.clear();
        }
    }

    @State(Scope.Thread)
    public static class OffHeapQueue {
        public ByteArrayQueue queue = new ByteArrayQueue(new ByteBufferProviderDouble(ByteBuffer.allocateDirect(SizeHelper.parse("900m"))), new PositionHolderDouble());

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            queue.clear();
        }
    }

    @State(Scope.Thread)
    public static class MemoryMappedQueue {
        public ByteArrayQueue queue;

        private final TemporaryFolder temporaryFolder = new TemporaryFolder();

        public MemoryMappedQueue() {
            try {
                temporaryFolder.create();
                QueueFileHandler queueFileHandler = new QueueFileHandler(temporaryFolder.newFile(), SizeHelper.parse("900m"));
                queue = new ByteArrayQueue(queueFileHandler,queueFileHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Data data = new Data();
            while (queue.getAvailableSpace() > data.data.length) {
                queue.enqueue(data.data);
            }
        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            queue.clear();
        }

        @TearDown(Level.Trial)
        public void tearDownTrial() {
            queue.close();
            temporaryFolder.delete();
        }

    }

    @Benchmark
    public void onHeapByteBufferQueue(Data data, OnHeapQueue queue, Blackhole blackhole) {
        blackhole.consume(queue.queue.enqueue(data.data));
        blackhole.consume(queue.queue.dequeue());
    }

    @Benchmark
    public void offHeapByteBufferQueue(Data data, OffHeapQueue queue, Blackhole blackhole) {
        blackhole.consume(queue.queue.enqueue(data.data));
        blackhole.consume(queue.queue.dequeue());
    }

    @Benchmark
    public void MemoryMappedByteBufferQueue(Data data, MemoryMappedQueue queue, Blackhole blackhole) {
        blackhole.consume(queue.queue.enqueue(data.data));
        blackhole.consume(queue.queue.dequeue());
    }
}
