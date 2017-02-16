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
@Warmup(iterations = 10, time = 50, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 50, timeUnit = TimeUnit.MILLISECONDS)
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

        private PositionHolderDouble positionHolderDouble = new PositionHolderDouble();

        public ByteArrayQueue queue = new ByteArrayQueue(new ByteBufferProviderDouble(ByteBuffer.allocate(SizeHelper.parse("900m"))), positionHolderDouble);

        public OnHeapQueue() {
            Data data = new Data();
            boolean enqueued = queue.enqueue(data.data);
            while (enqueued) {
                enqueued = queue.enqueue(data.data);
            }
        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            positionHolderDouble.readPosition(0);
        }
    }

    @State(Scope.Thread)
    public static class OffHeapQueue {

        private PositionHolderDouble positionHolderDouble = new PositionHolderDouble();

        public ByteArrayQueue queue = new ByteArrayQueue(new ByteBufferProviderDouble(ByteBuffer.allocateDirect(SizeHelper.parse("900m"))), positionHolderDouble);

        public OffHeapQueue() {
            Data data = new Data();
            boolean enqueued = queue.enqueue(data.data);
            while (enqueued) {
                enqueued = queue.enqueue(data.data);
            }
        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            positionHolderDouble.readPosition(0);
        }
    }

    @State(Scope.Thread)
    public static class MemoryMappedQueue {
        public ByteArrayQueue queue;

        private final TemporaryFolder temporaryFolder = new TemporaryFolder();

        private QueueFileHandler queueFileHandler;

        public MemoryMappedQueue() {
            try {
                temporaryFolder.create();
                queueFileHandler = new QueueFileHandler(temporaryFolder.newFile(), SizeHelper.parse("900m"));
                queue = new ByteArrayQueue(queueFileHandler,queueFileHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Data data = new Data();
            boolean enqueued = queue.enqueue(data.data);
            while (enqueued) {
                enqueued = queue.enqueue(data.data);
            }

        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            queueFileHandler.readPosition(0);
        }


        @TearDown(Level.Trial)
        public void tearDownTrial() {
            queue.close();
            temporaryFolder.delete();
        }

    }

    @Benchmark
    public byte[] OnHeapQueue(OnHeapQueue queue) {
        byte[] data = queue.queue.deque();
        if (data.length == 0) {
            throw new RuntimeException("Wrong [r: " + queue.queue.readBuffer.position() + ", w: " + queue.queue.writeBuffer.position());
        }
        return queue.queue.deque();
    }

    @Benchmark
    public byte[] OffHeapQueue(OffHeapQueue queue) {
        byte[] data = queue.queue.deque();
        if (data.length == 0) {
            throw new RuntimeException("Wrong [r: " + queue.queue.readBuffer.position() + ", w: " + queue.queue.writeBuffer.position());
        }
        return queue.queue.deque();
    }

    @Benchmark
    public byte[] MemoryMappedQueue(MemoryMappedQueue queue) {
        byte[] data = queue.queue.deque();
        if (data.length == 0) {
            throw new RuntimeException("Wrong [r: " + queue.queue.readBuffer.position() + ", w: " + queue.queue.writeBuffer.position());
        }
        return queue.queue.deque();
    }
}
