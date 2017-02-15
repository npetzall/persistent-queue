package npetzall.queue.object.codec

import spock.lang.Specification

import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class StringTranscoderSpec extends Specification {

    def "Encode"() {
        setup:
        StringTranscoder stringTranscoder = new StringTranscoder(StandardCharsets.UTF_8)

        when:
        byte[] actual = stringTranscoder.encode("hello")

        then:
        assertThat(actual).containsExactly("hello".getBytes(StandardCharsets.UTF_8))
    }

    def "Decode"() {
        setup:
        StringTranscoder stringTranscoder = new StringTranscoder(StandardCharsets.UTF_8)

        when:
        String actual = stringTranscoder.decode("hello".getBytes(StandardCharsets.UTF_8))

        then:
        actual == "hello"
    }
}
