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
public class ByteArrayQueueDequeueBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        public byte[] data = "This is a long line of text that should be stored as an element in the bytebufferqueue"
                .getBytes(StandardCharsets.UTF_8);
    }

    @State(Scope.Thread)
    public static class OnHeapQueue {
        public ByteArrayQueue queue = new ByteArrayQueue(new ByteBufferProviderDouble(ByteBuffer.allocate(SizeHelper.parse("900m"))), new PositionHolderDouble());

        public OnHeapQueue() {
            Data data = new Data();
            while (queue.getAvailableSpace() > data.data.length) {
                queue.enqueue(data.data);
            }
        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            queue.clear();
        }
    }

    @State(Scope.Thread)
    public static class OffHeapQueue {
        public ByteArrayQueue queue = new ByteArrayQueue(new ByteBufferProviderDouble(ByteBuffer.allocateDirect(SizeHelper.parse("900m"))), new PositionHolderDouble());

        public OffHeapQueue() {
            Data data = new Data();
            while (queue.getAvailableSpace() > data.data.length) {
                queue.enqueue(data.data);
            }
        }

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
    public byte[] onHeapQueue(OnHeapQueue queue) {
        return queue.queue.dequeue();
    }

    @Benchmark
    public byte[] offHeapQueue(OffHeapQueue queue) {
        return queue.queue.dequeue();
    }

    @Benchmark
    public byte[] MemoryMappedQueue(MemoryMappedQueue queue) {
        return queue.queue.dequeue();
    }
}
