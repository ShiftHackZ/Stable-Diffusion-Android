package com.google.firebase.encoders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public interface ValueEncoderContext {

    /** Adds {@code value} as a primitive encoded value. */
    @NonNull
    ValueEncoderContext add(@Nullable String value) throws IOException;

    /** Adds {@code value} as a primitive encoded value. */
    @NonNull
    ValueEncoderContext add(float value) throws IOException;

    /** Adds {@code value} as a primitive encoded value. */
    @NonNull
    ValueEncoderContext add(double value) throws IOException;

    /** Adds {@code value} as a primitive encoded value. */
    @NonNull
    ValueEncoderContext add(int value) throws IOException;

    /** Adds {@code value} as a primitive encoded value. */
    @NonNull
    ValueEncoderContext add(long value) throws IOException;

    /** Adds {@code value} as a primitive encoded value. */
    @NonNull
    ValueEncoderContext add(boolean value) throws IOException;

    /** Adds {@code value} as a encoded array of bytes. */
    @NonNull
    ValueEncoderContext add(@NonNull byte[] bytes) throws IOException;
}
