package com.simpsite.simpsiteservers.service;

import com.simpsite.simpsiteservers.Codec.Base62Codec;
import com.simpsite.simpsiteservers.Codec.Codec;
import com.simpsite.simpsiteservers.Codec.HashCodec;
import com.simpsite.simpsiteservers.Codec.RandomCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CodecFactory {
    private final Base62Codec base62Codec;
    private final HashCodec hashCodec;
    private final RandomCodec randomCodec;

    public Codec createCodec(String codecType) {
        switch (codecType) {
            case "Base62":
                return base62Codec;
            case "Hash":
                return hashCodec;
            case "Random":
                return randomCodec;
            default:
                throw new IllegalArgumentException("Invalid codec type: " + codecType);
        }
    }
}
