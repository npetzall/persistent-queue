package npetzall.queue;

import npetzall.queue.codec.StringTranscoder;
import org.junit.rules.TemporaryFolder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(value = 2)
@Warmup(iterations = 10)
@Measurement(iterations = 20)
@BenchmarkMode({Mode.Throughput, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class PersistentQueueEnqueueDequeueBenchmark {

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
                    .size("20m")
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
                    .size("20m")
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
                    .size("20m")
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
    public void onHeapCache(Data data, OnHeapCache queueHolder, Blackhole blackhole) {
        blackhole.consume(queueHolder.queue.enqueue(data.data));
        blackhole.consume(queueHolder.queue.dequeue());
    }

    @Benchmark
    public void offHeapCache(Data data, OffHeapCache queueHolder, Blackhole blackhole) {
        blackhole.consume(queueHolder.queue.enqueue(data.data));
        blackhole.consume(queueHolder.queue.dequeue());
    }

    @Benchmark
    public void readThroughCache(Data data, ReadThoughCache queueHolder, Blackhole blackhole) {
        blackhole.consume(queueHolder.queue.enqueue(data.data));
        blackhole.consume(queueHolder.queue.dequeue());
    }
}
