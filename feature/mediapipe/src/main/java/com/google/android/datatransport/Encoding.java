package com.google.android.datatransport;

import androidx.annotation.NonNull;

public final class Encoding {

    public static Encoding of(@NonNull String name) {
        return new Encoding(name);
    }

    public String getName() {
        return "";
    }

    private Encoding(@NonNull String name) {

    }
}
