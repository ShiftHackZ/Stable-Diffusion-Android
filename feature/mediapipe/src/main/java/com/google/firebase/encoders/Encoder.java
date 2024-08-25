package com.google.firebase.encoders;

import androidx.annotation.NonNull;

import java.io.IOException;

interface Encoder<TValue, TContext> {

    /** Encode {@code obj} using {@code TContext}. */
    void encode(@NonNull TValue obj, @NonNull TContext context) throws IOException;
}
