package npetzall.queue.bytearray;

import npetzall.queue.doubles.ByteBufferProviderDouble;
import npetzall.queue.doubles.PositionHolderDouble;
import npetzall.queue.file.QueueFileHandler;
import npetzall.queue.helpers.SizeHelper;
import org.junit.rules.TemporaryFolder;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Fork(value = 2)
@Warmup(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode({Mode.Throughput, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ByteArrayQueueEnqueueBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        public byte[] data = "This is a long line of text that should be stored as an element in the bytebufferqueue"
                .getBytes(StandardCharsets.UTF_8);
    }

    @State(Scope.Thread)
    public static class OnHeapQueue {
        public ByteArrayQueue queue = new ByteArrayQueue(new ByteBufferProviderDouble(ByteBuffer.allocate(SizeHelper.parse("900m"))),new PositionHolderDouble());

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
    public boolean onHeapQueue(Data data, OnHeapQueue queue) {
        boolean enqueued = queue.queue.enqueue(data.data);
        if (!enqueued) {
            throw new RuntimeException("Wrong");
        }
        return enqueued;
    }

    @Benchmark
    public boolean offHeapQueue(Data data, OffHeapQueue queue) {
        boolean enqueued = queue.queue.enqueue(data.data);
        if (!enqueued) {
            throw new RuntimeException("Wrong");
        }
        return enqueued;
    }

    @Benchmark
    public boolean MemoryMappedQueue(Data data, MemoryMappedQueue queue) {
        boolean enqueued = queue.queue.enqueue(data.data);
        if (!enqueued) {
            throw new RuntimeException("Wrong");
        }
        return enqueued;
    }


}
