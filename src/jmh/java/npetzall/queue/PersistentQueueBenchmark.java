package npetzall.queue;

import npetzall.queue.codec.StringTranscoder;
import org.junit.rules.TemporaryFolder;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(value = 2)
@Warmup(iterations = 3)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PersistentQueueBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        public final String[] strings = new String[]{"one", "two", "three", "four", "five", "six", "seven"};
    }

    @State(Scope.Thread)
    public static class OnHeapCache {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
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
        }
    }

    @State(Scope.Thread)
    public static class OffHeapCache {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
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
        }
    }

    @State(Scope.Thread)
    public static class ReadThoughCache {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
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
        }
    }

    //@Benchmark
    public boolean onHeapCache(Data data, OnHeapCache queueHolder) {
        PersistentQueue<String> queue = queueHolder.queue;
        int added = 0;
        for (String str : data.strings) {
            queue.enqueue(str);
            added++;
        }
        int retrieved = 0;
        for (int i = 0; i < added; i++) {
            if (notNullOrEmpty(queue.dequeue())) {
                retrieved++;
            }
        }
        return retrieved == added;
    }

    //@Benchmark
    public boolean offHeapCache(Data data, OffHeapCache queueHolder) {
        PersistentQueue<String> queue = queueHolder.queue;
        int added = 0;
        for (String str : data.strings) {
            queue.enqueue(str);
            added++;
        }
        int retrieved = 0;
        for (int i = 0; i < added; i++) {
            if (notNullOrEmpty(queue.dequeue())) {
                retrieved++;
            }
        }
        return retrieved == added;
    }

    //@Benchmark
    public boolean readThroughCache(Data data, ReadThoughCache queueHolder) {
        PersistentQueue<String> queue = queueHolder.queue;
        int added = 0;
        for (String str : data.strings) {
            queue.enqueue(str);
            added++;
        }
        int retrieved = 0;
        for (int i = 0; i < added; i++) {
            if (notNullOrEmpty(queue.dequeue())) {
                retrieved++;
            }
        }
        return retrieved == added;
    }

    private boolean notNullOrEmpty(String string) {
        return string != null && !string.isEmpty();
    }
}
