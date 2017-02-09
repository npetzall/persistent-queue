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
@Warmup(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode({Mode.Throughput, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ByteBufferQueueEnqueueBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        public byte[] data = "This is a long line of text that should be stored as an element in the bytebufferqueue"
                .getBytes(StandardCharsets.UTF_8);
    }

    @State(Scope.Thread)
    public static class OnHeapByteBufferQueue {
        public ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(SizeHelper.parse("900m")));

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            byteBufferQueue.clear();
        }
    }

    @State(Scope.Thread)
    public static class OffHeapByteBufferQueue {
        public ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocateDirect(SizeHelper.parse("900m")));

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            byteBufferQueue.clear();
        }
    }

    @State(Scope.Thread)
    public static class MemoryMappedFile {
        public ByteBufferQueue byteBufferQueue;

        private final TemporaryFolder temporaryFolder = new TemporaryFolder();
        private final RandomAccessFile randomAccessFile;

        public MemoryMappedFile() {
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
            temporaryFolder.delete();
        }

    }

    @Benchmark
    public boolean onHeapByteBufferQueue(Data data, OnHeapByteBufferQueue byteBufferQueue) {
        return byteBufferQueue.byteBufferQueue.enqueue(data.data);
    }

    @Benchmark
    public boolean offHeapByteBufferQueue(Data data, OffHeapByteBufferQueue byteBufferQueue) {
        return byteBufferQueue.byteBufferQueue.enqueue(data.data);
    }

    @Benchmark
    public boolean MemoryMappedByteBufferQueue(Data data, MemoryMappedFile byteBufferQueue) {
        return byteBufferQueue.byteBufferQueue.enqueue(data.data);
    }


}
