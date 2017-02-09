package npetzall.queue;

import npetzall.queue.codec.StringTranscoder;
import org.junit.rules.TemporaryFolder;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(value = 2)
@Warmup(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode({Mode.Throughput, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PersistentQueueEnqueueBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        public String data = "This is a long line of text that should be stored as an element in the bytebufferqueue";
    }

    @State(Scope.Thread)
    public static class OnHeapCache {
        private final TemporaryFolder temporaryFolder = new TemporaryFolder();
        public PersistentQueue<String> queue;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            temporaryFolder.create();
            queue = (PersistentQueue<String>) PersistentQueueBuilder.<String>queueFile(temporaryFolder.newFile("onHeapCache.que"))
                    .onHeapReadCache()
                    .size("900m")
                    .transcoder(new StringTranscoder())
                    .build();
        }

        @Setup(Level.Iteration)
        public void setupIt() throws IOException {
            queue.clear();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            queue.close();
            temporaryFolder.delete();
        }
    }

    @State(Scope.Thread)
    public static class OffHeapCache {
        private final TemporaryFolder temporaryFolder = new TemporaryFolder();
        public PersistentQueue<String> queue;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            temporaryFolder.create();
            queue = (PersistentQueue<String>) PersistentQueueBuilder.<String>queueFile(temporaryFolder.newFile("offHeapCache.que"))
                    .offHeapReadCache()
                    .size("900m")
                    .transcoder(new StringTranscoder())
                    .build();
        }

        @Setup(Level.Iteration)
        public void setupIt() throws IOException {
            queue.clear();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            queue.close();
            temporaryFolder.delete();
        }
    }

    @State(Scope.Thread)
    public static class ReadThoughCache {
        private final TemporaryFolder temporaryFolder = new TemporaryFolder();
        public PersistentQueue<String> queue;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            temporaryFolder.create();
            queue = (PersistentQueue<String>) PersistentQueueBuilder.<String>queueFile(temporaryFolder.newFile("readeThroughCache.que"))
                    .size("900m")
                    .transcoder(new StringTranscoder())
                    .build();
        }

        @Setup(Level.Iteration)
        public void setupIt() throws IOException {
            queue.clear();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            queue.close();
            temporaryFolder.delete();
        }
    }

    @Benchmark
    public boolean onHeapCache(Data data, OnHeapCache queueHolder) {
        return queueHolder.queue.enqueue(data.data);
    }

    @Benchmark
    public boolean offHeapCache(Data data, OffHeapCache queueHolder) {
        return queueHolder.queue.enqueue(data.data);
    }

    @Benchmark
    public boolean readThroughCache(Data data, ReadThoughCache queueHolder) {
        return queueHolder.queue.enqueue(data.data);
    }

}
