package com.google.firebase.encoders;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Writer;

public interface DataEncoder {

    /** Encodes {@code obj} into {@code writer}. */
    void encode(@NonNull Object obj, @NonNull Writer writer) throws IOException;

    /** Returns the string-encoded representation of {@code obj}. */
    @NonNull
    String encode(@NonNull Object obj);
}
