package com.google.firebase.encoders.json;

import androidx.annotation.NonNull;

import com.google.firebase.encoders.DataEncoder;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ValueEncoder;
import com.google.firebase.encoders.config.Configurator;
import com.google.firebase.encoders.config.EncoderConfig;

import java.io.IOException;
import java.io.Writer;

public class JsonDataEncoderBuilder implements EncoderConfig<JsonDataEncoderBuilder>  {

    public JsonDataEncoderBuilder() {
    }

    @NonNull
    @Override
    public <T> JsonDataEncoderBuilder registerEncoder(
            @NonNull Class<T> clazz, @NonNull ObjectEncoder<? super T> objectEncoder) {

        return this;
    }

    @NonNull
    @Override
    public <T> JsonDataEncoderBuilder registerEncoder(
            @NonNull Class<T> clazz, @NonNull ValueEncoder<? super T> encoder) {

        return this;
    }

    /** Encoder used if no encoders are found among explicitly registered ones. */
    @NonNull
    public JsonDataEncoderBuilder registerFallbackEncoder(
            @NonNull ObjectEncoder<Object> fallbackEncoder) {
        return this;
    }

    @NonNull
    public JsonDataEncoderBuilder configureWith(@NonNull Configurator config) {
        return this;
    }

    @NonNull
    public JsonDataEncoderBuilder ignoreNullValues(boolean ignore) {
        return this;
    }

    @NonNull
    public DataEncoder build() {
        return new DataEncoder() {
            @Override
            public void encode(@NonNull Object o, @NonNull Writer writer) throws IOException {

            }

            @Override
            public String encode(@NonNull Object o) {
                return "";
            }
        };
    }
}
