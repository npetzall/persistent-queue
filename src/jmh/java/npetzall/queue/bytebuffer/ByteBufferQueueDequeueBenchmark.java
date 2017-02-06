package npetzall.queue.bytebuffer;

import npetzall.queue.helpers.SizeHelper;
import org.junit.rules.TemporaryFolder;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Fork(value = 2)
@Warmup(iterations = 10, batchSize = 10000000)
@Measurement(iterations = 20, batchSize = 10000000)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ByteBufferQueueDequeueBenchmark {
    @State(Scope.Benchmark)
    public static class Data {
        public final byte[] data = "This is a long line of text that should be stored as an element in the bytebufferqueue"
                .getBytes(StandardCharsets.UTF_8);
    }

    @State(Scope.Thread)
    public static class OnHeapByteBufferQueue {
        public final ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(SizeHelper.parse("900m")));

        public OnHeapByteBufferQueue() {
            Data data = new Data();
            while (byteBufferQueue.getAvailableSpace() > data.data.length) {
                byteBufferQueue.enqueue(data.data);
            }
        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            byteBufferQueue.clear();
        }
    }

    @State(Scope.Thread)
    public static class OffHeapByteBufferQueue {
        public final ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocateDirect(SizeHelper.parse("900m")));

        public OffHeapByteBufferQueue() {
            Data data = new Data();
            while (byteBufferQueue.getAvailableSpace() > data.data.length) {
                byteBufferQueue.enqueue(data.data);
            }
        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            byteBufferQueue.clear();
        }
    }

    @State(Scope.Thread)
    public static class MemoryMappedFile {
        public final ByteBufferQueue byteBufferQueue;

        private final RandomAccessFile randomAccessFile;

        public MemoryMappedFile() {
            TemporaryFolder temporaryFolder = new TemporaryFolder();
            RandomAccessFile tmpRandomAccessFile = null;
            ByteBuffer byteBuffer = null;
            try {
                temporaryFolder.create();
                tmpRandomAccessFile = new RandomAccessFile(temporaryFolder.newFile(), "rw");
                tmpRandomAccessFile.setLength(SizeHelper.parse("900m"));
                byteBuffer = tmpRandomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, SizeHelper.parse("900m"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            randomAccessFile = tmpRandomAccessFile;
            byteBufferQueue = new ByteBufferQueue(byteBuffer);
            Data data = new Data();
            while (byteBufferQueue.getAvailableSpace() > data.data.length) {
                byteBufferQueue.enqueue(data.data);
            }
        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            byteBufferQueue.clear();
        }

        @TearDown(Level.Trial)
        public void tearDownTrial() {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Benchmark
    public int onHeapByteBufferQueue(Data data, OnHeapByteBufferQueue byteBufferQueue) {
        return byteBufferQueue.byteBufferQueue.dequeue().length;
    }

    @Benchmark
    public int offHeapByteBufferQueue(Data data, OffHeapByteBufferQueue byteBufferQueue) {
        return byteBufferQueue.byteBufferQueue.dequeue().length;
    }

    @Benchmark
    public int MemoryMappedByteBufferQueue(Data data, MemoryMappedFile byteBufferQueue) {
        return byteBufferQueue.byteBufferQueue.dequeue().length;
    }
}
