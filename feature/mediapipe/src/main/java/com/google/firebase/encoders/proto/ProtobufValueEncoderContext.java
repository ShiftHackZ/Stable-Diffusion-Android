package com.google.firebase.encoders.proto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.encoders.FieldDescriptor;
import com.google.firebase.encoders.ValueEncoderContext;

import java.io.IOException;

public class ProtobufValueEncoderContext implements ValueEncoderContext {

    ProtobufValueEncoderContext(ProtobufDataEncoderContext objEncoderCtx) {

    }

    ProtobufValueEncoderContext() {}

    void resetContext(FieldDescriptor field, boolean skipDefault) {

    }

    @NonNull
    @Override
    public ValueEncoderContext add(@Nullable String value) throws IOException {
        return this;
    }

    @NonNull
    @Override
    public ValueEncoderContext add(float value) throws IOException {
        return this;
    }

    @NonNull
    @Override
    public ValueEncoderContext add(double value) throws IOException {
        return this;
    }

    @NonNull
    @Override
    public ValueEncoderContext add(int value) throws IOException {
        return this;
    }

    @NonNull
    @Override
    public ValueEncoderContext add(long value) throws IOException {
        return this;
    }

    @NonNull
    @Override
    public ValueEncoderContext add(boolean value) throws IOException {
        return this;
    }

    @NonNull
    @Override
    public ValueEncoderContext add(@NonNull byte[] bytes) throws IOException {
        return this;
    }
}
