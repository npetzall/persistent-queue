package npetzall.queue.codec

import spock.lang.Specification

import java.nio.charset.StandardCharsets

class StringDecoderSpec extends Specification {

    def "Decode bytes of 'hello' utf-8"() {
        setup:
        byte[] input = "hello".getBytes(StandardCharsets.UTF_8)
        String expected = "hello"
        StringDecoder stringDecoder = new StringDecoder(StandardCharsets.UTF_8)

        when:
        String actual = stringDecoder.decode(input)

        then:
        actual == "hello"
    }

    def "Decode bytes of 'hello' us_ascii"() {
        setup:
        byte[] input = "hello".getBytes(StandardCharsets.US_ASCII)
        String expected = "hello"
        StringDecoder stringDecoder = new StringDecoder(StandardCharsets.US_ASCII)

        when:
        String actual = stringDecoder.decode(input)

        then:
        actual == "hello"
    }

}
