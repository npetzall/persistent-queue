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
public class ByteBufferReadBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        public byte[] data = "This is a long line of text that should be stored as an element in the bytebufferqueue"
                .getBytes(StandardCharsets.UTF_8);
    }

    @State(Scope.Thread)
    public static class OnHeapByteBuffer {

        public ByteBuffer byteBuffer = ByteBuffer.allocate(SizeHelper.parse("900m"));

        public byte[] readBuffer;

        public OnHeapByteBuffer() {
            Data data = new Data();
            readBuffer = new byte[data.data.length];
            while (byteBuffer.remaining() > data.data.length) {
                byteBuffer.put(data.data);
            }
            byteBuffer.position(0);
        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            byteBuffer.clear();
        }
    }

    @State(Scope.Thread)
    public static class OffHeapByteBuffer {
        public ByteBuffer byteBuffer = ByteBuffer.allocateDirect(SizeHelper.parse("900m"));

        public byte[] readBuffer;

        public OffHeapByteBuffer() {
            Data data = new Data();
            readBuffer = new byte[data.data.length];
            while (byteBuffer.remaining() > data.data.length) {
                byteBuffer.put(data.data);
            }
            byteBuffer.position(0);
        }

        @TearDown(Level.Iteration)
        public void tearDownIt() {
            byteBuffer.clear();
        }
    }

    @State(Scope.Thread)
    public static class MemoryMappedFile {
        public ByteBuffer byteBuffer;

        private final TemporaryFolder temporaryFolder = new TemporaryFolder();
        private final RandomAccessFile randomAccessFile;

        public byte[] readBuffer;

        public MemoryMappedFile() {
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
            Data data = new Data();
            readBuffer = new byte[data.data.length];
            while (byteBuffer.remaining() > data.data.length) {
                byteBuffer.put(data.data);
            }
            byteBuffer.position(0);
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
            temporaryFolder.delete();
        }

    }

    @Benchmark
    public ByteBuffer onHeap(OnHeapByteBuffer byteBuffer) {
        return byteBuffer.byteBuffer.get(byteBuffer.readBuffer);
    }

    @Benchmark
    public ByteBuffer offHeap(OffHeapByteBuffer byteBuffer) {
        return byteBuffer.byteBuffer.get(byteBuffer.readBuffer);
    }

    @Benchmark
    public ByteBuffer memoryMappedFile(MemoryMappedFile byteBuffer) {
        return byteBuffer.byteBuffer.get(byteBuffer.readBuffer);
    }
}
