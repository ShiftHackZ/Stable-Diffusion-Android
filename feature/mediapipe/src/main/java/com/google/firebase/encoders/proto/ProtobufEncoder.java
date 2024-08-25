package com.google.firebase.encoders.proto;

import androidx.annotation.NonNull;

import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ValueEncoder;
import com.google.firebase.encoders.config.Configurator;
import com.google.firebase.encoders.config.EncoderConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class ProtobufEncoder {

    ProtobufEncoder(
            Map<Class<?>, ObjectEncoder<?>> objectEncoders,
            Map<Class<?>, ValueEncoder<?>> valueEncoders,
            ObjectEncoder<Object> fallbackEncoder) {
    }

    ProtobufEncoder() {}

    /** Encodes an arbitrary object and directly writes into the output stream. */
    public void encode(@NonNull Object value, @NonNull OutputStream outputStream) throws IOException {
    }

    /** Encodes an arbitrary object and returns it as a byte array. */
    @NonNull
    public byte[] encode(@NonNull Object value) {
        return new byte[0];
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements EncoderConfig<Builder> {

        @NonNull
        @Override
        public <U> Builder registerEncoder(
                @NonNull Class<U> type, @NonNull ObjectEncoder<? super U> encoder) {
            return this;
        }

        @NonNull
        @Override
        public <U> Builder registerEncoder(
                @NonNull Class<U> type, @NonNull ValueEncoder<? super U> encoder) {
            return this;
        }

        @NonNull
        public Builder registerFallbackEncoder(@NonNull ObjectEncoder<Object> fallbackEncoder) {
            return this;
        }

        @NonNull
        public Builder configureWith(@NonNull Configurator config) {
            config.configure(this);
            return this;
        }

        public ProtobufEncoder build() {
            return new ProtobufEncoder();
        }
    }
}
