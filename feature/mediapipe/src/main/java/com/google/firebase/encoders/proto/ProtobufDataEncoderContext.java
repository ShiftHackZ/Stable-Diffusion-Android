package com.google.firebase.encoders.proto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.encoders.EncodingException;
import com.google.firebase.encoders.FieldDescriptor;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ObjectEncoderContext;
import com.google.firebase.encoders.ValueEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class ProtobufDataEncoderContext implements ObjectEncoderContext  {

    ProtobufDataEncoderContext(
            OutputStream output,
            Map<Class<?>, ObjectEncoder<?>> objectEncoders,
            Map<Class<?>, ValueEncoder<?>> valueEncoders,
            ObjectEncoder<Object> fallbackEncoder) {

    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull String name, @Nullable Object obj) throws IOException {
        return add(FieldDescriptor.of(name), obj);
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull String name, double value) throws IOException {
        return add(FieldDescriptor.of(name), value);
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull String name, int value) throws IOException {
        return add(FieldDescriptor.of(name), value);
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull String name, long value) throws IOException {
        return add(FieldDescriptor.of(name), value);
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull String name, boolean value) throws IOException {
        return add(FieldDescriptor.of(name), value);
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull FieldDescriptor field, @Nullable Object obj)
            throws IOException {
        return add(field, obj, true);
    }

    ObjectEncoderContext add(
            @NonNull FieldDescriptor field, @Nullable Object obj, boolean skipDefault)
            throws IOException {

        return this;
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull FieldDescriptor field, double value) throws IOException {
        return add(field, value, true);
    }

    ObjectEncoderContext add(@NonNull FieldDescriptor field, double value, boolean skipDefault)
            throws IOException {
        return this;
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull FieldDescriptor field, float value) throws IOException {

        return add(field, value, true);
    }

    ObjectEncoderContext add(@NonNull FieldDescriptor field, float value, boolean skipDefault)
            throws IOException {
        return this;
    }

    @NonNull
    @Override
    public ProtobufDataEncoderContext add(@NonNull FieldDescriptor field, int value)
            throws IOException {
        return add(field, value, true);
    }

    ProtobufDataEncoderContext add(@NonNull FieldDescriptor field, int value, boolean skipDefault)
            throws IOException {
        return this;
    }

    @NonNull
    @Override
    public ProtobufDataEncoderContext add(@NonNull FieldDescriptor field, long value)
            throws IOException {
        return add(field, value, true);
    }

    ProtobufDataEncoderContext add(@NonNull FieldDescriptor field, long value, boolean skipDefault)
            throws IOException {
        return this;
    }

    @NonNull
    @Override
    public ProtobufDataEncoderContext add(@NonNull FieldDescriptor field, boolean value)
            throws IOException {
        return add(field, value, true);
    }

    ProtobufDataEncoderContext add(@NonNull FieldDescriptor field, boolean value, boolean skipDefault)
            throws IOException {
        return add(field, value ? 1 : 0, skipDefault);
    }

    @NonNull
    @Override
    public ObjectEncoderContext inline(@Nullable Object value) throws IOException {
        return encode(value);
    }

    ProtobufDataEncoderContext encode(@Nullable Object value) throws IOException {
        throw new EncodingException("No encoder for " + value.getClass());
    }

    @NonNull
    @Override
    public ObjectEncoderContext nested(@NonNull String name) throws IOException {
        return nested(FieldDescriptor.of(name));
    }

    @NonNull
    @Override
    public ObjectEncoderContext nested(@NonNull FieldDescriptor field) throws IOException {
        throw new EncodingException("nested() is not implemented for protobuf encoding.");
    }

}
