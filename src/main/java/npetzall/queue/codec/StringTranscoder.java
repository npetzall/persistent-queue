package npetzall.queue.codec;

import npetzall.queue.api.Transcoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringTranscoder implements Transcoder<String> {

    private final StringEncoder encoder;
    private final StringDecoder decoder;

    public StringTranscoder(){
        encoder = new StringEncoder(StandardCharsets.UTF_8);
        decoder = new StringDecoder(StandardCharsets.UTF_8);
    }

    public StringTranscoder(Charset charset) {
        encoder = new StringEncoder(charset);
        decoder = new StringDecoder(charset);
    }

    @Override
    public String decode(byte[] data) {
        return decoder.decode(data);
    }

    @Override
    public byte[] encode(String element) {
        return encoder.encode(element);
    }
}
