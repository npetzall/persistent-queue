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
@Warmup(iterations = 10, batchSize = 10900000)
@Measurement(iterations = 20, batchSize = 10900000)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ByteBufferWriteBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        public final byte[] data = "This is a long line of text that should be stored as an element in the bytebufferqueue"
                .getBytes(StandardCharsets.UTF_8);
    }

    @State(Scope.Thread)
    public static class OnHeapByteBuffer {
        public final ByteBuffer byteBuffer = ByteBuffer.allocate(SizeHelper.parse("900m"));

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            byteBuffer.clear();
        }
    }

    @State(Scope.Thread)
    public static class OffHeapByteBuffer {
        public final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(SizeHelper.parse("900m"));

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            byteBuffer.clear();
        }
    }

    @State(Scope.Thread)
    public static class MemoryMappedFile {
        public final ByteBuffer byteBuffer;

        private final RandomAccessFile randomAccessFile;

        public MemoryMappedFile() {
            TemporaryFolder temporaryFolder = new TemporaryFolder();
            RandomAccessFile tmpRandomAccessFile = null;
            ByteBuffer tmpByteBuffer = null;
            try {
                temporaryFolder.create();
                tmpRandomAccessFile = new RandomAccessFile(temporaryFolder.newFile(), "rw");
                tmpRandomAccessFile.setLength(SizeHelper.parse("900m"));
                tmpByteBuffer = tmpRandomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, SizeHelper.parse("900m"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            randomAccessFile = tmpRandomAccessFile;
            byteBuffer = tmpByteBuffer;
        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            byteBuffer.clear();
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
    public int onHeap(Data data, OnHeapByteBuffer byteBuffer) {
        byteBuffer.byteBuffer.put(data.data);
        return byteBuffer.byteBuffer.position();
    }

    @Benchmark
    public int offHeap(Data data, OffHeapByteBuffer byteBuffer) {
        byteBuffer.byteBuffer.put(data.data);
        return byteBuffer.byteBuffer.position();
    }

    @Benchmark
    public int memoryMappedFile(Data data, MemoryMappedFile byteBuffer) {
        byteBuffer.byteBuffer.put(data.data);
        return byteBuffer.byteBuffer.position();
    }
}
