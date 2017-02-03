package npetzall.queue.codec

import spock.lang.Specification

import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class StringEncoderSpec extends Specification {

    def "Encode 'hello' utf-8"() {
        setup:
        String input = "hello"
        StringEncoder stringEncoder = new StringEncoder(StandardCharsets.UTF_8)

        when:
        byte[] actual = stringEncoder.encode("hello")

        then:
        assertThat(actual).containsExactly("hello".getBytes(StandardCharsets.UTF_8))
    }
}
