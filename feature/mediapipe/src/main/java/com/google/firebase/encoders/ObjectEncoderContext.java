package com.google.firebase.encoders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public interface ObjectEncoderContext {

    @Deprecated
    @NonNull
    ObjectEncoderContext add(@NonNull String name, @Nullable Object obj) throws IOException;

    @Deprecated
    @NonNull
    ObjectEncoderContext add(@NonNull String name, double value) throws IOException;

    @Deprecated
    @NonNull
    ObjectEncoderContext add(@NonNull String name, int value) throws IOException;

    @Deprecated
    @NonNull
    ObjectEncoderContext add(@NonNull String name, long value) throws IOException;

    @Deprecated
    @NonNull
    ObjectEncoderContext add(@NonNull String name, boolean value) throws IOException;

    @NonNull
    ObjectEncoderContext add(@NonNull FieldDescriptor field, @Nullable Object obj) throws IOException;

    @NonNull
    ObjectEncoderContext add(@NonNull FieldDescriptor field, float value) throws IOException;

    @NonNull
    ObjectEncoderContext add(@NonNull FieldDescriptor field, double value) throws IOException;

    @NonNull
    ObjectEncoderContext add(@NonNull FieldDescriptor field, int value) throws IOException;

    @NonNull
    ObjectEncoderContext add(@NonNull FieldDescriptor field, long value) throws IOException;

    @NonNull
    ObjectEncoderContext add(@NonNull FieldDescriptor field, boolean value) throws IOException;

    @NonNull
    ObjectEncoderContext inline(@Nullable Object value) throws IOException;

    @NonNull
    ObjectEncoderContext nested(@NonNull String name) throws IOException;

    @NonNull
    ObjectEncoderContext nested(@NonNull FieldDescriptor field) throws IOException;
}
