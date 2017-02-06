package npetzall.queue

import npetzall.queue.api.Queue
import npetzall.queue.cache.OffHeapReadCache
import npetzall.queue.cache.OnHeapReadCache
import npetzall.queue.cache.ReadThroughReadCache
import npetzall.queue.codec.StringTranscoder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class QueueBuilderSpec extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    def "Build a queue with byteBuffer read cache"() {
        when:
        Queue<String> queue = PersistentQueueBuilder
                .queueFile(temporaryFolder.newFile("queueWithOffHeapReadCache"))
                .size("20m")
                .transcoder(new StringTranscoder())
                .offHeapReadCache()
                .build();
        then:
        queue != null
        ((PersistentQueue)queue).getReadCacheClass() == OffHeapReadCache.class
    }

    def "Build a queue with heap read cache"() {
        when:
        Queue<String> queue = PersistentQueueBuilder
                .queueFile(temporaryFolder.newFile("queueWithOnHeapReadCache"))
                .size("20m")
                .transcoder(new StringTranscoder())
                .onHeapReadCache()
                .build();
        then:
        queue != null
        ((PersistentQueue)queue).getReadCacheClass() == OnHeapReadCache.class
    }

    def "Build a queue without read cache"() {
        when:
        Queue<String> queue = PersistentQueueBuilder
                .queueFile(temporaryFolder.newFile("queueWithReadThroughReadCache"))
                .size("20m")
                .transcoder(new StringTranscoder())
                .build();
        then:
        queue != null
        ((PersistentQueue)queue).getReadCacheClass() == ReadThroughReadCache.class
    }
}
