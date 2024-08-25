package com.google.firebase.encoders.json;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.encoders.EncodingException;
import com.google.firebase.encoders.FieldDescriptor;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ObjectEncoderContext;
import com.google.firebase.encoders.ValueEncoder;
import com.google.firebase.encoders.ValueEncoderContext;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class JsonValueObjectEncoderContext implements ObjectEncoderContext, ValueEncoderContext {

    JsonValueObjectEncoderContext(
            @NonNull Writer writer,
            @NonNull Map<Class<?>, ObjectEncoder<?>> objectEncoders,
            @NonNull Map<Class<?>, ValueEncoder<?>> valueEncoders,
            ObjectEncoder<Object> fallbackEncoder,
            boolean ignoreNullValues) {

    }

    JsonValueObjectEncoderContext() {}


    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(@NonNull String name, @Nullable Object o)
            throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(@NonNull String name, double value) throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(@NonNull String name, int value) throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(@NonNull String name, long value) throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(@NonNull String name, boolean value) throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull FieldDescriptor field, @Nullable Object obj)
            throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull FieldDescriptor field, float value) throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull FieldDescriptor field, double value) throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull FieldDescriptor field, int value) throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull FieldDescriptor field, long value) throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public ObjectEncoderContext add(@NonNull FieldDescriptor field, boolean value)
            throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public ObjectEncoderContext inline(@Nullable Object value) throws IOException {
        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public ObjectEncoderContext nested(@NonNull String name) throws IOException {

        return new JsonValueObjectEncoderContext();
    }

    @NonNull
    @Override
    public ObjectEncoderContext nested(@NonNull FieldDescriptor field) throws IOException {
        return nested(field.getName());
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(@Nullable String value) throws IOException {

        return this;
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(float value) throws IOException {


        return this;
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(double value) throws IOException {

        return this;
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(int value) throws IOException {

        return this;
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(long value) throws IOException {

        return this;
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(boolean value) throws IOException {

        return this;
    }

    @NonNull
    @Override
    public JsonValueObjectEncoderContext add(@Nullable byte[] bytes) throws IOException {

        return this;
    }

    @NonNull
    JsonValueObjectEncoderContext add(@Nullable Object o, boolean inline) throws IOException {

        return new JsonValueObjectEncoderContext();
    }

    JsonValueObjectEncoderContext doEncode(ObjectEncoder<Object> encoder, Object o, boolean inline)
            throws IOException {
        return this;
    }

    private boolean cannotBeInline(Object value) {
        return true;
    }

    void close() throws IOException {

    }

    private void maybeUnNest() throws IOException {

    }

    private JsonValueObjectEncoderContext internalAdd(@NonNull String name, @Nullable Object o)
            throws IOException, EncodingException {
        return add(o, false);
    }

    private JsonValueObjectEncoderContext internalAddIgnoreNullValues(
            @NonNull String name, @Nullable Object o) throws IOException, EncodingException {
        return add(o, false);
    }
}
