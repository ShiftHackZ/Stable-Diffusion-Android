package com.google.firebase.encoders;

import androidx.annotation.NonNull;

public final class EncodingException extends RuntimeException {

    public EncodingException(@NonNull String message) {
        super(message);
    }

    public EncodingException(@NonNull String message, @NonNull Exception cause) {
        super(message, cause);
    }
}
